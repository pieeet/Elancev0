package com.rocdev.android.elancev0.models;


import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.rocdev.android.elancev0.firebase.MyFireBase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static com.rocdev.android.elancev0.activities.BaseActivity.mUser;
import static com.rocdev.android.elancev0.interfaces.Constants.*;

/**
 * Created by piet on 26-10-16.
 *
 */

public class Admin {

    private DatabaseReference mDatabase;

    /**
     *
     */
    public Admin() {
        mDatabase = MyFireBase.getInstance().getmDatabase();
    }


    public void addUser(User user) {
        // voeg de user toe onder Users
        mDatabase.child(FB_USERS)
                .child(user.get_id())
                .setValue(user.valuesToMap());
    }

    public void deleteUser(final User user) {

        //verwijder evt referenties van coachees
        if (user.getCoachees() != null) {
            for (String coacheeKey : user.getCoachees().keySet()) {
                DatabaseReference coacheeRef = mDatabase
                        .child(FB_USERS)
                        .child(coacheeKey);
                coacheeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User coachee = dataSnapshot.getValue(User.class);
                        if (coachee.getAfspraken() != null) {
                            verwijderAfspraken(coachee);
                        }
                        deleteCoachRef(coachee);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }

        // verwijder evt afspraken
        if (user.getAfspraken() != null) {
            verwijderAfspraken(user);
        }

        //delete evt coach en coachee refs
        if (user.getCoachId() != null) {
            deleteCoachRef(user);
            deleteCoacheeRef(user.getCoachId(), user.get_id());
        }

        //verwijder ten slotte de user
        mDatabase.child(FB_USERS)
                .child(user.get_id()).removeValue();
    }


    public void updateUser(User user, final List<User> exCoachees) {
        if ((exCoachees != null) && !(exCoachees.isEmpty())) {
            for (final User exCoachee : exCoachees) {
                user.getCoachees().remove(exCoachee.get_id());
                deleteCoacheeRef(user.get_id(), exCoachee.get_id());
                deleteCoachRef(exCoachee);

                if (exCoachee.getAfspraken() != null) {
                    verwijderAfspraken(exCoachee);
                }
            }
        }
        addUser(user);
    }

    private void deleteCoacheeRef(String coachKey, String coacheeKey) {
        mDatabase.child(FB_USERS)
                .child(coachKey)
                .child(KEY_COACHEES)
                .child(coacheeKey)
                .removeValue();
    }

    private void deleteCoachRef(User user) {
        mDatabase.child(FB_USERS)
                .child(user.get_id())
                .child(KEY_COACH_ID)
                .removeValue();
    }


    private void verwijderAfspraken(User user) {
        mDatabase.child(FB_USERS)
                .child(user.get_id())
                .child(KEY_AFSPRAKEN).removeValue();


        HashMap<String, Long> afspraken = user.getAfspraken();

        for (String afspraakKey : afspraken.keySet()) {
            mDatabase.child(FB_AFSPRAKEN)
                    .child(afspraakKey)
                    .child(KEY_DEELNEMERS)
                    .child(user.get_id()).removeValue();
        }
    }

    public void addCoachee(User coachee, User mUser) {

        // voeg coachee toe aan mUser
        mDatabase.child(FB_USERS).child(mUser.get_id())
                .child(KEY_COACHEES)
                .child(coachee.get_id())
                .setValue(true);

        // set attribuut coach van coachee
        mDatabase.child(FB_USERS).child(coachee.get_id())
                .child(KEY_COACH_ID)
                .setValue(mUser.get_id());

        //verwijder coachee uit lijst vorige coach
        if (coachee.getCoachId() != null) {
            mDatabase.child(FB_USERS).child(coachee.getCoachId())
                    .child(KEY_COACHEES)
                    .child(coachee.getCoachId())
                    .removeValue();
        }
        // set coachee onder mUser
        mDatabase.child(FB_USERS).child(mUser.get_id())
                .child(KEY_COACHEES)
                .child(coachee.get_id())
                .setValue(true);
    }

    /**
     *
     * @param afspraak afspraak
     */
    public void addAfspraak(Afspraak afspraak) {
        String keyAfspraak = afspraak.get_id();
        if (keyAfspraak == null) {
            keyAfspraak = mDatabase.child(FB_AFSPRAKEN).push().getKey();
            afspraak.set_id(keyAfspraak);
        }

        DatabaseReference afspraakRef = mDatabase.child(FB_AFSPRAKEN).child(keyAfspraak);
        afspraakRef.setValue(afspraak.toMap());

        // maak index naar afspraak onder deelnemers en set long begintijd als value
        // hier kan wellicht op geselecteerd of gesorteerd worden
        for (String userId : afspraak.getDeelnemers().keySet()) {
            mDatabase.child(FB_USERS)
                    .child(userId)
                    .child(KEY_AFSPRAKEN)
                    .child(keyAfspraak)
                    .setValue(afspraak.getBeginTijd());
        }
        new StuurAfspraakNotificatieTask().execute(afspraak);
    }



    public void deleteAfspraak(Afspraak afspraak) {
        DatabaseReference afspraakRef = mDatabase.child(FB_AFSPRAKEN).child(afspraak.get_id());
        afspraakRef.removeValue();

        // delete index naar afspraak onder deelnemers
        for (String userId : afspraak.getDeelnemers().keySet()) {
            mDatabase.child(FB_USERS)
                    .child(userId)
                    .child(KEY_AFSPRAKEN)
                    .child(afspraak.get_id())
                    .removeValue();
        }

    }

    public void addLocatie(Locatie locatie) {
        if (locatie.get_id() == null) {
            String key = mDatabase.child(FB_LOCATIES).push().getKey();
            locatie.set_id(key);
        }
        mDatabase.child(FB_LOCATIES).child(locatie.get_id()).setValue(locatie.toMap());
    }

    public void deleteLocatie(Locatie locatie) {
        mDatabase.child(FB_LOCATIES).child(locatie.get_id()).removeValue();
    }


    private class StuurAfspraakNotificatieTask extends AsyncTask<Afspraak, Void, String> {

        @Override
        protected String doInBackground(Afspraak... afspraken) {
            Afspraak afspraak = afspraken[0];
            HttpURLConnection connection = null;
            InputStream in = null;
            InputStreamReader reader = null;
            String res = "";

            try {
                JSONArray deelnemers = new JSONArray();
                for (String deelnemerKey: afspraak.getDeelnemers().keySet()) {
                    if (!deelnemerKey.equals(mUser.get_id())) {
                        JSONObject deelnemer = new JSONObject();
                        deelnemer.put("id", deelnemerKey);
                        deelnemers.put(deelnemer);
                    }

                }
                URL url = new URL("http://ao.roc-dev.com/elance?sendAfspraakInvites=&deelnemers="
                        + deelnemers.toString() + "&afspraakId=" + afspraak.get_id()
                        + "&locatie=" + afspraak.getLocatie() + "&tijd=" + afspraak.getBeginTijd());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                in = connection.getInputStream();
                reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1 ) {
                    char c = (char) data;
                    res += c;
                    data = reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
//            Log.d("Elance response", s);
        }
    }

    public void saveToken(String token) {
        new BewaarTokenTask().execute(token);
    }



    // bewaar token op ap server
    class BewaarTokenTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String token = params[0];
            HttpURLConnection connection = null;
            InputStream in = null;

            try {
                URL url = new URL("http://ao.roc-dev.com/elance?saveToken=&userId="
                        + mUser.get_id() + "&token=" + token);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                in = connection.getInputStream();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

    }





}
