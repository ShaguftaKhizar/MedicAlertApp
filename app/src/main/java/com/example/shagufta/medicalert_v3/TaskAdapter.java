package com.example.shagufta.medicalert_v3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Shagufta on 11/02/2018.
 */

public class TaskAdapter extends BaseAdapter implements ListAdapter {

    private final Context context;
    private final List<Medicament> medicamentListe;

    public TaskAdapter(Context context, List<Medicament> tasks) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        if (tasks == null) {
            throw new IllegalArgumentException("List of tasks must not be null.");
        }
        this.context = context;
        this.medicamentListe = tasks;
    }

    @Override
    public int getCount() { return this.medicamentListe.size(); }

    @Override
    public Object getItem(int position) { return this.medicamentListe.get(position); }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return Adapter.IGNORE_ITEM_VIEW_TYPE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_task_adapter, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.med_name);
        CheckBox completed = (CheckBox) convertView.findViewById(R.id.checkbox_completed);

        Medicament o = this.medicamentListe.get(position);
        name.setText(o.getName());
        completed.setChecked(o.isCompleted());
        completed.setId(position);

        return convertView;
    }

    public void add(Medicament o) {
        this.medicamentListe.add(o);
        this.notifyDataSetChanged();
    }

    public void set(int position, Medicament o) {
        this.medicamentListe.set(position, o);
        this.notifyDataSetChanged();
    }

    public void remove(int position) { this.medicamentListe.remove(position); }

}
