package com.rocdev.android.elancev0.activities;

import android.os.Bundle;

import com.rocdev.android.elancev0.fragments.AddLocatieFragment;
import com.rocdev.android.elancev0.models.Locatie;

/**
 * Created by piet on 30-10-16.
 *
 */

public class AddLocatieActivity extends BaseActivity implements
        AddLocatieFragment.OnAddAttractieFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_NO_DRAWER);
        if (savedInstanceState == null) {
            changeFragment(AddLocatieFragment.newInstance(null));
        }
    }

    @Override
    public void bewaarLocatie(Locatie attractie) {
        admin.addLocatie(attractie);
        finish();
    }

    @Override
    public void deleteLocatie(Locatie locatie) {}
}
