package com.rocdev.android.elancev0.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;


import java.util.Calendar;


/**
 *
 * Created by piet on  27-07-16.
 */
public class DatePickerFragment extends DialogFragment {
    DatePickerDialog myDatePicker;



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (getArguments() != null) {
            c.setTimeInMillis(getArguments().getLong("datum"));
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog.OnDateSetListener dateSetListener = (DatePickerDialog.OnDateSetListener) getTargetFragment();
        myDatePicker = new DatePickerDialog(getActivity(), dateSetListener, year, month, day); // DatePickerDialog gets callBack listener as 2nd parameter
        // Create a new instance of DatePickerDialog and return it
        return myDatePicker;
    }

    public static DatePickerFragment newInstance(long datum) {
        DatePickerFragment dpf = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putLong("datum", datum);
        dpf.setArguments(args);
        return dpf;
    }


}