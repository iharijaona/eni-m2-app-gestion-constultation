package edu.mg.eni.m2.patient.consultation.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.joanzapata.iconify.widget.IconTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.mg.eni.m2.patient.consultation.R;
import edu.mg.eni.m2.patient.consultation.adapter.MedecinAdapter;
import edu.mg.eni.m2.patient.consultation.helpers.DBHelpers;
import edu.mg.eni.m2.patient.consultation.helpers.FileHelpers;
import edu.mg.eni.m2.patient.consultation.model.Medecin;
import edu.mg.eni.m2.patient.consultation.service.MedecinService;

public class MedecinFragment extends Fragment implements MedecinAdapter.MedecinInterface, PullRefreshLayout.OnRefreshListener {
    private DBHelpers dbHelpers;
    public AppCompatEditText editTextName;
    public AppCompatEditText editTextNum;
    public AppCompatEditText editTextTaux;
    public IconTextView icon;
    public Boolean isUpdate = false;
    private PullRefreshLayout refreshableLayout;
    public MedecinAdapter medecinAdapter;
    public MedecinService medecinService;
    private RelativeLayout noResults;
    public RecyclerView recyclerView;
    public TextView textEmpty;
    private ArrayList<Medecin> allMedecin = new ArrayList<>();

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.dbHelpers = new DBHelpers(getContext());
        this.medecinService = new MedecinService(this.dbHelpers.getDb());
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_medecin, viewGroup, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.refreshableLayout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        this.refreshableLayout.setRefreshStyle(0);
        this.refreshableLayout.setOnRefreshListener(this);
        Typeface createFromAsset = Typeface.createFromAsset(getContext().getAssets(), "OratorStd.otf");
        this.icon = (IconTextView) view.findViewById(R.id.empty_icon);
        this.icon.setTypeface(createFromAsset);
        this.textEmpty = (TextView) view.findViewById(R.id.empty_result);
        this.noResults = (RelativeLayout) view.findViewById(R.id.empty_view);
        this.medecinAdapter = new MedecinAdapter(allMedecin, getContext(), this);
        setupView(view);
        loadMeds();
    }

    public void setupView(View view) {
        setHasOptionsMenu(true);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.cell_medecin);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(1);
        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.medecinAdapter);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_medecin, menu);
        ((SearchView) menu.findItem(R.id.app_bar_search).getActionView()).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            public boolean onQueryTextChange(String str) {
                MedecinFragment.this.medecinAdapter.getFilter().filter(str.trim());
                return false;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.app_bar_add) {
            modalDialog(null);
        }
        else if (menuItem.getItemId() == R.id.app_bar_export) {
            this.exportMedecinsToPdf(FileHelpers.getAppPath(this.getContext()) + this.getResources().getString(R.string.app_name) +"-Medecins-"+String.valueOf(new Date().getTime())+".pdf");
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void loadMeds() {
        List<Medecin> medecinTmpList = this.medecinService.fetchAll();
        allMedecin.clear();
        if (medecinTmpList != null && !medecinTmpList.isEmpty()) {
            allMedecin.addAll(medecinTmpList);
            this.noResults.setVisibility(View.INVISIBLE);
        }
        else{
            this.noResults.setVisibility(View.VISIBLE);
        }

        this.medecinAdapter.notifyDataSetChanged();
        this.recyclerView.invalidate();

        this.refreshableLayout.setRefreshing(false);

    }

    public void modalDialog(Medecin currentMedecin) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View inflate = getLayoutInflater().inflate(R.layout.single_dialog_medecin, (ViewGroup) null);
        dialogBuilder.setView(inflate);

        final AlertDialog createDialog = dialogBuilder.create();
        this.editTextNum = (AppCompatEditText) inflate.findViewById(R.id.txtFieldNum);
        this.editTextName = (AppCompatEditText) inflate.findViewById(R.id.txtFieldNom);
        this.editTextTaux = (AppCompatEditText) inflate.findViewById(R.id.txtFieldTaux);
        AppCompatButton appCompatButton = (AppCompatButton) inflate.findViewById(R.id.app_button_add);

        this.isUpdate = false;

        if (currentMedecin != null && currentMedecin.getIdMed() != null) {
            this.editTextNum.setText(currentMedecin.getIdMed());
            this.editTextNum.setEnabled(false);
            this.editTextName.setText(currentMedecin.getNom());
            this.editTextTaux.setText(String.valueOf(currentMedecin.getTaux()));
            appCompatButton.setText("Modifier");
            this.isUpdate = true;
        }
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Validate form data
                if (MedecinFragment.this.editTextName.getText().toString().trim().isEmpty() || MedecinFragment.this.editTextNum.getText().toString().trim().isEmpty() || MedecinFragment.this.editTextTaux.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MedecinFragment.this.getContext(), "Veuillez compléter le formulaire", Toast.LENGTH_LONG).show();
                    return;
                }

                try{
                    Integer.parseInt(MedecinFragment.this.editTextNum.getText().toString().trim());
                }
                catch (Exception e){
                    Toast.makeText(MedecinFragment.this.getContext(), "Veuillez entrer un taux horaire valide", Toast.LENGTH_LONG).show();
                    return;
                }

                // Create new medecin
                if (!MedecinFragment.this.isUpdate) {
                    Medecin newMedecin = new Medecin();
                    newMedecin.setIdMed(MedecinFragment.this.editTextNum.getText().toString().trim());
                    newMedecin.setTaux(Integer.parseInt(MedecinFragment.this.editTextTaux.getText().toString().trim()));
                    newMedecin.setNom(MedecinFragment.this.editTextName.getText().toString());

                    Medecin existingMedecin = MedecinFragment.this.medecinService.fetchById(MedecinFragment.this.editTextNum.getText().toString());
                    if (existingMedecin != null && existingMedecin.getIdMed() != null) {
                        Context context = MedecinFragment.this.getContext();
                        Toast.makeText(context, "Le medecin "+existingMedecin.getIdMed()+" existe déjà au nom de " + existingMedecin.getNom(), Toast.LENGTH_LONG).show();
                    } else if (MedecinFragment.this.medecinService.add(newMedecin)) {
                        MedecinFragment.this.loadMeds();
                        createDialog.dismiss();
                    }
                }
                // Update existing medecin
                else {
                    Medecin updatedMedecin = new Medecin();
                    updatedMedecin.setIdMed(MedecinFragment.this.editTextNum.getText().toString().trim());
                    updatedMedecin.setTaux(Integer.parseInt(MedecinFragment.this.editTextTaux.getText().toString().trim()));
                    updatedMedecin.setNom(MedecinFragment.this.editTextName.getText().toString());
                    if (MedecinFragment.this.medecinService.update(updatedMedecin)) {
                        MedecinFragment.this.loadMeds();
                        createDialog.dismiss();
                    }
                }
            }
        });
        createDialog.show();
    }

    public void onMedecinItemClick(Medecin medecin) {
        modalDialog(medecin);
    }

    @Override
    public void onRefresh() {
        loadMeds();
    }

    public void notifyUpdate() {
        loadMeds();
    }


    public void exportMedecinsToPdf(String destFilePath) {

        if (new File(destFilePath).exists()) {
            new File(destFilePath).delete();
        }

        try {
            /**
             * Creating Document
             */
            PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(destFilePath));
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            PdfDocumentInfo info = pdfDocument.getDocumentInfo();

            info.setTitle("Example of iText7 by Harijaona Ravelondrina");
            info.setAuthor("Harijaona Ravelondrina");
            info.setSubject("iText7 PDF Demo");
            info.setKeywords("iText, PDF, Harijaona Ravelondrina");
            info.setCreator("Harijaona Ravelondrina");

            Document document = new Document(pdfDocument, PageSize.A4, true);

            /***
             * Variables for further use....
             */
            Color mColorAccent = new DeviceRgb(153, 204, 255);
            Color mColorBlack = new DeviceRgb(0, 0, 0);
            float mHeadingFontSize = 20.0f;
            float mValueFontSize = 26.0f;

            /**
             * How to USE FONT....
             */
            PdfFont font = PdfFontFactory.createFont("assets/fonts/brandon_medium.otf", "UTF-8", true);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator(new DottedLine());
            lineSeparator.setStrokeColor(new DeviceRgb(0, 0, 68));

            // Title Order Details...
            // Adding Title....
            Text mOrderDetailsTitleChunk = new Text("Liste des médecins").setFont(font).setFontSize(36.0f).setFontColor(mColorBlack);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(mOrderDetailsTitleParagraph);

            List<Medecin> medecinTmpList = this.medecinService.fetchAll();

            for (Medecin medItem : medecinTmpList) {

                // ID
                Text medIdChunk = new Text("ID:").setFont(font).setFontSize(mHeadingFontSize).setFontColor(mColorAccent);
                Paragraph medIdParagraph = new Paragraph(medIdChunk);
                document.add(medIdParagraph);

                Text medIdValueChunk = new Text("#" + medItem.getIdMed()).setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack);
                Paragraph medIdValueParagraph = new Paragraph(medIdValueChunk);
                document.add(medIdValueParagraph);

                // Name
                Text medNameChunk = new Text("Nom et Prénom:").setFont(font).setFontSize(mHeadingFontSize).setFontColor(mColorAccent);
                Paragraph medNameParagraph = new Paragraph(medNameChunk);
                document.add(medNameParagraph);

                Text medNameValueChunk = new Text(medItem.getNom()).setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack);
                Paragraph medNameValueParagraph = new Paragraph(medNameValueChunk);
                document.add(medNameValueParagraph);

                // Hourly Rate
                Text medRateChunk = new Text("Taux horaire:").setFont(font).setFontSize(mHeadingFontSize).setFontColor(mColorAccent);
                Paragraph medRateParagraph = new Paragraph(medRateChunk);
                document.add(medRateParagraph);

                Text medRateValueChunk = new Text(medItem.getTaux() + " MGA / HEURE").setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack);
                Paragraph medRateValueParagraph = new Paragraph(medRateValueChunk);
                document.add(medRateValueParagraph);

                // Adding Line Breakable Space....
                document.add(new Paragraph(""));
                // Adding Horizontal Line...
                document.add(lineSeparator);
                // Adding Line Breakable Space....
                document.add(new Paragraph(""));
            }

            document.close();

            FileHelpers.openPdfFromActivity(getContext(), destFilePath);
            Toast.makeText(getContext(), "Le document PDF a été exporté dans " + destFilePath, Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            Log.e("exportPdf","createPdf: Error " + e.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(getContext(), "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }


}

