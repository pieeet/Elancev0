package com.rocdev.android.elancev0.fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.models.Afspraak;
import com.rocdev.android.elancev0.models.Locatie;
import com.rocdev.android.elancev0.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddAfspraakFragment.OnNieuweAfspraakFragmentListener} interface
 * to handle interaction events.
 * Use the {@link AddAfspraakFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAfspraakFragment extends BaseFragment
        implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {



    private User user;
    private Date datum;
    private Date datumEnTijd;
    private Locatie locatie;

    private OnNieuweAfspraakFragmentListener mListener;

    LinearLayout checkBoxLinearLayout;
    EditText beschrijvingEditText;
    EditText lokatieEditText;
    EditText datumEditText;
    EditText tijdEditText;
    Button maakAfspraakButton;
    CheckBox[] coacheesCheckBoxes;
    TextView warning;

    ArrayList<User> coachees;
    View rootView;

    Context context;

    public AddAfspraakFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @return A new instance of fragment AddAfspraakFragment.
     */
    public static AddAfspraakFragment newInstance(User user) {
        AddAfspraakFragment fragment = new AddAfspraakFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(KEY_USER);
        }
        if (savedInstanceState != null) {
            locatie = savedInstanceState.getParcelable(KEY_LOCATIE);
            user = savedInstanceState.getParcelable(KEY_USER);
            coachees = savedInstanceState.getParcelableArrayList(KEY_COACHEES);
            if (savedInstanceState.get(KEY_DATUM) != null) {
                datum = new Date(savedInstanceState.getLong(KEY_DATUM));
            }
            if (savedInstanceState.get(KEY_DATUM_EN_TIJD) != null) {
                datumEnTijd = new Date(savedInstanceState.getLong(KEY_DATUM_EN_TIJD));
            }
        }
        context = getActivity();
    }

    @Override
    public void setTitle() {
        title = "Nieuwe afspraak";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LOCATIE, locatie);
        outState.putParcelable(KEY_USER, user);
        outState.putParcelableArrayList(KEY_COACHEES, coachees);
        if (datum != null) {
            outState.putLong(KEY_DATUM, datum.getTime());
        }
        if (datumEnTijd != null) {
            outState.putLong(KEY_DATUM_EN_TIJD, datumEnTijd.getTime());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nieuwe_afspraak, container, false);
        this.rootView = rootView;
        setUserListener();
        return rootView;
    }


    private void initViews(View view) {
        checkBoxLinearLayout = (LinearLayout) view.findViewById(R.id.checkBoxLayout);
        checkBoxLinearLayout.removeAllViews();
        coacheesCheckBoxes = new CheckBox[user.getCoachees().size()];
        if (coachees == null || coachees.isEmpty()) {
            for (int i = 0; i < user.getCoachees().size(); i++) {
                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(getString(R.string.text_loading));
                checkBox.setChecked(true);
                coacheesCheckBoxes[i] = checkBox;
                checkBoxLinearLayout.addView(checkBox);
            }
        } else {
            for (int i = 0; i < coachees.size(); i++) {
                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(coachees.get(i).getNaam());
                checkBox.setChecked(false);
                if (coachees.size() == 1) {
                    checkBox.setChecked(true);
                }
                coacheesCheckBoxes[i] = checkBox;
                checkBoxLinearLayout.addView(checkBox);
            }
        }

        beschrijvingEditText = (EditText) view.findViewById(R.id.beschrijvingEditText);
        lokatieEditText = (EditText) view.findViewById(R.id.locatieEditText);
        if (locatie != null) {
            lokatieEditText.setText(locatie.getNaam());
        }
        datumEditText = (EditText) view.findViewById(R.id.datumEditText);
        if (datum != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            datumEditText.setText(sdf.format(datum));
        }
        tijdEditText = (EditText) view.findViewById(R.id.timeEditText);
        if (datumEnTijd != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tijdEditText.setText(sdf.format(datumEnTijd));
        }
        maakAfspraakButton = (Button) view.findViewById(R.id.maakAfspraakButton);
        warning = (TextView) view.findViewById(R.id.warning);

        initListeners();
    }



    private void initListeners() {

        lokatieEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKiesLocatieSelected();
            }
        });

        datumEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.setTargetFragment(AddAfspraakFragment.this, 0);
                datePicker.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        tijdEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(AddAfspraakFragment.this, 0);
                timePicker.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        maakAfspraakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> warnings = new ArrayList<>();
                HashMap<String, Boolean> deelnemers = new HashMap<>();
                //voeg eerst gebruiker toe
                deelnemers.put(user.get_id(), true);

                String beschrijving = beschrijvingEditText.getText().toString();
                String locatieNaam = lokatieEditText.getText().toString();
                if (locatieNaam.equals("")) {
                    warnings.add("Geef een lokatie");
                }
                String datum = datumEditText.getText().toString();
                if (datum.equals("")) {
                    warnings.add("Geef een datum");
                }
                String tijdstip = tijdEditText.getText().toString();
                if (tijdstip.equals("")) {
                    warnings.add("Geef een tijdstip");
                }
                boolean hasChecked = false;
                //voeg de coachee(s) toe
                for (int i = 0; i < coacheesCheckBoxes.length; i++) {
                    if (coacheesCheckBoxes[i].isChecked()) {
                        hasChecked = true;
                        deelnemers.put(coachees.get(i).get_id(),
                                true);
                    }
                }
                if (!hasChecked) {
                    warnings.add("Kies een deelnemer");
                }
                if (warnings.isEmpty()) {
                    Afspraak afspraak = new Afspraak();
                    afspraak.setBeschrijving(beschrijving);
                    afspraak.setLocatie(locatieNaam);
                    afspraak.setLocatieId(locatie.get_id());
                    afspraak.setBeginTijd(datumEnTijd.getTime());
                    afspraak.setDeelnemers(deelnemers);
                    onMaakAfspraak(afspraak);
                } else {
                    String warningRegels = "Vul de volgende velden in:\n";
                    for (String w: warnings) {
                        warningRegels += "- " + w + "\n";
                    }
                    warningRegels += "Tik op het scherm om verder te gaan.";
                    warning.setText(warningRegels);
                    warning.setVisibility(View.VISIBLE);
                }

            }
        });
        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning.setVisibility(View.GONE);
            }
        });
    }

    private void getCoachees() {
        coachees = new ArrayList<>();
        for (String keyCoachee : user.getCoachees().keySet()) {

            DatabaseReference coacheeRef = mDatabase
                    .child(FB_USERS)
                    .child(keyCoachee);
            coacheeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User coachee = dataSnapshot.getValue(User.class);
                    coachees.add(coachee);
                    if (user != null) {
                        if (coachees.size() == user.getCoachees().size()) {
                            initViews(rootView);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        }
    }


    public void onMaakAfspraak(Afspraak afspraak) {
        if (mListener != null) {
            mListener.onMaakAfspraak(afspraak);
        }
    }

    public void onKiesLocatieSelected() {
        if (mListener != null) {
            mListener.onKiesLocatie();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddAfspraakFragment.OnNieuweAfspraakFragmentListener) {
            mListener = (OnNieuweAfspraakFragmentListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddUserFragmentInterActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setLocatie(Locatie locatie) {
        this.locatie = locatie;
        lokatieEditText.setText(locatie.getNaam());
    }

    //Date en Time pickers

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        datum = cal.getTime();
        if (datumEnTijd != null) {
            Calendar datumTijdCal = Calendar.getInstance();
            datumTijdCal.setTime(datumEnTijd);
            datumTijdCal.set(year, monthOfYear, dayOfMonth);
            datumEnTijd = datumTijdCal.getTime();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        datumEditText.setText(sdf.format(datum));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        if (datum != null) {
            calendar.setTime(datum);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        datumEnTijd = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tijdEditText.setText(sdf.format(datumEnTijd));
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnNieuweAfspraakFragmentListener {
        void onKiesLocatie();
        void onMaakAfspraak(Afspraak afspraak);
    }




    private void setUserListener() {
        DatabaseReference userRef = mDatabase
                .child(FB_USERS)
                .child(user.get_id());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getCoachees() != null) {
                        getCoachees();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


}
