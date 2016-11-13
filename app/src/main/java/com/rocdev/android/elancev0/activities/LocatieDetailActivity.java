package com.rocdev.android.elancev0.activities;

import android.content.Intent;
import android.os.Bundle;

import com.rocdev.android.elancev0.fragments.LocatieDetailFragment;
import com.rocdev.android.elancev0.models.Locatie;

/**
 * Created by piet on 30-10-16.
 * Activity voor Lokatie Details
 */

public class LocatieDetailActivity extends BaseActivity implements
        LocatieDetailFragment.OnAttractieDetailFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT_NO_DRAWER);
        Intent intent = getIntent();
        Locatie locatie = intent.getParcelableExtra(KEY_LOCATIE);
        changeFragment(LocatieDetailFragment.newInstance(locatie));


    }

    @Override
    public void updateLocatie(Locatie attractie) {
        admin.addLocatie(attractie);
        finish();
    }

    @Override
    public void deleteLocatie(Locatie locatie) {
        admin.deleteLocatie(locatie);
        finish();
    }
}
