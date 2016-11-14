package com.rocdev.android.elancev0.activities;


import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.fragments.AddAfspraakFragment;
import com.rocdev.android.elancev0.fragments.AddLocatieFragment;
import com.rocdev.android.elancev0.fragments.AfspraakDetailFragment;
import com.rocdev.android.elancev0.fragments.AfsprakenFragment;
import com.rocdev.android.elancev0.fragments.LocatiesFragment;
import com.rocdev.android.elancev0.models.Afspraak;
import com.rocdev.android.elancev0.models.Locatie;
import com.rocdev.android.elancev0.models.User;

/**
 * @author piet on 25-7-2016
 *         In deze Activity worden de afspraken van de gebruiker getoond. Eerst worden
 *         de gegevens van de gebruiker opgehaald, vervolgens zijn afspraken. Zie database
 */

public class AfsprakenActivity extends BaseSideNavActivity

        //Listeners van gebruikte fragments
        implements
        AfsprakenFragment.OnAfsprakenFragmentEventListener,
        AddAfspraakFragment.OnNieuweAfspraakFragmentListener,
        LocatiesFragment.OnAttractiesFragmentInteractionListener,
        AddLocatieFragment.OnAddAttractieFragmentInteractionListener,
        AfspraakDetailFragment.OnAfspraakDetailFragmentInteractionListener {


    // Gebruikte Fragments
    private AfsprakenFragment afsprakenFragment;
    private AddAfspraakFragment addAfspraakFragment;
    private AfspraakDetailFragment afspraakDetailFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_WITH_DRAWER);
        isTwoPane = findViewById(R.id.fragments_container) != null;
        if (afsprakenFragment == null) {
            afsprakenFragment = AfsprakenFragment.newInstance(mUser);
        }
        if (isTwoPane) {
            // eventueel oude fragment
            Fragment fragment = fragmentManager.findFragmentById(R.id.content_container);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
            clearRightFragment();

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_pane_left, afsprakenFragment)
                    .commit();
        } else {
            changeFragment(afsprakenFragment);
        }
        // als activity wordt gestart door een afspraaknotificatie
        Intent intent = getIntent();
        if (intent.getStringExtra(KEY_AFSPRAAK_ID) != null) {
            checkAfspraakIdIntent(intent);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkAfspraakIdIntent(intent);
    }

    private void checkAfspraakIdIntent(Intent intent) {
        if (intent.getStringExtra(KEY_AFSPRAAK_ID) != null) {
            String afspraakId = intent.getStringExtra(KEY_AFSPRAAK_ID);
            mDatabase.child(FB_AFSPRAKEN).child(afspraakId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Afspraak afspraak = dataSnapshot.getValue(Afspraak.class);
                    onAfspraakSelected(afspraak);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    protected void onResume() {
        activityActive = ACTIVITY_AFSPRAKEN;
        super.onResume();
    }

    /**
     * @param afspraak afspraak wordt opgeslagen
     */
    @Override
    public void onAfspraakSelected(Afspraak afspraak) {

        if (isTwoPane) {
            afspraakDetailFragment = AfspraakDetailFragment.newInstance(afspraak);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_pane_right, afspraakDetailFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Intent intent = new Intent(this, AfspraakDetailActivity.class);
            intent.putExtra(KEY_AFSPRAAK, afspraak);
            startActivity(intent);
        }
    }

    /**
     * start nieuwe afspraak scherm
     *
     * @param user de eigen user (misschien niet nodig)
     */
    @Override
    public void onInitNieuweAfspraak(User user) {
        //check of user wel coachees heeft om een afspraak mee te maken
        if (user.getCoachees() == null || user.getCoachees().isEmpty()) {
            Toast.makeText(this, "Je hebt geen coachees om een afspraak mee te maken",
                    Toast.LENGTH_LONG).show();
        } else {
            if (isTwoPane) {
                addAfspraakFragment = AddAfspraakFragment.newInstance(mUser);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_pane_right, addAfspraakFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                startActivity(new Intent(this, AddAfspraakActivity.class));
            }
        }
    }

    /**
     * vanuit locaties kan nieuwe locatie worden gemaakt
     */
    @Override
    public void onInitNieuweAttractie() {
        if (isTwoPane) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_pane_right, AddLocatieFragment.newInstance(null))
                    .addToBackStack(null)
                    .commit();
        } else {
            changeFragmentAddToBackstack(AddLocatieFragment.newInstance(null));
        }
    }


    /**
     * initieert lokatie kiezer vanuit addAfspraakFragment
     */
    @Override
    public void onKiesLocatie() {
        if (isTwoPane) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_pane_right, LocatiesFragment.newInstance(false))
                    .addToBackStack(null)
                    .commit();
        } else {
            changeFragmentAddToBackstack(LocatiesFragment.newInstance(false));
        }
    }

    @Override
    public void onWijzigLocatieVanAfspraak() {
        if (isTwoPane) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_pane_right, LocatiesFragment.newInstance(true))
                    .addToBackStack(null)
                    .commit();
        } else {
            changeFragmentAddToBackstack(LocatiesFragment.newInstance(true));
        }
    }


    /**
     * reageert op kiezen locatie (attractie) in locatiesFragment en keert terug naar
     * addAfspraakFragment
     *
     * @param locatie de gekozen locatie
     */
    @Override
    public void onAttractieSelected(Locatie locatie) {
        if (isTwoPane) {
            if (fragmentManager.popBackStackImmediate()) {
                addAfspraakFragment = (AddAfspraakFragment) fragmentManager
                        .findFragmentById(R.id.content_pane_right);
                addAfspraakFragment.setLocatie(locatie);
            } else {
                Toast.makeText(this, "Probeer nogmaals", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (fragmentManager.popBackStackImmediate()) {
                addAfspraakFragment = (AddAfspraakFragment) fragmentManager
                        .findFragmentById(CONTENT_CONTAINER);
                addAfspraakFragment.setLocatie(locatie);
            } else {
                Toast.makeText(this, "Probeer nogmaals", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttractieGewijzigd(Locatie locatie) {
        if (isTwoPane) {
            if (fragmentManager.popBackStackImmediate()) {
                afspraakDetailFragment = (AfspraakDetailFragment) fragmentManager
                        .findFragmentById(R.id.content_pane_right);
                afspraakDetailFragment.setLocatie(locatie);
            } else {
                Toast.makeText(this, "Probeer nogmaals", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (fragmentManager.popBackStackImmediate()) {
                afspraakDetailFragment = (AfspraakDetailFragment) fragmentManager
                        .findFragmentById(CONTENT_CONTAINER);
                afspraakDetailFragment.setLocatie(locatie);
            } else {
                Toast.makeText(this, "Probeer nogmaals", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * reageert op knop maak afspraak in addAfspraakFragment
     *
     * @param afspraak afspraak
     */
    @Override
    public void onMaakAfspraak(Afspraak afspraak) {
        if (fragmentManager.popBackStackImmediate()) {
            admin.addAfspraak(afspraak);
        }
    }

    @Override
    public void onDeleteAfspraak(Afspraak afspraak) {
        if (fragmentManager.popBackStackImmediate()) {
            admin.deleteAfspraak(afspraak);
        }
    }

    @Override
    public void bewaarLocatie(Locatie locatie) {
        if (fragmentManager.popBackStackImmediate()) {
            admin.addLocatie(locatie);
        }
    }

    @Override
    public void deleteLocatie(Locatie locatie) {}

    @Override
    public void onBackPressed() {
        if (afspraakDetailFragment != null || addAfspraakFragment != null) {
            toolbar.setTitle("Afspraken");
        }
        super.onBackPressed();
    }


}
