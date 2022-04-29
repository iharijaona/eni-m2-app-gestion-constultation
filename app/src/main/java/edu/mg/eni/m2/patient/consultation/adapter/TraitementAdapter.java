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
import edu.mg.eni.m2.patient.consultation.model.Traitement;
import edu.mg.eni.m2.patient.consultation.service.TraitementService;

public class TraitementAdapter extends RecyclerView.Adapter<TraitementAdapter.ViewHolder>  implements Filterable {
    public Context context;
    public TraitementInterface traitementInterface;
    private List<Traitement> traitementList;
    private List<Traitement> traitementListFiltered;

    public TraitementAdapter(List<Traitement> list, Context context, TraitementInterface traitementInterface) {
        this.context = context;
        this.traitementInterface = traitementInterface;
        this.traitementList = list;
        this.traitementListFiltered = list;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_cell_traitement, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Traitement traitement = this.traitementListFiltered.get(i);
        viewHolder.textViewMedName.setText(traitement.getMedecin().getNom());
        viewHolder.textViewPatName.setText(traitement.getPatient().getNom());
        viewHolder.textViewTaux.setText(String.valueOf(traitement.getNbHour() * traitement.getMedecin().getTaux() )+" MGA");
        viewHolder.buttonUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TraitementAdapter.this.traitementInterface.onTraitementItemClick(traitement);
            }
        });
        viewHolder.buttonDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TraitementAdapter.this.context);
                builder.setMessage((CharSequence) "Voulez-vous vraiment supprimer la consultation de " + traitement.getPatient().getNom());
                builder.setPositiveButton((CharSequence) "OUI", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new TraitementService(new DBHelpers(TraitementAdapter.this.context).getDb()).deleteAll(traitement.getId());
                        TraitementAdapter.this.traitementInterface.notifyUpdate();
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
        return this.traitementListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charSequence == null || (charString != null && charString.trim().isEmpty())) {
                    traitementListFiltered = traitementList;
                } else {
                    List<Traitement> filteredList = new ArrayList<>();
                    for (Traitement row : traitementList) {
                        if (row.getPatient().getNom().toLowerCase().contains(charString.toLowerCase()) || row.getMedecin().getNom().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    traitementListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = traitementListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                traitementListFiltered = (ArrayList<Traitement>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface TraitementInterface {
        void onTraitementItemClick(Traitement traitement);
        void notifyUpdate();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button buttonDel;
        public Button buttonUp;
        public TextView textViewMedName;
        public TextView textViewPatName;
        public TextView textViewTaux;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.textViewPatName = (TextView) view.findViewById(R.id.tv_patient_name);
            this.textViewMedName = (TextView) view.findViewById(R.id.tv_medecin_name);
            this.textViewTaux = (TextView) view.findViewById(R.id.tv_rate_hour);
            this.buttonUp = (Button) view.findViewById(R.id.button_update);
            this.buttonDel = (Button) view.findViewById(R.id.button_delete);
        }
    }
}
