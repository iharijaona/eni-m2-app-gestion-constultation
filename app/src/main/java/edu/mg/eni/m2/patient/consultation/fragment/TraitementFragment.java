package edu.mg.eni.m2.patient.consultation.fragment;


import android.content.ActivityNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
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
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
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
import edu.mg.eni.m2.patient.consultation.adapter.MedecinSpAdapter;
import edu.mg.eni.m2.patient.consultation.adapter.PatientSPAdapter;
import edu.mg.eni.m2.patient.consultation.adapter.TraitementAdapter;
import edu.mg.eni.m2.patient.consultation.helpers.DBHelpers;
import edu.mg.eni.m2.patient.consultation.helpers.FileHelpers;
import edu.mg.eni.m2.patient.consultation.model.Medecin;
import edu.mg.eni.m2.patient.consultation.model.Patient;
import edu.mg.eni.m2.patient.consultation.model.Traitement;
import edu.mg.eni.m2.patient.consultation.service.MedecinService;
import edu.mg.eni.m2.patient.consultation.service.PatientService;
import edu.mg.eni.m2.patient.consultation.service.TraitementService;

public class TraitementFragment extends Fragment implements TraitementAdapter.TraitementInterface, PullRefreshLayout.OnRefreshListener  {
    private DBHelpers dbHelpers;
    public AppCompatEditText editTextDuree;
    public IconTextView icon;
    private PullRefreshLayout refreshableLayout;
    public Medecin medecin = new Medecin();
    private MedecinService medecinService;
    private RelativeLayout noResults;
    public Patient patient = new Patient();
    private PatientService patientService;
    public RecyclerView recyclerView;
    private AppCompatSpinner spinnerMedecin;
    private AppCompatSpinner spinnerPatient;
    public TextView textEmpty;
    public TraitementAdapter traitementAdapter;
    public TraitementService traitementService;
    public Boolean isUpdate = false;
    private ArrayList<Traitement> allTraitement = new ArrayList<>();

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.dbHelpers = new DBHelpers(getContext());
        this.traitementService = new TraitementService(this.dbHelpers.getDb());
        this.medecinService = new MedecinService(this.dbHelpers.getDb());
        this.patientService = new PatientService(this.dbHelpers.getDb());
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_traitement, viewGroup, false);
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
        this.traitementAdapter = new TraitementAdapter(allTraitement, getContext(), this);
        setupView(view);
        loadTraitements();
    }

    private void setupView(View view) {
        setHasOptionsMenu(true);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.cell_traitement);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(1);
        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.traitementAdapter);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_medecin, menu);
        ((SearchView) menu.findItem(R.id.app_bar_search).getActionView()).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            public boolean onQueryTextChange(String str) {
                TraitementFragment.this.traitementAdapter.getFilter().filter(str.trim());
                return false;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.app_bar_add) {
            modalDialog(null);
        }
        else if (menuItem.getItemId() == R.id.app_bar_export) {
            this.exportTraitementsToPdf(FileHelpers.getAppPath(this.getContext()) + this.getResources().getString(R.string.app_name) +"-Traitements-"+String.valueOf(new Date().getTime())+".pdf");
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void loadTraitements() {

        List<Traitement> allTraitementTmpList = this.traitementService.fetchAll();
        allTraitement.clear();

        if (allTraitementTmpList != null && !allTraitementTmpList.isEmpty()) {
            allTraitement.addAll(allTraitementTmpList);
            this.noResults.setVisibility(View.INVISIBLE);
        }
        else{
            this.noResults.setVisibility(View.VISIBLE);
        }


        this.traitementAdapter.notifyDataSetChanged();
        this.recyclerView.invalidate();

        this.refreshableLayout.setRefreshing(false);


    }

    public void modalDialog(Traitement currentTraitement) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View inflate = getLayoutInflater().inflate(R.layout.single_dialog_traitement, (ViewGroup) null);
        dialogBuilder.setView(inflate);

        final AlertDialog createDialog = dialogBuilder.create();
        this.spinnerMedecin = (AppCompatSpinner) inflate.findViewById(R.id.txtFieldMedecin);
        this.spinnerPatient = (AppCompatSpinner) inflate.findViewById(R.id.txtFieldPatient);
        this.editTextDuree = (AppCompatEditText) inflate.findViewById(R.id.txtFieldTaux);
        AppCompatButton appCompatButton = (AppCompatButton) inflate.findViewById(R.id.app_button_add);
        this.spinnerMedecin.setAdapter((SpinnerAdapter) new MedecinSpAdapter(this.medecinService.fetchAll(), getActivity()));
        this.spinnerMedecin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                TraitementFragment.this.medecin = (Medecin) adapterView.getItemAtPosition(i);
            }
        });
        this.spinnerPatient.setAdapter((SpinnerAdapter) new PatientSPAdapter(this.patientService.fetchAll(), getActivity()));
        this.spinnerPatient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                TraitementFragment.this.patient = (Patient) adapterView.getItemAtPosition(i);
            }
        });

        this.isUpdate = false;

        if (currentTraitement != null && currentTraitement.getId() > 0) {
            this.editTextDuree.setText(String.valueOf(currentTraitement.getNbHour()));
            appCompatButton.setText("Modifier");
            this.isUpdate = true;
        }

        appCompatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Check form validation
                if (TraitementFragment.this.editTextDuree.getText().toString().trim().isEmpty()) {
                    Toast.makeText(TraitementFragment.this.getContext(), "Veuillez compléter le formulaire", Toast.LENGTH_LONG).show();
                }

                try{
                    Integer.parseInt(TraitementFragment.this.editTextDuree.getText().toString().trim());
                }
                catch (Exception e){
                    Toast.makeText(TraitementFragment.this.getContext(), "Veuillez entrer une duree de consultation valide", Toast.LENGTH_LONG).show();
                    return;
                }

                // Register new treatment
                if (!TraitementFragment.this.isUpdate) {
                    Traitement newTraitement = new Traitement();
                    newTraitement.setMedecin(TraitementFragment.this.medecin);
                    newTraitement.setPatient(TraitementFragment.this.patient);
                    newTraitement.setNbHour(Integer.parseInt(TraitementFragment.this.editTextDuree.getText().toString()));
                    if (TraitementFragment.this.traitementService.add(newTraitement)) {
                        TraitementFragment.this.loadTraitements();
                        createDialog.dismiss();
                    }
                }

                // Update existing
                else {
                    Traitement updatedTraitement = new Traitement();
                    updatedTraitement.setMedecin(TraitementFragment.this.medecin);
                    updatedTraitement.setPatient(TraitementFragment.this.patient);
                    updatedTraitement.setNbHour(Integer.parseInt(TraitementFragment.this.editTextDuree.getText().toString()) * TraitementFragment.this.medecin.getTaux());
                    if (TraitementFragment.this.traitementService.update(updatedTraitement)) {
                        TraitementFragment.this.loadTraitements();
                        createDialog.dismiss();
                    }
                }
            }
        });
        createDialog.show();
    }

    public void onTraitementItemClick(Traitement traitement) {
        modalDialog(traitement);
    }

    @Override
    public void onRefresh() {
        loadTraitements();
    }

    public void notifyUpdate() {
        loadTraitements();
    }

    public void exportTraitementsToPdf(String destFilePath) {

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
            Text mOrderDetailsTitleChunk = new Text("Liste des traitements").setFont(font).setFontSize(36.0f).setFontColor(mColorBlack);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(mOrderDetailsTitleParagraph);

            List<Traitement> traitementListTmp = this.traitementService.fetchAll();

            for (Traitement traitItem : traitementListTmp) {

                // ID
                Text traitIdChunk = new Text("ID:").setFont(font).setFontSize(mHeadingFontSize).setFontColor(mColorAccent);
                Paragraph traitIdParagraph = new Paragraph(traitIdChunk);
                document.add(traitIdParagraph);

                Text traitIdValueChunk = new Text("#" + String.valueOf(traitItem.getId())).setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack);
                Paragraph traitIdValueParagraph = new Paragraph(traitIdValueChunk);
                document.add(traitIdValueParagraph);

                // Medecin Name
                Text traitMedNameChunk = new Text("Médecin:").setFont(font).setFontSize(mHeadingFontSize).setFontColor(mColorAccent);
                Paragraph traitMedNameParagraph = new Paragraph(traitMedNameChunk);
                document.add(traitMedNameParagraph);

                Text traitMedNameValueChunk = new Text(traitItem.getMedecin().getNom()).setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack);
                Paragraph traitMedNameValueParagraph = new Paragraph(traitMedNameValueChunk);
                document.add(traitMedNameValueParagraph);

                // Patient Name
                Text traitPatNameChunk = new Text("Patient:").setFont(font).setFontSize(mHeadingFontSize).setFontColor(mColorAccent);
                Paragraph traitPatNameParagraph = new Paragraph(traitPatNameChunk);
                document.add(traitPatNameParagraph);

                Text traitPatNameValueChunk = new Text(traitItem.getPatient().getNom()).setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack);
                Paragraph traitPatNameValueParagraph = new Paragraph(traitPatNameValueChunk);
                document.add(traitPatNameValueParagraph);

                // Amount
                Text traiAmountChunk = new Text("Montant de la consultation:").setFont(font).setFontSize(mHeadingFontSize).setFontColor(mColorAccent);
                Paragraph traiAmountParagraph = new Paragraph(traiAmountChunk);
                document.add(traiAmountParagraph);

                Text traiAmountValueChunk = new Text( String.valueOf(traitItem.getNbHour() * traitItem.getMedecin().getTaux()) + " MGA").setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack);
                Paragraph traiAmountValueParagraph = new Paragraph(traiAmountValueChunk);
                document.add(traiAmountValueParagraph);

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
