package com.rocdev.android.elancev0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.fragments.AddLocatieFragment;
import com.rocdev.android.elancev0.fragments.LocatiesFragment;
import com.rocdev.android.elancev0.models.Locatie;


/**
 * @author piet on 22/7/2016
 *         <p>
 *         subklasse BaseActivity
 *         enige dat hij doet is een LocatiesFragment op de R.id.content_container view plaatsen
 *         en listener implementeren
 */

public class LocatiesActivity extends BaseSideNavActivity implements
        LocatiesFragment.OnAttractiesFragmentInteractionListener,
        AddLocatieFragment.OnAddAttractieFragmentInteractionListener

{

    private LocatiesFragment locatiesFragment;
    private AddLocatieFragment addLocatieFragment;
    private AddLocatieFragment locatieDetailFragment;

    private String fragmentTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_WITH_DRAWER);
        isTwoPane = findViewById(R.id.fragments_container) != null;
        if (locatiesFragment == null) {
            locatiesFragment = LocatiesFragment.newInstance(false);

        }
        fragmentTitle = "Locaties";

        if (isTwoPane) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.content_container);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
            clearRightFragment();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_pane_left, locatiesFragment)
                    .commit();
        } else {
            changeFragment(locatiesFragment);
        }


    }

    @Override
    protected void onResume() {
        activityActive = ACTIVITY_LOCATIES;
        super.onResume();

    }

    //Deze methode reageert op kliklistener op listItem in LocatiesFragment
    @Override
    public void onAttractieSelected(Locatie attractie) {

        if (isTwoPane) {
            // eventueel oude fragment
            Fragment fragment = fragmentManager.findFragmentById(R.id.content_container);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
            clearRightFragment();

            locatieDetailFragment = AddLocatieFragment.newInstance(attractie);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_pane_right, locatieDetailFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Intent intent = new Intent(this, LocatieDetailActivity.class);
            intent.putExtra(KEY_LOCATIE, attractie);
            startActivity(intent);
        }

    }

    @Override
    public void onAttractieGewijzigd(Locatie attractie) {

    }

    @Override
    public void onInitNieuweAttractie() {
        if (isTwoPane) {
            addLocatieFragment = AddLocatieFragment.newInstance(null);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_pane_right, addLocatieFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            startActivity(new Intent(this, AddLocatieActivity.class));
        }

    }

    @Override
    public void bewaarLocatie(Locatie locatie) {
        if (isTwoPane) {
            admin.addLocatie(locatie);
            clearRightFragment();
            toolbar.setTitle(fragmentTitle);
        }

    }

//    @Override
//    public void updateLocatie(Locatie attractie) {
//        if (isTwoPane) {
//            admin.addLocatie(attractie);
//            clearRightFragment();
//            toolbar.setTitle(fragmentTitle);
//        }
//    }

    @Override
    public void deleteLocatie(Locatie locatie) {
        if (isTwoPane) {
            admin.deleteLocatie(locatie);
            clearRightFragment();
            toolbar.setTitle(fragmentTitle);
        }
    }

    @Override
    public void onBackPressed() {
        if (addLocatieFragment != null || locatieDetailFragment != null) {
            toolbar.setTitle(fragmentTitle);
        }
        super.onBackPressed();
    }
}
