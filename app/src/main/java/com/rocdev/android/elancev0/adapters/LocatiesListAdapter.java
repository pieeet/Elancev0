package com.rocdev.android.elancev0.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.models.Locatie;

import java.util.ArrayList;

/**
 * Created by piet on 30-10-16.
 * adapter voor locatielistview
 */

public class LocatiesListAdapter extends BaseAdapter {

    private ArrayList<Locatie> locaties;
    private LayoutInflater inflater;




    public LocatiesListAdapter(ArrayList<Locatie> locaties, Context context) {
        this.locaties = locaties;
        inflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return locaties.size();
    }

    @Override
    public Object getItem(int position) {
        return locaties.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row;
        if (view == null) {
            row = inflater.inflate(R.layout.layout_locatie_listitem, null);
        } else {
            row = view;
        }
        TextView locatieNaam = (TextView) row.findViewById(R.id.locatieNaamTextView);
        Locatie locatie = locaties.get(i);
        locatieNaam.setText(locatie.getNaam());
        TextView adres = (TextView) row.findViewById(R.id.locatieAdresTextView);
        adres.setText(locatie.getAdres() + " - " + locatie.getStadsdeel());
        return row;
    }
}
