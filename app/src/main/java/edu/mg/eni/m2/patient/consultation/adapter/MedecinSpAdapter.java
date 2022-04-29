package edu.mg.eni.m2.patient.consultation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import edu.mg.eni.m2.patient.consultation.R;
import edu.mg.eni.m2.patient.consultation.model.Medecin;

public class MedecinSpAdapter extends BaseAdapter {
    private Context context;
    private List<Medecin> medecins;

    public MedecinSpAdapter(List<Medecin> list, Context context) {
        this.medecins = list;
        this.context = context;
    }

    public int getCount() {
        return this.medecins.size();
    }

    public Medecin getItem(int i) {
        return this.medecins.get(i);
    }

    public long getItemId(int i) {
        return (long) this.medecins.size() + i;
    }

    public class Holder {
        TextView textView;

        public Holder() {

        }
    }

    @SuppressLint({"ViewHolder"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.single_item_medecin, viewGroup, false);
            holder = new Holder();
            holder.textView = (TextView) view.findViewById(R.id.text);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.textView.setText(this.medecins.get(i).getNom());
        return view;
    }
}
