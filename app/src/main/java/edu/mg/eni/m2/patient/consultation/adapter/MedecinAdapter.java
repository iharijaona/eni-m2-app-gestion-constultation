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
import edu.mg.eni.m2.patient.consultation.model.Medecin;
import edu.mg.eni.m2.patient.consultation.service.MedecinService;

public class MedecinAdapter extends RecyclerView.Adapter<MedecinAdapter.ViewHolder> implements Filterable {


    public Context context;
    public MedecinInterface medecinInterface;
    private List<Medecin> medecinList;
    private List<Medecin> medecinListFiltered;

    public interface MedecinInterface {
        void onMedecinItemClick(Medecin medecin);
        void notifyUpdate();
    }

    public MedecinAdapter(List<Medecin> list, Context context, MedecinInterface medecinInterface) {
        this.context = context;
        this.medecinInterface = medecinInterface;
        this.medecinList = list;
        this.medecinListFiltered = list;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_cell_element, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Medecin medecin = this.medecinListFiltered.get(i);
        viewHolder.textViewName.setText(medecin.getNom());
        viewHolder.textViewTaux.setText(String.valueOf(medecin.getTaux()) + " MGA / H");
        viewHolder.buttonUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MedecinAdapter.this.medecinInterface.onMedecinItemClick(medecin);
            }
        });
        viewHolder.buttonDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MedecinAdapter.this.context);
                builder.setMessage((CharSequence) "Voulez-vous vraiment supprimer " + medecin.getNom());
                builder.setPositiveButton((CharSequence) "OUI", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new MedecinService(new DBHelpers(MedecinAdapter.this.context).getDb()).deleteAll(medecin.getIdMed());
                        MedecinAdapter.this.medecinInterface.notifyUpdate();
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
        return this.medecinListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charSequence == null || (charString != null && charString.trim().isEmpty())) {
                    medecinListFiltered = medecinList;
                } else {
                    List<Medecin> filteredList = new ArrayList<>();
                    for (Medecin row : medecinList) {
                        if (row.getNom().toLowerCase().contains(charString.toLowerCase()) || row.getIdMed().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    medecinListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = medecinListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                medecinListFiltered = (ArrayList<Medecin>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public Button buttonDel;

        public Button buttonUp;

        public TextView textViewName;

        public TextView textViewTaux;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.tv_name);
            this.textViewTaux = (TextView) view.findViewById(R.id.tv_taux);
            this.buttonUp = (Button) view.findViewById(R.id.button_update);
            this.buttonDel = (Button) view.findViewById(R.id.button_delete);
        }
    }
}
