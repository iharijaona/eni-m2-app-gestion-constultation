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
import edu.mg.eni.m2.patient.consultation.adapter.PatientAdapter;
import edu.mg.eni.m2.patient.consultation.helpers.DBHelpers;
import edu.mg.eni.m2.patient.consultation.helpers.FileHelpers;
import edu.mg.eni.m2.patient.consultation.model.Medecin;
import edu.mg.eni.m2.patient.consultation.model.Patient;
import edu.mg.eni.m2.patient.consultation.service.PatientService;

public class PatientFragment extends Fragment implements PatientAdapter.PatientInterface, PullRefreshLayout.OnRefreshListener  {
    private DBHelpers dbHelpers;
    public AppCompatEditText editTextAdress;
    public AppCompatEditText editTextName;
    public AppCompatEditText editTextNum;
    public IconTextView icon;
    public Boolean isUpdate = false;
    private PullRefreshLayout refreshableLayout;
    private RelativeLayout noResults;
    public PatientAdapter patientAdapter;
    public PatientService patientService;
    public RecyclerView recyclerView;
    private TextView textEmpty;
    private List<Patient> allPatient = new ArrayList<>();

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.dbHelpers = new DBHelpers(getContext());
        this.patientService = new PatientService(this.dbHelpers.getDb());
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_patient, viewGroup, false);
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
        this.patientAdapter = new PatientAdapter(allPatient, getContext(), this);
        setupView(view);
        loadPatients();
    }

    private void setupView(View view) {
        setHasOptionsMenu(true);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.cell_patient);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(1);
        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.patientAdapter);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_medecin, menu);
        ((SearchView) menu.findItem(R.id.app_bar_search).getActionView()).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            public boolean onQueryTextChange(String str) {
                PatientFragment.this.patientAdapter.getFilter().filter(str.trim());
                return false;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.app_bar_add) {
            modalDialog(null);
        }
        else if (menuItem.getItemId() == R.id.app_bar_export) {
            this.exportPatientsToPdf(FileHelpers.getAppPath(this.getContext()) + this.getResources().getString(R.string.app_name) +"-Patients-"+String.valueOf(new Date().getTime())+".pdf");
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void loadPatients() {
        List<Patient> allPatientTmpList = this.patientService.fetchAll();
        allPatient.clear();
        if (allPatientTmpList != null && !allPatientTmpList.isEmpty()) {
            allPatient.addAll(allPatientTmpList);
            this.noResults.setVisibility(View.INVISIBLE);
        }
        else{
            this.noResults.setVisibility(View.VISIBLE);
        }

        this.recyclerView.invalidate();
        this.patientAdapter.notifyDataSetChanged();

        this.refreshableLayout.setRefreshing(false);
    }

    public void modalDialog(Patient currentPatient) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View inflate = getLayoutInflater().inflate(R.layout.single_dialog_patient, (ViewGroup) null);
        dialogBuilder.setView(inflate);

        final AlertDialog editorDialog = dialogBuilder.create();
        this.editTextName = (AppCompatEditText) inflate.findViewById(R.id.txtNom);
        this.editTextNum = (AppCompatEditText) inflate.findViewById(R.id.txtNum);
        this.editTextAdress = (AppCompatEditText) inflate.findViewById(R.id.txtAdresse);
        AppCompatButton appCompatButton = (AppCompatButton) inflate.findViewById(R.id.app_btn_add);

        this.isUpdate = false;

        if (currentPatient != null && currentPatient.getIdPat() != null) {
            this.editTextNum.setText(currentPatient.getIdPat());
            this.editTextNum.setEnabled(false);
            this.editTextName.setText(currentPatient.getNom());
            this.editTextAdress.setText(currentPatient.getAdresse());
            appCompatButton.setText("Modifier");
            this.isUpdate = true;
        }

        appCompatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Validate form data
                if (PatientFragment.this.editTextName.getText().toString().trim().isEmpty() || PatientFragment.this.editTextNum.getText().toString().trim().isEmpty() || PatientFragment.this.editTextAdress.getText().toString().trim().isEmpty()) {
                    Toast.makeText(PatientFragment.this.getContext(), "Veuillez compléte le formulaire", Toast.LENGTH_LONG).show();
                    return;
                }

                // Create new patient
                if (!PatientFragment.this.isUpdate) {

                    Patient newPatient = new Patient();
                    newPatient.setIdPat(PatientFragment.this.editTextNum.getText().toString().trim());
                    newPatient.setAdresse(PatientFragment.this.editTextAdress.getText().toString().trim());
                    newPatient.setNom(PatientFragment.this.editTextName.getText().toString());

                    Patient existingPatient = PatientFragment.this.patientService.fetchById(PatientFragment.this.editTextNum.getText().toString());
                    if (existingPatient != null && existingPatient.getIdPat() != null) {
                        Context context = PatientFragment.this.getContext();
                        Toast.makeText(context, "Le patient "+existingPatient.getIdPat()+" existe déjà au nom de " + existingPatient.getNom(), Toast.LENGTH_LONG).show();
                    } else if (PatientFragment.this.patientService.add(newPatient)) {
                        PatientFragment.this.loadPatients();
                        editorDialog.dismiss();
                    }
                }

                // Update existing patient
                else {
                    Patient updatedPatient = new Patient();
                    updatedPatient.setIdPat(PatientFragment.this.editTextNum.getText().toString().trim());
                    updatedPatient.setAdresse(PatientFragment.this.editTextAdress.getText().toString().trim());
                    updatedPatient.setNom(PatientFragment.this.editTextName.getText().toString());
                    if (PatientFragment.this.patientService.update(updatedPatient)) {
                        PatientFragment.this.loadPatients();
                        editorDialog.dismiss();
                    }
                }
            }
        });
        editorDialog.show();
    }

    public void onPatientItemClick(Patient patient) {
        modalDialog(patient);
    }

    @Override
    public void onRefresh() {
        loadPatients();
    }

    public void notifyUpdate() {
        loadPatients();
    }


    public void exportPatientsToPdf(String destFilePath) {

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
            Text mOrderDetailsTitleChunk = new Text("Liste des patients").setFont(font).setFontSize(36.0f).setFontColor(mColorBlack);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(mOrderDetailsTitleParagraph);

            List<Patient> patientListTmp = this.patientService.fetchAll();

            for (Patient patItem : patientListTmp) {

                // ID
                Text patIdChunk = new Text("ID:").setFont(font).setFontSize(mHeadingFontSize).setFontColor(mColorAccent);
                Paragraph patIdParagraph = new Paragraph(patIdChunk);
                document.add(patIdParagraph);

                Text patIdValueChunk = new Text("#" + patItem.getIdPat()).setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack);
                Paragraph patIdValueParagraph = new Paragraph(patIdValueChunk);
                document.add(patIdValueParagraph);

                // Name
                Text patNameChunk = new Text("Nom et Prénom:").setFont(font).setFontSize(mHeadingFontSize).setFontColor(mColorAccent);
                Paragraph patNameParagraph = new Paragraph(patNameChunk);
                document.add(patNameParagraph);

                Text patNameValueChunk = new Text(patItem.getNom()).setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack);
                Paragraph patNameValueParagraph = new Paragraph(patNameValueChunk);
                document.add(patNameValueParagraph);

                // Adresse
                Text patAddrChunk = new Text("Adresse:").setFont(font).setFontSize(mHeadingFontSize).setFontColor(mColorAccent);
                Paragraph patAddrParagraph = new Paragraph(patAddrChunk);
                document.add(patAddrParagraph);

                Text patAddrValueChunk = new Text(patItem.getAdresse()).setFont(font).setFontSize(mValueFontSize).setFontColor(mColorBlack);
                Paragraph patAddrValueParagraph = new Paragraph(patAddrValueChunk);
                document.add(patAddrValueParagraph);

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
