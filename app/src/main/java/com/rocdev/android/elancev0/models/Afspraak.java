package com.rocdev.android.elancev0.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.rocdev.android.elancev0.interfaces.Constants;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by piet on 23-07-16.
 * 
 */
@IgnoreExtraProperties
public class Afspraak implements Constants, Parcelable, Comparable<Afspraak> {


    private static final String DATE_FORMAT_TEMPLATE = "d MMMM yyyy HH:mm";

    private String _id;
    private String beschrijving;
    private long beginTijd;
    private String locatie;
    private String locatieId;
    private HashMap<String, Boolean> deelnemers;

    public Afspraak() {
        // verplichte lege constructor
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(KEY_ID, _id);
        map.put(KEY_BESCHRIJVING, beschrijving);
        map.put(KEY_BEGIN_TIJD, beginTijd);
        map.put(KEY_LOCATIE, locatie);
        map.put(KEY_DEELNEMERS, deelnemers);
        map.put(KEY_LOCATIE_ID, locatieId);
        return map;
    }



    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    @SuppressWarnings("unused")
    public String getLocatieId() {
        return locatieId;
    }

    public void setLocatieId(String locatieId) {
        this.locatieId = locatieId;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public long getBeginTijd() {
        return beginTijd;
    }

    public String getBeginTijdFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_TEMPLATE, Locale.getDefault());
        return sdf.format(getBeginTijd());
    }

    public void setBeginTijd(long beginTijd) {
        this.beginTijd = beginTijd;
    }

    public String getLocatie() {
        return locatie;
    }

    public void setLocatie(String locatie) {
        this.locatie = locatie;
    }

    public HashMap<String, Boolean> getDeelnemers() {
        return deelnemers;
    }

    public void setDeelnemers(HashMap<String, Boolean> deelnemers) {
        this.deelnemers = deelnemers;
    }

    @SuppressWarnings("unchecked")
    private Afspraak(Parcel in) {
        _id = in.readString();
        beschrijving = in.readString();
        beginTijd = in.readLong();
        locatie = in.readString();
        deelnemers = (HashMap<String, Boolean>) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(beschrijving);
        dest.writeLong(beginTijd);
        dest.writeString(locatie);
        dest.writeValue(deelnemers);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Afspraak> CREATOR = new Parcelable.Creator<Afspraak>() {
        @Override
        public Afspraak createFromParcel(Parcel in) {
            return new Afspraak(in);
        }

        @Override
        public Afspraak[] newArray(int size) {
            return new Afspraak[size];
        }
    };

    @Override
    public int compareTo(@NonNull Afspraak another) {

        if ((Long) this.beginTijd < (Long) another.beginTijd) {
            return -1;
        }
        if (this.beginTijd == another.beginTijd) {
            return 0;
        }
        return 1;
    }



}