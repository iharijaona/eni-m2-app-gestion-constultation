package edu.mg.eni.m2.patient.consultation.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import edu.mg.eni.m2.patient.consultation.R;
import edu.mg.eni.m2.patient.consultation.helpers.DBHelpers;
import edu.mg.eni.m2.patient.consultation.model.Patient;
import edu.mg.eni.m2.patient.consultation.service.PatientService;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder>  implements Filterable {

    public Context context;
    public PatientInterface patientInterface;
    private List<Patient> patientList;
    private List<Patient> patientListFiltered;

    public interface PatientInterface {
        void onPatientItemClick(Patient patient);
        void notifyUpdate();
    }

    public PatientAdapter(List<Patient> list, Context context, PatientInterface patientInterface) {
        this.context = context;
        this.patientInterface = patientInterface;
        this.patientList = list;
        this.patientListFiltered = list;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_cell_element, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Patient patient = this.patientListFiltered.get(i);
        viewHolder.textViewName.setText(patient.getNom());
        viewHolder.textViewAdresse.setText(patient.getAdresse());
        viewHolder.buttonUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PatientAdapter.this.patientInterface.onPatientItemClick(patient);
            }
        });
        viewHolder.buttonDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientAdapter.this.context);
                builder.setMessage((CharSequence) "Voulez-vous vraiment supprimer " + patient.getNom());
                builder.setPositiveButton((CharSequence) "OUI", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new PatientService(new DBHelpers(PatientAdapter.this.context).getDb()).deleteAll(patient.getIdPat());
                        PatientAdapter.this.patientInterface.notifyUpdate();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton((CharSequence) "NON", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create();
                builder.show();
            }
        });
    }

    public int getItemCount() {
        return this.patientListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charSequence == null || (charString != null && charString.trim().isEmpty())) {
                    patientListFiltered = patientList;
                } else {
                    List<Patient> filteredList = new ArrayList<>();
                    for (Patient row : patientList) {
                        if (row.getNom().toLowerCase().contains(charString.toLowerCase()) || row.getIdPat().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    patientListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = patientListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                patientListFiltered = (ArrayList<Patient>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public Button buttonDel;
        public Button buttonUp;
        public TextView textViewName;
        public TextView textViewAdresse;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.tv_name);
            this.textViewAdresse = (TextView) view.findViewById(R.id.tv_taux);
            this.buttonUp = (Button) view.findViewById(R.id.button_update);
            this.buttonDel = (Button) view.findViewById(R.id.button_delete);
        }
    }
}
