package com.rocdev.android.elancev0.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.models.Locatie;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAddAttractieFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddLocatieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddLocatieFragment extends BaseFragment {

    private OnAddAttractieFragmentInteractionListener mListener;

    EditText naamEditText;
    EditText adresEditText;
    EditText postcodeEditText;
    EditText plaatsEditText;
    Spinner stadsdeelSpinner;
    GridLayout checkBoxLayout;
    Button maakLocatieButton;
    Button deleteLocatieButton;

    Locatie locatie;


    ArrayList<String> stadsdelen;
    ArrayList<String> themas;

    ArrayAdapter<String> stadsdeelAdapter;
    TextView warning;

    String actualWaardeStadsdeel;

    private static final String DEFAULT_STADSDEEL = "Kies een stadsdeel";
    private static final String KEY_ACTUAL_WAARDE_STADSDEEL = "actialWaardeStadsdeel";
    private static final String KEY_SELECTED_THEMAS = "selectedThemas";
    private static final int NOT_SELECTED = 0;
    private static final int SELECTED = 1;

    int[] checkBoxesSelected;
    CheckBox[] checkBoxes;



    public AddLocatieFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment AddLocatieFragment.
     */
    public static AddLocatieFragment newInstance(@Nullable Locatie locatie) {
        AddLocatieFragment fragment = new AddLocatieFragment();
        if (locatie != null) {
            Bundle args = new Bundle();
            args.putParcelable(KEY_LOCATIE, locatie);
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locatie = getArguments().getParcelable(KEY_LOCATIE);
        }
        if (savedInstanceState != null) {
            stadsdelen = savedInstanceState.getStringArrayList(KEY_STADSDELEN);
            themas = savedInstanceState.getStringArrayList(KEY_THEMAS);
            actualWaardeStadsdeel = savedInstanceState.getString(KEY_ACTUAL_WAARDE_STADSDEEL);
            checkBoxesSelected = savedInstanceState.getIntArray(KEY_SELECTED_THEMAS);

        } else {
            stadsdelen = new ArrayList<>();
            themas = new ArrayList<>();
            if (locatie == null) {
                actualWaardeStadsdeel = DEFAULT_STADSDEEL;
            } else {
                actualWaardeStadsdeel = locatie.getStadsdeel();
            }
        }

    }

    @Override
    public void setTitle() {
        if (locatie != null) {
            title = locatie.getNaam();
        } else {
            title = "Nieuwe locatie";
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_STADSDELEN, stadsdelen);
        outState.putStringArrayList(KEY_THEMAS, themas);
        outState.putString(KEY_ACTUAL_WAARDE_STADSDEEL, actualWaardeStadsdeel);
        outState.putIntArray(KEY_SELECTED_THEMAS, checkBoxesSelected);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_locatie, container, false);
        initViews(rootView);
        initListeners();
        return rootView;
    }


    private void initViews(View v) {
        naamEditText = (EditText) v.findViewById(R.id.naamAttractieEditText);
        adresEditText = (EditText) v.findViewById(R.id.adresLocatieEditText);
        postcodeEditText = (EditText) v.findViewById(R.id.postcodeLocatieEditText);
        plaatsEditText = (EditText) v.findViewById(R.id.plaatsLocatieEditText);
        plaatsEditText.setText(naamStad);
        stadsdeelSpinner = (Spinner) v.findViewById(R.id.stadsdeelLocatieSpinner);
        checkBoxLayout = (GridLayout) v.findViewById(R.id.themasCheckBoxLayout);

        if (themas.isEmpty()) {
            haalThemas();
        } else {
            maakThemaCheckBoxen();
        }
        maakLocatieButton = (Button) v.findViewById(R.id.maakLocatieButton);
        deleteLocatieButton = (Button) v.findViewById(R.id.verwijderLocatieButton);
        if (locatie != null) {
            naamEditText.setText(locatie.getNaam());
            adresEditText.setText(locatie.getAdres());
            postcodeEditText.setText(locatie.getPostcode());
            plaatsEditText.setText(locatie.getPlaats());
            maakLocatieButton.setText("Update locatie");
            deleteLocatieButton.setVisibility(View.VISIBLE);
        }
        stadsdeelAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.layout_spinner, stadsdelen);
        stadsdeelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        stadsdeelSpinner.setAdapter(stadsdeelAdapter);

        if (!actualWaardeStadsdeel.equals(DEFAULT_STADSDEEL)) {
            for (int i = 0; i < stadsdelen.size(); i++) {
                if (stadsdelen.get(i).equals(actualWaardeStadsdeel)) {
                    stadsdeelSpinner.setSelection(i);
                    break;
                }
            }
        }
        warning = (TextView) v.findViewById(R.id.warning_add_locatie);

        if (stadsdelen.isEmpty()) {
            stadsdelen.add(DEFAULT_STADSDEEL);
            haalStadsdelen();
        } else {
            setStadsdeelSpinnerItem();
        }
    }

    private void setStadsdeelSpinnerItem() {
        for (int i = 0; i < stadsdelen.size(); i++) {
            if (stadsdelen.get(i).equals(actualWaardeStadsdeel)) {
                stadsdeelSpinner.setSelection(i);
                break;
            }
        }
    }

    private void maakThemaCheckBoxen() {
        checkBoxes = new CheckBox[themas.size()];
        if (checkBoxesSelected == null) {
            checkBoxesSelected = new int[themas.size()];
            if (locatie != null) {
                for (int i = 0; i < themas.size(); i++) {
                    String thema = themas.get(i);
                    for (String themaKey: locatie.getThemas().keySet()) {
                        if (themaKey.equals(thema)) {
                            checkBoxesSelected[i] = SELECTED;
                        }
                    }
                }
            } else {
                for (int i = 0; i < checkBoxesSelected.length; i++) {
                    checkBoxesSelected[i] = NOT_SELECTED;
                }
            }
        }
        for (Integer i = 0; i < themas.size(); i++) {
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(themas.get(i));
            checkBox.setTag(i);
            checkBoxLayout.addView(checkBox);
            checkBoxes[i] = checkBox;
            if (checkBoxesSelected[i] == SELECTED) {
                checkBoxes[i].setChecked(true);
            }
        }
        setCheckBoxListeners();
    }

    private void setCheckBoxListeners() {
        for (CheckBox checkBoxe : checkBoxes) {
            checkBoxe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View checkBox) {
                    Integer indexTag = (Integer) checkBox.getTag();
                    if (checkBoxes[indexTag].isChecked()) {
                        checkBoxesSelected[indexTag] = SELECTED;
                    } else {
                        checkBoxesSelected[indexTag] = NOT_SELECTED;
                    }
                }
            });
        }
    }

    public void initListeners() {

        stadsdeelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualWaardeStadsdeel = stadsdelen.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning.setVisibility(View.GONE);
            }
        });
        maakLocatieButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (locatie == null) {
                    locatie = new Locatie();
                }
                String naam = naamEditText.getText().toString();
                String adres = adresEditText.getText().toString();
                String postcode = postcodeEditText.getText().toString();
                String plaats = plaatsEditText.getText().toString();
                HashMap<String, Boolean> themasMap = new HashMap<>();
                for (int i = 0; i < checkBoxesSelected.length; i++) {
                    if (checkBoxesSelected[i] == SELECTED) {
                        themasMap.put(themas.get(i), true);
                    }
                }

                //check op benodigde invoer
                if (naam.equals("")
                        || adres.equals("")
                        || actualWaardeStadsdeel.equals(DEFAULT_STADSDEEL)
                        || themasMap.isEmpty()) {
                    warning.setVisibility(View.VISIBLE);
                //bewaar de locatie
                } else {
                    if (mListener != null) {
                        locatie.setNaam(naam);
                        locatie.setAdres(adres);
                        locatie.setPostcode(postcode);
                        locatie.setThemas(themasMap);
                        locatie.setPlaats(plaats);
                        locatie.setStadsdeel(actualWaardeStadsdeel);
                        mListener.bewaarLocatie(locatie);
                    }
                }
            }
        });

        deleteLocatieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.deleteLocatie(locatie);
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddAttractieFragmentInteractionListener) {
            mListener = (OnAddAttractieFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddAttractieFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAddAttractieFragmentInteractionListener {
        void bewaarLocatie(Locatie attractie);
        void deleteLocatie(Locatie locatie);

    }

    private void haalStadsdelen() {
        mDatabase.child(FB_STEDEN)
                .child(naamStad)
                .child(FB_STADSDELEN).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    stadsdelen.clear();
                    stadsdelen.add(DEFAULT_STADSDEEL);
                    for (DataSnapshot stadsdeel: dataSnapshot.getChildren()) {
                        stadsdelen.add(stadsdeel.getKey());
                    }
                    stadsdeelAdapter.notifyDataSetChanged();
                    setStadsdeelSpinnerItem();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void haalThemas() {
        mDatabase.child(FB_THEMAS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    themas.clear();
                    for (DataSnapshot themaSnapshot : dataSnapshot.getChildren()) {
                        themas.add(themaSnapshot.getKey());
                    }
                    maakThemaCheckBoxen();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}
