package com.rocdev.android.elancev0.fragments;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.rocdev.android.elancev0.firebase.MyFireBase;
import com.rocdev.android.elancev0.interfaces.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment implements Constants {

    String naamStad;
    DatabaseReference mDatabase;


    //fragments kunnen allemaal de title setten
    protected static String title;

    public BaseFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        naamStad = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(KEY_NAAM_STAD, DEFAULT_NAAM_STAD);
        mDatabase = MyFireBase.getInstance().getmDatabase();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResume() {
        super.onResume();
        setTitle();
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }

    }

    public abstract void setTitle();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
