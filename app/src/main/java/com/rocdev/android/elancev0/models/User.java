package com.rocdev.android.elancev0.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.rocdev.android.elancev0.interfaces.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by piet on 21-07-16.
 *
 */
public class User implements  Constants, Comparable<User>, Parcelable {

    private String naam;
    private String achternaam;
    private String plaats;
    private String telefoon;
    private String email;
    private String _id;
    private boolean isCoach;
    private boolean isCoachee;
    private boolean isAdmin;
    private String coachId;
    private String profielId;
    private String photoUrl;
    private String firebaseUid;
    private String token;


    //lijst met ids van coachees
    private HashMap<String, Boolean> coachees;

    //lijst met afspraakIds
    private HashMap<String, Long> afspraken;



    public Map<String, Object> valuesToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_ID, _id);
        map.put(KEY_NAAM, naam);
        map.put(KEY_USER_ACHTERNAAM, achternaam);
        map.put(KEY_USER_EMAIL, email);
        map.put(KEY_PLAATS, plaats);
        map.put(KEY_USER_TELEFOON, telefoon);
        map.put(KEY_USER_IS_COACH, isCoach);
        map.put(KEY_USER_COACH_ID, coachId);
        map.put(KEY_USER_IS_ADMIN, isAdmin);
        map.put(KEY_USER_PROFIEL_ID, profielId);
        map.put(KEY_AFSPRAKEN, afspraken);
        map.put(KEY_COACHEES, coachees);
        map.put(KEY_PHOTOURL_USER, photoUrl);
        map.put(KEY_FIREBASE_UID, firebaseUid);
        map.put(KEY_FIREBASE_TOKEN, token);
        return map;
    }



    public User(){

    }

//    public String getProfielId() {
//        return profielId;
//    }
//
//    public void setProfielId(String profielId) {
//        this.profielId = profielId;
//    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }


    @SuppressWarnings("unused")
    public String getPlaats() {
        return plaats;
    }

    public void setPlaats(String plaats) {
        this.plaats = plaats;
    }

    public String getTelefoon() {
        return telefoon;
    }

    public void setTelefoon(String telefoon) {
        this.telefoon = telefoon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, Boolean> getCoachees() {
        return coachees;
    }

//    public ArrayList<String> getCoacheesList() {
//        ArrayList<String> list = new ArrayList<>();
//        for (String key: coachees.keySet()) {
//            list.add(key);
//        }
//        return list;
//    }

    @SuppressWarnings("unused")
    public void setCoachees(HashMap<String, Boolean> coachees) {
        this.coachees = coachees;
    }

    public String getCoachId() {
        return coachId;
    }

    @SuppressWarnings("unused")
    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean getIsCoach() {
        return isCoach;
    }

    public void setIsCoach(boolean coach) {
        isCoach = coach;
    }

    @SuppressWarnings("unused")
    public boolean getIsCoachee() {
        return isCoachee;
    }

    @SuppressWarnings("unused")
    public void setIsCoachee(boolean coachee) {
        isCoachee = coachee;
    }

    @SuppressWarnings("unused")
    public boolean getIsAdmin() {
        return isAdmin;
    }

    @SuppressWarnings("unused")
    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    public HashMap<String, Long> getAfspraken() {
        return afspraken;
    }

//    public ArrayList<String> getAfsprakenList() {
//        ArrayList<String> list = new ArrayList<>();
//        if (afspraken != null) {
//            for (String key : afspraken.keySet()) {
//                list.add(key);
//            }
//        }
//        return list;
//    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @SuppressWarnings("unused")
    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    @SuppressWarnings("unused")
    public void setAfspraken(HashMap<String, Long> afspraken) {
        this.afspraken = afspraken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    @Override
    public int compareTo(@NonNull User another) {
        return this.naam.compareToIgnoreCase(another.naam);

    }

    protected User(Parcel in) {
        naam = in.readString();
        achternaam = in.readString();
        plaats = in.readString();
        telefoon = in.readString();
        email = in.readString();
        _id = in.readString();
        isCoach = in.readByte() != 0x00;
        isCoachee = in.readByte() != 0x00;
        isAdmin = in.readByte() != 0x00;
        coachId = in.readString();
        profielId = in.readString();
        photoUrl = in.readString();
        firebaseUid = in.readString();
        token = in.readString();
        coachees = (HashMap) in.readValue(HashMap.class.getClassLoader());
        afspraken = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(naam);
        dest.writeString(achternaam);
        dest.writeString(plaats);
        dest.writeString(telefoon);
        dest.writeString(email);
        dest.writeString(_id);
        dest.writeByte((byte) (isCoach ? 0x01 : 0x00));
        dest.writeByte((byte) (isCoachee ? 0x01 : 0x00));
        dest.writeByte((byte) (isAdmin ? 0x01 : 0x00));
        dest.writeString(coachId);
        dest.writeString(profielId);
        dest.writeString(photoUrl);
        dest.writeString(firebaseUid);
        dest.writeString(token);
        dest.writeValue(coachees);
        dest.writeValue(afspraken);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}