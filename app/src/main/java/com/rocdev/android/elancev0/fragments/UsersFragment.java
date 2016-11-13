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

import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.adapters.UserListAdapter;
import com.rocdev.android.elancev0.models.User;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends BaseFragment {

    private static final int ALLE_USERS = 0;
    private static final int ALLEEN_COACHES = 1;

    private OnFragmentInteractionListener mListener;
    private ArrayList<User> users;
    private ArrayList<User> usersSelection;
    private UserListAdapter listViewAdapter;
    private int spinnerItemSelected;
    private TextView meldingTextView;
    private FloatingActionButton fab;

//    private User user;

    private ChildEventListener listener;

    private String fragmentTitle = "Gebruikers";


    public UsersFragment() {
        // Required empty public constructor
    }

    public static UsersFragment newInstance(@Nullable User user) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER, user);
        fragment.setArguments(args);

        return new UsersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        users = new ArrayList<>();
        usersSelection = new ArrayList<>();
        spinnerItemSelected = ALLE_USERS;
    }

    @Override
    public void setTitle() {
        title = fragmentTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_users, container, false);
        initViews(rootView);
        if (listener == null) {
            downloadUserData();
        }
        return rootView;
    }

    public void changeTitle(String title) {
        fragmentTitle = title;

    }

    private void initViews(View view) {

        //Message
        meldingTextView = (TextView) view.findViewById(R.id.meldingTextView);

        //Spinner
        Spinner userSpinner = (Spinner) view.findViewById(R.id.userSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.layout_spinner,
                getResources().getStringArray(R.array.USER_SPINNER_ITEMS));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(spinnerAdapter);
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItemSelected = position;
                updateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Listview
        ListView usersListView = (ListView) view.findViewById(R.id.usersListView);
        listViewAdapter = new UserListAdapter(usersSelection, getActivity());
        usersListView.setAdapter(listViewAdapter);
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onUserSelected(position);
            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.fab_users);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNewUser();
            }
        });
    }

    private void updateListView() {
        if (isAdded()) {
            usersSelection.clear();
            meldingTextView.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            Collections.sort(users);

            // alle users worden getoond
            if (spinnerItemSelected == ALLE_USERS) {
                for (User user: users) {
                    usersSelection.add(user);
                }
            } else if (spinnerItemSelected == ALLEEN_COACHES) {
                for (User user : users) {
                    // alleen coaches worden getoond
                    if (user.getIsCoach()) {
                        usersSelection.add(user);
                    }
                }
            }

            listViewAdapter.notifyDataSetChanged();
        }

    }


    private void downloadUserData() {
        mDatabase.child(FB_USERS)
                .orderByChild(KEY_PLAATS)
                .equalTo(naamStad)
                .addChildEventListener(listener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                users.add(dataSnapshot.getValue(User.class));
                updateListView();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                int index = 0;
                for (User u: users) {
                    if (u.get_id().equals(user.get_id())) {
                        users.set(index, user);
                        break;
                    }
                    index++;
                }
                updateListView();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                for (User u: users) {
                    if (u.get_id().equals(user.get_id())) {
                        users.remove(u);
                        break;
                    }
                }
                updateListView();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }


    // hook method into UI event
    public void onUserSelected(int position) {
        User user = usersSelection.get(position);
        if (mListener != null) {
            mListener.onUserSelected(user);
        }
    }

    public void onNewUser() {
        if (mListener != null) {
            mListener.onNewUser();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public interface OnFragmentInteractionListener {
        // DONE: Update argument type and name
        void onUserSelected(User user);
        void onNewUser();
    }
}
