package com.rocdev.android.elancev0.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.models.Afspraak;

import java.util.ArrayList;

/**
 * Created by piet on 24-07-16.
 *
 */
public class AfsprakenListAdapter extends BaseAdapter {
    private ArrayList<Afspraak> afspraken;
    private LayoutInflater layoutInflater;


    public AfsprakenListAdapter(ArrayList<Afspraak> afspraken, Context context) {
        this.afspraken = afspraken;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return afspraken.size();
    }

    @Override
    public Object getItem(int position) {
        return afspraken.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            row = layoutInflater.inflate(R.layout.layout_afspraak_listitem, null);

        } else {
            row = convertView;
        }
        TextView beschrijvingTextView = (TextView) row.findViewById(R.id.afspraakItemBeschrijving);
        TextView datumTextView = (TextView) row.findViewById(R.id.afspraakItemDatumEnTijd);
        Afspraak afspraak = afspraken.get(position);
        beschrijvingTextView.setText(afspraak.getLocatie());
        datumTextView.setText(afspraak.getBeginTijdFormat());
        return row;
    }
}
