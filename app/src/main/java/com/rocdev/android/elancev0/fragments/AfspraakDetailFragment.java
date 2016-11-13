package com.rocdev.android.elancev0.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.models.Afspraak;
import com.rocdev.android.elancev0.models.Locatie;
import com.rocdev.android.elancev0.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AfspraakDetailFragment.OnAfspraakDetailFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AfspraakDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfspraakDetailFragment extends BaseFragment
        implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    // DONE: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_AFSPRAAK = "afspraak";

    // DONE: Rename and change types of parameters
    private Afspraak afspraak;

    private OnAfspraakDetailFragmentInteractionListener mListener;

    LinearLayout checkBoxLinearLayout;
    EditText beschrijvingEditText;
    EditText lokatieEditText;
    EditText datumEditText;
    EditText tijdEditText;
    Button updateAfspraakButton;
    Button verwijderAfspraakButton;
    CheckBox[] deelnemersCheckBoxes;

    ArrayList<User> deelnemers;

    Context context;

    Locatie locatie;
    private Date datum;
    private Date datumEnTijd;



    public AfspraakDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param afspraak de afspraak.
     * @return A new instance of fragment AfspraakDetailFragment.
     */
    // DONE: Rename and change types and number of parameters
    public static AfspraakDetailFragment newInstance(Afspraak afspraak) {
        AfspraakDetailFragment fragment = new AfspraakDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_AFSPRAAK, afspraak);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LOCATIE, locatie);
        outState.putParcelableArrayList(KEY_DEELNEMERS, deelnemers);
        if (datum != null) {
            outState.putLong(KEY_DATUM, datum.getTime());
        }
        if (datumEnTijd != null) {
            outState.putLong(KEY_DATUM_EN_TIJD, datumEnTijd.getTime());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            afspraak = getArguments().getParcelable(ARG_AFSPRAAK);
        }
        if (savedInstanceState != null) {
            locatie = savedInstanceState.getParcelable(KEY_LOCATIE);
            deelnemers = savedInstanceState.getParcelableArrayList(KEY_DEELNEMERS);
            if (savedInstanceState.get(KEY_DATUM) != null) {
                datum = new Date(savedInstanceState.getLong(KEY_DATUM));
            }
            if (savedInstanceState.get(KEY_DATUM_EN_TIJD) != null) {
                datumEnTijd = new Date(savedInstanceState.getLong(KEY_DATUM_EN_TIJD));
            }
        } else {
            if (afspraak != null) {
                datum = new Date(afspraak.getBeginTijd());
                datumEnTijd = new Date(afspraak.getBeginTijd());
            }


        }

        context = getActivity();
    }

    @Override
    public void setTitle() {
        title = "Afspraak";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_nieuwe_afspraak, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View view) {
        checkBoxLinearLayout = (LinearLayout) view.findViewById(R.id.checkBoxLayout);
        setDeelnemersCheckBoxes();
        getDeelnemers();
        beschrijvingEditText = (EditText) view.findViewById(R.id.beschrijvingEditText);
        beschrijvingEditText.setText(afspraak.getBeschrijving());
        lokatieEditText = (EditText) view.findViewById(R.id.locatieEditText);
        if (locatie == null) {
            lokatieEditText.setText(afspraak.getLocatie());
        } else {
            lokatieEditText.setText(locatie.getNaam());
        }
        datumEditText = (EditText) view.findViewById(R.id.datumEditText);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        datumEditText.setText(sdf.format(datum));
        tijdEditText = (EditText) view.findViewById(R.id.timeEditText);
        sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tijdEditText.setText(sdf.format(datumEnTijd));
        updateAfspraakButton = (Button) view.findViewById(R.id.maakAfspraakButton);
        updateAfspraakButton.setText(getString(R.string.knop_update_afspraak));
        verwijderAfspraakButton = (Button) view.findViewById(R.id.verwijderAfspraakButton);
        verwijderAfspraakButton.setVisibility(View.VISIBLE);
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
                DialogFragment datePicker = DatePickerFragment.newInstance(datum.getTime());
                datePicker.setTargetFragment(AfspraakDetailFragment.this, 0);
                datePicker.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        tijdEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = TimePickerFragment.newInstance(datum.getTime());
                timePicker.setTargetFragment(AfspraakDetailFragment.this, 0);
                timePicker.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });


        updateAfspraakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afspraak.setBeschrijving(beschrijvingEditText.getText().toString());
                if (locatie != null) {
                    afspraak.setLocatie(locatie.getNaam());
                    afspraak.setLocatieId(locatie.get_id());
                }
                afspraak.setBeginTijd(datumEnTijd.getTime());
                onUpdateAfspraak(afspraak);

            }
        });
        beschrijvingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        verwijderAfspraakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteAfspraak(afspraak);
            }
        });
    }

    public void onKiesLocatieSelected() {
        if (mListener != null) {
            mListener.onWijzigLocatieVanAfspraak();
        }
    }

    private void setDeelnemersCheckBoxes() {
        checkBoxLinearLayout.removeAllViews();
        deelnemersCheckBoxes = new CheckBox[afspraak.getDeelnemers().size()];
        if (deelnemers == null || deelnemers.isEmpty()) {
            for (int i = 0; i < afspraak.getDeelnemers().size(); i++) {
                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(getString(R.string.text_loading));
                checkBox.setChecked(true);
                checkBox.setClickable(false);
                deelnemersCheckBoxes[i] = checkBox;
                checkBoxLinearLayout.addView(checkBox);
            }
        } else {
            for (int i = 0; i < deelnemers.size(); i++) {
                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(deelnemers.get(i).getNaam());
                checkBox.setChecked(true);
                checkBox.setClickable(false);
                deelnemersCheckBoxes[i] = checkBox;
                checkBoxLinearLayout.addView(checkBox);
            }
        }
    }

    public void onUpdateAfspraak(Afspraak afspraak) {
        if (mListener != null) {
            mListener.onMaakAfspraak(afspraak);
        }
    }

    public void onDeleteAfspraak(Afspraak afspraak) {
        if (mListener != null) {
            mListener.onDeleteAfspraak(afspraak);
        }
    }

    public void setLocatie(Locatie locatie) {
        this.locatie = locatie;
        lokatieEditText.setText(locatie.getNaam());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAfspraakDetailFragmentInteractionListener) {
            mListener = (OnAfspraakDetailFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAfspraakDetailFragmentInteractionListener {
        void onDeleteAfspraak(Afspraak afspraak);
        void onMaakAfspraak(Afspraak afspraak);
        void onWijzigLocatieVanAfspraak();
    }

    private void getDeelnemers() {
        deelnemers = new ArrayList<>();
        for (String deelnemerKey : afspraak.getDeelnemers().keySet()) {
            mDatabase.child(FB_USERS).child(deelnemerKey)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User deelnemer = dataSnapshot.getValue(User.class);
                    deelnemers.add(deelnemer);
                    if (deelnemers.size() == afspraak.getDeelnemers().size()) {
                        setDeelnemersCheckBoxes();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
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

}
