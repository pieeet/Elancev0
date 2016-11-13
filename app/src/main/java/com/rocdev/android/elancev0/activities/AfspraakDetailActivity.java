package com.rocdev.android.elancev0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rocdev.android.elancev0.fragments.AfspraakDetailFragment;
import com.rocdev.android.elancev0.fragments.LocatiesFragment;
import com.rocdev.android.elancev0.models.Afspraak;
import com.rocdev.android.elancev0.models.Locatie;

/**
 * Created by piet on 28-10-16.
 *  Activity for one pane layout
 */

public class AfspraakDetailActivity extends BaseActivity implements
        LocatiesFragment.OnAttractiesFragmentInteractionListener,
        AfspraakDetailFragment.OnAfspraakDetailFragmentInteractionListener {

    private AfspraakDetailFragment afspraakDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT_NO_DRAWER);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent.getParcelableExtra(KEY_AFSPRAAK) != null) {
                Afspraak afspraak = intent.getParcelableExtra(KEY_AFSPRAAK);
                afspraakDetailFragment = AfspraakDetailFragment.newInstance(afspraak);
                changeFragment(afspraakDetailFragment);
            }

        }
    }

    @Override
    public void onDeleteAfspraak(Afspraak afspraak) {
        admin.deleteAfspraak(afspraak);
        finish();
    }

    @Override
    public void onMaakAfspraak(Afspraak afspraak) {
        admin.addAfspraak(afspraak);
        finish();
    }

    @Override
    public void onWijzigLocatieVanAfspraak() {
        changeFragmentAddToBackstack(LocatiesFragment.newInstance(true));
    }

    @Override
    public void onAttractieSelected(Locatie attractie) {}

    @Override
    public void onAttractieGewijzigd(Locatie locatie) {
        if (fragmentManager.popBackStackImmediate()) {
            afspraakDetailFragment = (AfspraakDetailFragment) fragmentManager
                    .findFragmentById(CONTENT_CONTAINER);
            afspraakDetailFragment.setLocatie(locatie);
        } else {
            Toast.makeText(this, "Probeer nogmaals", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInitNieuweAttractie() {

    }
}
