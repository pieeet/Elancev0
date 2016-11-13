package com.rocdev.android.elancev0.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.firebase.MyFireBase;
import com.rocdev.android.elancev0.interfaces.Constants;
import com.rocdev.android.elancev0.models.Admin;
import com.rocdev.android.elancev0.models.User;


/**
 * @author piet on 22-07-16.
 *         Omdat we met één Moeder layout werken voor meerdere activities
 *         is het handig om een BaseActivity te hebben om code die in iedere Activity
 *         moet worden geïmplementeerd af te handelen. De views worden gemaakt met de @Override methode
 *         setContentView. Deze wordt in de sub-activities in de onCreate methode aangeroepen met de
 *         juiste layout.
 */
public abstract class BaseActivity extends AppCompatActivity implements Constants,
        GoogleApiClient.OnConnectionFailedListener {

    //Content container worden fragments op gezet en is voor iedere Activity dezelfde
    protected static final int CONTENT_CONTAINER = R.id.content_container;
    //iedere activity heeft een fragmentmanager
    protected FragmentManager fragmentManager;
    //iedere act heeft dezelfde toolbar
    protected Toolbar toolbar;
    protected SharedPreferences prefs;

    //de database ref
    protected DatabaseReference mDatabase;

    public static User mUser;

    private Menu menu;
    static boolean  coachBevestigd;

    private GoogleApiClient mGoogleApiClient;

    //Layout constanten
    protected static final int LAYOUT_NO_DRAWER = R.layout.app_bar_main;
    protected static final int LAYOUT_WITH_DRAWER = R.layout.activity_main;

    private ValueEventListener mUserListener;

    protected Admin admin;

    protected boolean isTwoPane;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        // Singleton firebase reference
        mDatabase = MyFireBase.getInstance().getmDatabase();
        setMyUser();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (admin == null) {
            admin = new Admin();
        }



    }

    public void clearRightFragment() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_pane_right);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    // Hier de toolbar die in alle child Activities moet worden vertoond
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // stuur user eventueel naar inlog scherm of set Listener
    private void setMyUser() {
        if (mUser == null) {
            String userId = PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .getString(KEY_ID, null);
            if (userId == null) {
                startActivity(new Intent(this, LogInActivity.class));
                finish();
            } else {
                mUser = new User();
                mUser.set_id(userId);
                setMyUserListener();
            }
        } else {
            setMyUserListener();
        }
    }

    private void setMyUserListener() {
        if (mUserListener == null) {
            mDatabase.child(FB_USERS)
                    .child(mUser.get_id()).addValueEventListener(mUserListener = new
                    ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUser = dataSnapshot.getValue(User.class);
                    if (mUser != null) {
                        if (mUser.getIsCoach()) {
                            if (menu != null) {
                                menu.findItem(R.id.action_zoek_coachee)
                                        .setVisible(true);
                            }
                            coachBevestigd = true;
                        } else {
                            if (menu != null) {
                                menu.findItem(R.id.action_zoek_coachee)
                                        .setVisible(false);
                            }
                            coachBevestigd = false;
                        }
                        //Schrijf evt token naar app server
                        String token = prefs.getString(KEY_FIREBASE_TOKEN, null);
                        if (mUser.getToken() == null || !(mUser.getToken().equals(token))) {
                            mUser.setToken(token);
                            admin.addUser(mUser);
                            admin.saveToken(token);
                        }

                    } else {
                        signOut();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }


    protected void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            disconnect();
                        }
                    });
        } else {
            disconnect();
        }

    }

    private void disconnect() {
        mUser = null;
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_ID).apply();
        startActivity(new Intent(BaseActivity.this, LogInActivity.class));
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (coachBevestigd) {
            menu.findItem(R.id.action_zoek_coachee)
                    .setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            } else {
                finish();
            }
            return true;
        } else if (id == R.id.action_zoek_coachee) {
            startActivity(new Intent(this, AddCoacheeActivity.class));

        } else if (id == R.id.action_log_uit) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }


    protected void changeFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(CONTENT_CONTAINER, fragment)
                .commit();

    }

    protected void changeFragmentAddToBackstack(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(CONTENT_CONTAINER, fragment)
                .addToBackStack(null)
                .commit();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
