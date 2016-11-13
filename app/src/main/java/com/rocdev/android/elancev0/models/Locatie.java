package com.rocdev.android.elancev0.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.rocdev.android.elancev0.interfaces.Constants;

import java.util.HashMap;


/**
 * Created by piet on 19-07-16.
 *
 * Parcelable is er voor om data tussen activities en fragments etc te kunnen
 * doorgeven et intents. Je kunt van een model klasse eenvoudig een Parcelable klasse maken
 * met de tool
 * http://www.parcelabler.com/
 */
public class Locatie implements Constants, Comparable<Locatie>, Parcelable {

    // _id is de Firebase key. Die heb je bijvoorbeeld nodig als je de waarde van de attractie
    // wilt veranderen
    private String _id;
    private String naam;
    private String adres;
    private String postcode;
    private String plaats;
    private String stadsdeel;

    private HashMap<String, Boolean> themas;

    // lege constructor
    public Locatie() {
    }


//    //Constructor zonder _id Gebruik de setter voor _id als je de _id in het object wilt opslaan
//    public Locatie(String naam, String adres, String postcode, String plaats) {
//        this.naam = naam;
//        this.adres = adres;
//        this.postcode = postcode;
//        this.plaats = plaats;
//
//    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(KEY_ID, _id);
        map.put(KEY_ADRES, adres) ;
        map.put(KEY_NAAM, naam);
        map.put(KEY_POSTCODE, postcode);
        map.put(KEY_PLAATS, plaats);
        map.put(KEY_STADSDEEL, stadsdeel);
        map.put(KEY_THEMAS, themas);
        return map;

    }



    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    @SuppressWarnings("unused")
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }


    @SuppressWarnings("unused")public String getPlaats() {
        return plaats;
    }

    public void setPlaats(String plaats) {
        this.plaats = plaats;
    }

    public String getStadsdeel() {
        return stadsdeel;
    }

    public void setStadsdeel(String stadsdeel) {
        this.stadsdeel = stadsdeel;
    }

    public HashMap<String, Boolean> getThemas() {
        return themas;
    }

    public void setThemas(HashMap<String, Boolean> themas) {
        this.themas = themas;
    }


    @Override
    public int compareTo(@NonNull Locatie another) {
        return this.naam.compareToIgnoreCase(another.naam);
    }

    protected Locatie(Parcel in) {
        _id = in.readString();
        naam = in.readString();
        adres = in.readString();
        postcode = in.readString();
        plaats = in.readString();
        stadsdeel = in.readString();
        themas = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(naam);
        dest.writeString(adres);
        dest.writeString(postcode);
        dest.writeString(plaats);
        dest.writeString(stadsdeel);
        dest.writeValue(themas);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Locatie> CREATOR = new Parcelable.Creator<Locatie>() {
        @Override
        public Locatie createFromParcel(Parcel in) {
            return new Locatie(in);
        }

        @Override
        public Locatie[] newArray(int size) {
            return new Locatie[size];
        }
    };
}