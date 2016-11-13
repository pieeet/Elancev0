package com.rocdev.android.elancev0.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.models.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAddUserFragmentInterActionListener} interface
 * to handle interaction events.
 * Use the {@link AddUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddUserFragment extends BaseFragment {

    EditText voornaamEditText;
    EditText achternaamEditText;
    EditText emailEditText;
    EditText telefoonEditText;
    CheckBox isCoachCheckBox;
    Button maakGebruikerButton;
    TextView warning;

    private User user;

    private OnAddUserFragmentInterActionListener mListener;

    public AddUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddUserFragment.
     */
    public static AddUserFragment newInstance(User user) {
        AddUserFragment fragment = new AddUserFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().get(KEY_USER) != null) {
            user = getArguments().getParcelable(KEY_USER);
        }
    }

    @Override
    public void setTitle() {
        title = "Nieuwe gebruiker";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_user, container, false);
        initViews(rootView);
        initListeners();
        return rootView;
    }

    private void initViews(View v) {
        voornaamEditText = (EditText) v.findViewById(R.id.voornaamEditText);
        achternaamEditText = (EditText) v.findViewById(R.id.achternaamEditText);
        emailEditText = (EditText) v.findViewById(R.id.emailEditText);

        telefoonEditText = (EditText) v.findViewById(R.id.telefoonEditText);
        isCoachCheckBox = (CheckBox) v.findViewById(R.id.isCoachCheckBox);
        maakGebruikerButton = (Button) v.findViewById(R.id.maakUserButton);
        warning = (TextView) v.findViewById(R.id.warning);

        if (user != null) {
            if (user.getEmail() != null) {
                emailEditText.setText(user.getEmail());
                emailEditText.setFocusable(false);
                emailEditText.setClickable(false);
            }
            if (user.getNaam() != null) {
                voornaamEditText.setText(user.getNaam());
                maakGebruikerButton.setText(getString(R.string.knop_bevestig));

            }
            if (user.getAchternaam() != null) {
                achternaamEditText.setText(user.getAchternaam());
            }
            if (user.getTelefoon() != null) {
                telefoonEditText.setText(user.getTelefoon());
            }
            if (user.getIsCoach()) {
                isCoachCheckBox.setChecked(true);
            }
        }




    }

    private void initListeners() {
        maakGebruikerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<String> warnings = new ArrayList<>();
                String voornaam = voornaamEditText.getText().toString();
                if (voornaam.equals("")) {
                    warnings.add("Vul een voornaam in");
                }
                String achternaam = achternaamEditText.getText().toString();
                if (achternaam.equals("")) {
                    warnings.add("Vul een achternaam in");
                }
                String email = emailEditText.getText().toString();
                if (email.equals("")) {
                    warnings.add("Vul een email in");
                }
                String telefoon = telefoonEditText.getText().toString();
                boolean isCoach = isCoachCheckBox.isChecked();


                if (warnings.isEmpty()) {

                    if (user == null) {
                        user = new User();
                        user.set_id(email.replace('.', '_'));
                    }
                    user.setNaam(voornaam);
                    user.setAchternaam(achternaam);
                    user.setEmail(email);

                    user.setTelefoon(telefoon);
                    user.setIsCoach(isCoach);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String woonplaats = prefs.getString(KEY_NAAM_STAD,
                            DEFAULT_NAAM_STAD);
                    user.setPlaats(woonplaats);
                    onUserAdded(user);


                } else {
                    warning.setVisibility(View.VISIBLE);
//                    for (String warning : warnings) {
//                    }
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


    public void onUserAdded(User user) {
        if (mListener != null) {
            mListener.onUserAdded(user);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddUserFragmentInterActionListener) {
            mListener = (OnAddUserFragmentInterActionListener) context;
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
    public interface OnAddUserFragmentInterActionListener {
        void onUserAdded(User user);
    }
}
