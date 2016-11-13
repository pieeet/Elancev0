package com.rocdev.android.elancev0.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by piet on 28-07-16.
 *
 */
public class TimePickerFragment extends DialogFragment {

    Calendar calendar;
    TimePickerDialog myTimePicker;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = 0;

        if (getArguments() != null) {
            calendar.setTimeInMillis(getArguments().getLong("datum"));
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }


        TimePickerDialog.OnTimeSetListener timeSetListener = (TimePickerDialog.OnTimeSetListener) getTargetFragment();
        myTimePicker = new TimePickerDialog(getActivity(), timeSetListener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));

        // Create a new instance of TimePickerDialog and return it
        return myTimePicker;
    }

    public static TimePickerFragment newInstance(long datum) {
        TimePickerFragment tpf = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putLong("datum", datum);
        tpf.setArguments(args);
        return tpf;
    }



}
