package com.rocdev.android.elancev0.activities;

import android.content.Intent;
import android.os.Bundle;

import com.rocdev.android.elancev0.fragments.AddLocatieFragment;
import com.rocdev.android.elancev0.models.Locatie;

/**
 * Created by piet on 30-10-16.
 * Activity voor Lokatie Details
 */

public class LocatieDetailActivity extends BaseActivity implements
        AddLocatieFragment.OnAddAttractieFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT_NO_DRAWER);
        Intent intent = getIntent();
        Locatie locatie = intent.getParcelableExtra(KEY_LOCATIE);
        changeFragment(AddLocatieFragment.newInstance(locatie));


    }

    @Override
    public void bewaarLocatie(Locatie locatie) {
        admin.addLocatie(locatie);
        finish();
    }

    @Override
    public void deleteLocatie(Locatie locatie) {
        admin.deleteLocatie(locatie);
        finish();
    }
}
