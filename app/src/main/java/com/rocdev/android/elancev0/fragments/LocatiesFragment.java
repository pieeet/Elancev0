package com.rocdev.android.elancev0.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.adapters.LocatiesListAdapter;
import com.rocdev.android.elancev0.models.Locatie;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocatiesFragment extends BaseFragment {

    private String actualWaardeThema;
    private String actualWaardeStadsdeel;

    private ArrayList<Locatie> locaties;
    private ArrayList<Locatie> locatiesSelection;

    private ArrayList<String> stadsdelen;
    private ArrayList<String> themas;
    private ArrayList<String> locatiesListItems;

    ListView attractiesListView;
    Spinner stadsdeelSpinner;
    Spinner themaSpinner;
    FloatingActionButton fab;

    ChildEventListener locatiesListener;
    ValueEventListener themasListener;
    ValueEventListener stadsdelenListener;

    private ArrayAdapter<String> stadsdeelSpinnerAdapter;
    private ArrayAdapter<String> themaSpinnerAdapter;
    private LocatiesListAdapter locatiesListViewAdapter;

    private TextView meldingTextView;

    private OnAttractiesFragmentInteractionListener mListener;

    private boolean isLocatieWijzigen;
    private static final String ARG_IS_WIJZIGEN = "isWijzigen";


    public LocatiesFragment() {
        // Required empty public constructor
    }

    //factory methode om object te maken
    public static LocatiesFragment newInstance(boolean isLocatieWijzigen) {
        LocatiesFragment fragment = new LocatiesFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_WIJZIGEN, isLocatieWijzigen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isLocatieWijzigen = getArguments().getBoolean(ARG_IS_WIJZIGEN);
        }
        locatiesListItems = new ArrayList<>();
        locatiesSelection = new ArrayList<>();
        stadsdelen = new ArrayList<>();
        themas = new ArrayList<>();


        //Voor het geval ze later meer steden willen toevoegen
        //TODO een setting maken om een stad te kiezen

        actualWaardeThema = getString(R.string.DEFAULT_WAARDE_THEMA);
        actualWaardeStadsdeel = getString(R.string.DEFAULT_WAARDE_STADSDEEL);
        locaties = new ArrayList<>();

        // eerste waarde in spinner is "Kies een actualWaardeStadsdeel/thema"

        stadsdelen.add(getString(R.string.DEFAULT_WAARDE_STADSDEEL));
        themas.add(getString(R.string.DEFAULT_WAARDE_THEMA));
    }

    @Override
    public void setTitle() {
        title = "Locaties";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_locaties, container, false);
        initViews(rootView);
        initListeners();
        if (stadsdelenListener == null || themasListener == null) {
            fetchDataVoorSpinners();
        }
        if (locatiesListener == null) {
            getLocaties();
        }
        return rootView;
    }


    //haal de data
    private void fetchDataVoorSpinners() {
        //haal thema's
        this.getThemas();

        //haal stadsdelen
        this.getStadsdelen();
    }


    //maak de views en koppel de adapters
    private void initViews(View view) {

        //Message
        meldingTextView = (TextView) view.findViewById(R.id.meldingTextView);

        //FloatingActionButton
        fab = (FloatingActionButton) view.findViewById(R.id.fab_attracties);

        //LISTVIEW
        attractiesListView = (ListView) view.findViewById(R.id.attractiesListView);
        locatiesListViewAdapter = new LocatiesListAdapter(locatiesSelection, getActivity());
        attractiesListView.setAdapter(locatiesListViewAdapter);

        // STADSDEELSPINNER
        stadsdeelSpinner = (Spinner) view.findViewById(R.id.stadsdeelSpinner);
        stadsdeelSpinnerAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.layout_spinner, stadsdelen);
        stadsdeelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stadsdeelSpinner.setAdapter(stadsdeelSpinnerAdapter);

        //THEMASPINNER
        themaSpinner = (Spinner) view.findViewById(R.id.themaSpinner);
        themaSpinnerAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.layout_spinner, themas);
        themaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themaSpinner.setAdapter(themaSpinnerAdapter);
    }


    private void initListeners() {
        attractiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Locatie locatie = locatiesSelection.get(position);
                if (isLocatieWijzigen) {
                    onAttractieGewijzigd(locatie);
                } else {
                    onAttractieSelected(locatie);
                }
            }
        });

        stadsdeelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualWaardeStadsdeel = stadsdelen.get(position);
                updateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        themaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualWaardeThema = themas.get(position);
                updateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInitNieuweAttractie();
            }
        });
    }


    private void updateListView() {
        if (isAdded()) {
            locatiesSelection.clear();
            meldingTextView.setVisibility(View.GONE);
            for (Locatie locatie : locaties) {
                boolean isMatch = true;
                if (!actualWaardeStadsdeel.equals(getString(R.string.DEFAULT_WAARDE_STADSDEEL))) {
                    if (!actualWaardeStadsdeel.equals(locatie.getStadsdeel())) {
                        isMatch = false;
                    }
                }
                if (!actualWaardeThema.equals(getString(R.string.DEFAULT_WAARDE_THEMA))) {
                    boolean heeftThema = false;
                    for (String key : locatie.getThemas().keySet()) {
                        if (key.equals(actualWaardeThema)) {
                            heeftThema = true;
                            break;
                        }
                    }
                    if (!heeftThema) {
                        isMatch = false;
                    }
                }
                if (isMatch) {
                    locatiesSelection.add(locatie);
                }

            }
            Collections.sort(locatiesSelection);
            locatiesListItems.clear();
            for (Locatie locatie1 : locatiesSelection) {
                locatiesListItems.add(locatie1.getNaam());
            }
            locatiesListViewAdapter.notifyDataSetChanged();
        }
    }


    private void getThemas() {
        mDatabase.child(FB_THEMAS)
                .addListenerForSingleValueEvent(themasListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    themas.clear();
                    themas.add(getString(R.string.DEFAULT_WAARDE_THEMA));
                    for (DataSnapshot thema : dataSnapshot.getChildren()) {
                        themas.add(thema.getKey());
                    }
                    //update de spinner
                    themaSpinnerAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getStadsdelen() {
        mDatabase
                .child(FB_STEDEN)
                .child(naamStad)
                .child(FB_STADSDELEN).addListenerForSingleValueEvent(stadsdelenListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    stadsdelen.clear();
                    stadsdelen.add(getString(R.string.DEFAULT_WAARDE_STADSDEEL));
                    for (DataSnapshot stadsdeel : dataSnapshot.getChildren()) {
                        stadsdelen.add(stadsdeel.getKey());
                    }
                    //update de spinner
                    stadsdeelSpinnerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    private void getLocaties() {
        mDatabase.child(FB_LOCATIES)
                .orderByChild(KEY_PLAATS)
                .equalTo(naamStad)
                .addChildEventListener(locatiesListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        locaties.add(dataSnapshot.getValue(Locatie.class));
                        updateListView();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Locatie locatie = dataSnapshot.getValue(Locatie.class);
                        int index = 0;
                        for (Locatie l: locaties) {
                            if (l.get_id().equals(locatie.get_id())) {
                                locaties.set(index, locatie);
                                break;
                            }
                            index++;
                        }
                        updateListView();
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Locatie locatie = dataSnapshot.getValue(Locatie.class);
                        for (Locatie l: locaties) {
                            if (l.get_id().equals(locatie.get_id())) {
                                locaties.remove(l);
                                break;
                            }
                        }
                        updateListView();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

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
     * <p/>
     * Het idee is dat als gebruiker in de ListView op een attractie klikt er een
     * detailfragment wordt vertoond waarin de gegevens van een Locatie worden vertoond,
     * dus hij heeft in ieder geval de geselecteerde Locatie nodig.
     * Deze wordt doorgegeven via de Activity
     */
    public interface OnAttractiesFragmentInteractionListener {
        void onAttractieSelected(Locatie attractie);
        void onAttractieGewijzigd(Locatie attractie);
        void onInitNieuweAttractie();
    }

    private void onInitNieuweAttractie() {
        if (mListener != null) {
            mListener.onInitNieuweAttractie();
        }
    }

    private void onAttractieGewijzigd(Locatie locatie) {
        if (mListener != null) {
            mListener.onAttractieGewijzigd(locatie);
        }
    }

    private void onAttractieSelected(Locatie attractie) {
        if (mListener != null) {
            mListener.onAttractieSelected(attractie);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAttractiesFragmentInteractionListener) {
            mListener = (OnAttractiesFragmentInteractionListener) context;
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


}
