package edu.mg.eni.m2.patient.consultation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.mg.eni.m2.patient.consultation.R;


public class HomeAdapter extends BaseAdapter {
    private Context context;
    private Integer[] data;
    private String[] strings;

    public long getItemId(int i) {
        return (long) i;
    }

    public HomeAdapter(Context context2, Integer[] numArr, String[] strArr) {
        this.context = context2;
        this.data = numArr;
        this.strings = strArr;
    }

    public int getCount() {
        return this.data.length;
    }

    public Object getItem(int i) {
        return Integer.valueOf(i);
    }

    public class Holder {
        ImageView img;
        TextView textView;

        public Holder() {
        }
    }

    @SuppressLint({"ViewHolder"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.single_grid_element, viewGroup, false);
            holder = new Holder();
            holder.img = (ImageView) view.findViewById(R.id.image);
            holder.textView = (TextView) view.findViewById(R.id.text);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.img.setImageResource(this.data[i].intValue());
        holder.textView.setText(this.strings[i]);
        return view;
    }
}
