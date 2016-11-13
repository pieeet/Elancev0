package com.rocdev.android.elancev0.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.rocdev.android.elancev0.interfaces.Constants.FB_ROOT;

/**
 * Created by piet on 01-11-16.
 * Singleton om DatabaseReference te maken
 */

public final class MyFireBase {

    private static MyFireBase instance = null;

    private DatabaseReference mDatabase;


    private MyFireBase() {
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        fd.setPersistenceEnabled(true);
        mDatabase = fd.getReference(FB_ROOT);
        mDatabase.keepSynced(true);
    }

    public static synchronized MyFireBase getInstance() {
        if (instance == null) {
            instance = new MyFireBase();
        }
        return instance;
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }
}
