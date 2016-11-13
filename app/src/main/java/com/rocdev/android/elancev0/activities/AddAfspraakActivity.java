package com.rocdev.android.elancev0.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.rocdev.android.elancev0.fragments.AddAfspraakFragment;
import com.rocdev.android.elancev0.fragments.AddLocatieFragment;
import com.rocdev.android.elancev0.fragments.LocatiesFragment;
import com.rocdev.android.elancev0.models.Afspraak;
import com.rocdev.android.elancev0.models.Locatie;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by piet on 28-10-16.
 * Activity voor one pane layout
 */

public class AddAfspraakActivity extends BaseActivity implements
        AddAfspraakFragment.OnNieuweAfspraakFragmentListener,
        LocatiesFragment.OnAttractiesFragmentInteractionListener,
        AddLocatieFragment.OnAddAttractieFragmentInteractionListener {

    private AddAfspraakFragment addAfspraakFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_NO_DRAWER);
        if (savedInstanceState == null) {
            addAfspraakFragment = AddAfspraakFragment.newInstance(mUser);
            changeFragment(addAfspraakFragment);
        }
    }

    @Override
    public void onKiesLocatie() {
        changeFragmentAddToBackstack(LocatiesFragment.newInstance(false));
    }

    @Override
    public void onMaakAfspraak(Afspraak afspraak) {
        admin.addAfspraak(afspraak);
        finish();
    }

    @Override
    public void bewaarLocatie(Locatie attractie) {

    }

    @Override
    public void onAttractieSelected(Locatie locatie) {
        if (fragmentManager.popBackStackImmediate()) {
            addAfspraakFragment = (AddAfspraakFragment) fragmentManager
                    .findFragmentById(CONTENT_CONTAINER);
            addAfspraakFragment.setLocatie(locatie);
        } else {
            Toast.makeText(this, "Probeer nogmaals", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttractieGewijzigd(Locatie attractie) {

    }

    @Override
    public void onInitNieuweAttractie() {

    }



}
