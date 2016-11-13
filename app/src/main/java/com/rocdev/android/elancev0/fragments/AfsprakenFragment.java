package com.rocdev.android.elancev0.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.adapters.AfsprakenListAdapter;
import com.rocdev.android.elancev0.models.Afspraak;
import com.rocdev.android.elancev0.models.User;


import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AfsprakenFragment.OnAfsprakenFragmentEventListener} interface
 * to handle interaction events.
 * Use the {@link AfsprakenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfsprakenFragment extends BaseFragment {

    private OnAfsprakenFragmentEventListener mListener;
    private AfsprakenListAdapter afsprakenListAdapter;
    private User mUser;
    private ArrayList<Afspraak> mAfspraken;
    private TextView meldingTextView;
    private FloatingActionButton fab;
    private ChildEventListener afsprakenListener;
    private ListView afsprakenListView;


    public AfsprakenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @return A new instance of fragment AfsprakenFragment.
     */
    public static AfsprakenFragment newInstance(User user) {
        AfsprakenFragment fragment = new AfsprakenFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = getArguments().getParcelable(KEY_USER);
            setmUserListener();
        }
        mAfspraken = new ArrayList<>();
        afsprakenListAdapter = new AfsprakenListAdapter(mAfspraken, getActivity());

    }

    @Override
    public void setTitle() {
        title = "Afspraken";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_afspraken, container, false);
        initViews(rootView);
        initListeners();
        if (afsprakenListener == null) {
            setAfsprakenListener();
        } else {
            meldingTextView.setVisibility(View.GONE);
        }

        return rootView;
    }

    private void initViews(View view) {
        meldingTextView = (TextView) view.findViewById(R.id.meldingTextView);
        afsprakenListView = (ListView) view.findViewById(R.id.afsprakenListView);
        afsprakenListView.setAdapter(afsprakenListAdapter);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_afspraken);

    }

    private void initListeners() {


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInitNieuweAfspraak(mUser);
            }
        });



        afsprakenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Afspraak afspraak = mAfspraken.get(position);
                onAfspraakSelected(afspraak);
            }
        });

    }


    private void setAfsprakenListener() {

        mDatabase
                .child(FB_USERS)
                .child(mUser.get_id())
                .child(KEY_AFSPRAKEN)
                .addChildEventListener(afsprakenListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String afspraakKey = dataSnapshot.getKey();
                mDatabase
                        .child(FB_AFSPRAKEN)
                        .child(afspraakKey)
                        .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // afspraak kan verwijderd zijn
                        if (dataSnapshot.getValue() != null) {
                            Afspraak afspraak = dataSnapshot.getValue(Afspraak.class);
                            if (isAdded()) {
                                updateListView(afspraak);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                    private void updateListView(Afspraak afspraak) {
                        // Afspraken worden alleen in de lijst getoond wanneer ze > 1uur later zijn dan huidige tijd
//                        if (afspraak.getBeginTijd() >
//                                (new Date().getTime() - 1000 * 60 * 60)) {

                            boolean isInList = false;
                            for (int i = 0; i < mAfspraken.size(); i++) {
                                if (mAfspraken.get(i).get_id().equals(afspraak.get_id())) {
                                    isInList = true;
                                    mAfspraken.set(i, afspraak);
                                    break;
                                }
                            }
                            if (!isInList) {
                                mAfspraken.add(afspraak);
                                meldingTextView.setVisibility(View.GONE);
                            }
                            Collections.sort(mAfspraken);
                            afsprakenListAdapter.notifyDataSetChanged();
                    }

                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String afspraakKey = dataSnapshot.getKey();
                for (int i = 0; i < mAfspraken.size(); i++) {
                    if (mAfspraken.get(i).get_id().equals(afspraakKey)) {
                        mAfspraken.remove(i);
                        afsprakenListAdapter.notifyDataSetChanged();
                        break;
                    }

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void onAfspraakSelected(Afspraak afspraak) {
        if (mListener != null) {
            mListener.onAfspraakSelected(afspraak);
        }
    }

    //Deze methode wordt aangeroepen door de Floating Action Button
    public void onInitNieuweAfspraak(User user) {
        if (mListener != null)
            mListener.onInitNieuweAfspraak(user);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnAfsprakenFragmentEventListener) {
            mListener = (OnAfsprakenFragmentEventListener) context;
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
    public interface OnAfsprakenFragmentEventListener {
        void onAfspraakSelected(Afspraak afspraak);
        void onInitNieuweAfspraak(User user);
    }

    private void setmUserListener() {
        mDatabase.child(FB_USERS).child(mUser.get_id())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
