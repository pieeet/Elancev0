package com.rocdev.android.elancev0.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.fragments.AddUserFragment;
import com.rocdev.android.elancev0.fragments.LogInFragment;
import com.rocdev.android.elancev0.interfaces.Constants;
import com.rocdev.android.elancev0.models.Admin;
import com.rocdev.android.elancev0.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.rocdev.android.elancev0.activities.BaseActivity.coachBevestigd;
import static com.rocdev.android.elancev0.activities.BaseActivity.mUser;

public class LogInActivity extends AppCompatActivity implements
        LogInFragment.OnLogInFragmentInteractionListener,
        AddUserFragment.OnAddUserFragmentInterActionListener,
        Constants

{
    String userId;
    SharedPreferences prefs;
    FragmentManager fragmentManager;
    DatabaseReference mDatabase;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_container,
                        LogInFragment.newInstance()).commit();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(FB_ROOT);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }


    @Override
    public void onLogIn(final FirebaseUser firebaseUser) {
        String email = firebaseUser.getEmail();
        if (email != null) {
            userId = email.replace('.', '_');
        }

        DatabaseReference userRef = mDatabase
                .child(FB_USERS).child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    user = dataSnapshot.getValue(User.class);

                } else {
                    user = new User();
                    if (firebaseUser.getEmail() != null) {
                        user.set_id(firebaseUser.getEmail().replace('.', '_'));
                        user.setEmail(firebaseUser.getEmail());
                        user.setFirebaseUid(firebaseUser.getUid());
                        if (firebaseUser.getPhotoUrl() != null) {
                            user.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
                        }
                    }
                    if (firebaseUser.getPhotoUrl() != null) {
                        user.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
                    }


                }
                mUser = user;
                prefs.edit().putString(KEY_ID, user.get_id()).apply();
                fragmentManager.beginTransaction().replace(R.id.content_container,
                        AddUserFragment.newInstance(user)).commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Na inlog altijd invoer of bevestiging van user data
    @Override
    public void onUserAdded(User user) {
        new Admin().addUser(user);
        mUser = user;
        coachBevestigd = user.getIsCoach();
        prefs.edit().putString(KEY_ID, userId).apply();
        startActivity(new Intent(LogInActivity.this, MainActivity.class));
    }

}
