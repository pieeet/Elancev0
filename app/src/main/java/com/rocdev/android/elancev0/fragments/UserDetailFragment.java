package com.rocdev.android.elancev0.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserDetailFragment.OnUserDetailFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailFragment extends BaseFragment {
    // the fragment initialization parameters
    private static final String ARG_USER = "user";

    private User user;

    private Context context;

    private OnUserDetailFragmentInteractionListener mListener;

    //Views
    private View rootView;
    private EditText voornaamEditText;
    private EditText achternaamEditText;
    private EditText emailEditText;
    private EditText telefoonEditText;
    private CheckBox isCoachCheckBox;
    private TableRow coacheesTitleRow;
    private TableRow coacheesCheckBoxesRow;
    private GridLayout coacheesGridLayout;
    private CheckBox[] coacheesCheckBoxes;
    private Button verwijderButton;
    private Button updateButton;
    private ImageView profielFoto;
    TextView warning;

    private ArrayList<User> coachees;
    private User coach;


    public UserDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @return A new instance of fragment UserDetailFragment.
     */
    // DONE: Rename and change types and number of parameters
    public static UserDetailFragment newInstance(@Nullable User user) {
        UserDetailFragment fragment = new UserDetailFragment();

        if (user != null) {
            Bundle args = new Bundle();
            args.putParcelable(ARG_USER, user);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
        }
        context = getActivity();
    }

    @Override
    public void setTitle() {
        if (user != null) {
            title = user.getNaam();
        } else {
            title = "Nieuwe gebruiker";
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_detail, container, false);
        this.rootView = rootView;
        if (user != null) {
            setUserListener();
        } else {
            initViews(rootView);
        }
        return rootView;
    }

    private void initViews(View view) {
        voornaamEditText = (EditText) view.findViewById(R.id.userDetailVoornaamEditText);
        achternaamEditText = (EditText) view.findViewById(R.id.userDetailAchternaamEditText);
        emailEditText = (EditText) view.findViewById(R.id.userDetailEmailEditText);
        telefoonEditText = (EditText) view.findViewById(R.id.userDetailTelefoonEditText);
        TableRow profielfotoTableRow = (TableRow) view.findViewById(R.id.profielfotoTableRow);
        TableRow coachTableRow = (TableRow) view.findViewById(R.id.coachTableRow);
        isCoachCheckBox = (CheckBox) view.findViewById(R.id.isCoachCheckBox);
        coacheesTitleRow = (TableRow) view.findViewById(R.id.coacheesTitleTableRow);
        updateButton = (Button) view.findViewById(R.id.updateUserButton);
        verwijderButton = (Button) view.findViewById(R.id.verwijderUserButton);
        warning = (TextView) view.findViewById(R.id.userDetailWarning);

        if (user != null) {
            voornaamEditText.setText(user.getNaam());
            achternaamEditText.setText(user.getAchternaam());
            emailEditText.setText(user.getEmail());
            profielFoto = (ImageView) view.findViewById(R.id.userDetailImageView);
            if (user.getPhotoUrl() != null) {
                Picasso.with(context).load(user.getPhotoUrl())
                        .into(profielFoto);
            } else {
                profielFoto.setImageDrawable(ContextCompat
                        .getDrawable(context,
                                R.drawable.ic_account_circle_black_36dp));
            }
            if (coach != null) {
                coachTableRow.setVisibility(View.VISIBLE);
                EditText coachEditText = (EditText) view.findViewById(R.id.userDetailCoachEditText);
                coachEditText.setText(coach.getNaam() + " " + coach.getAchternaam());
            } else {
                coachTableRow.setVisibility(View.GONE);
            }
            if (user.getTelefoon() != null) {
                telefoonEditText.setText(user.getTelefoon());
            }
            coacheesGridLayout = (GridLayout) rootView.findViewById(R.id.coacheesCheckBoxLayout);
            isCoachCheckBox.setChecked(user.getIsCoach());
            coacheesCheckBoxesRow = (TableRow) view.findViewById(R.id.coacheesCheckBoxesTableRow);
            setCoacheesCheckboxes();
            updateButton.setText("Update gebruiker");

        } else {
            verwijderButton.setVisibility(View.GONE);
            updateButton.setText("Maak gebruiker");
            coachTableRow.setVisibility(View.GONE);
            profielfotoTableRow.setVisibility(View.GONE);
            coacheesTitleRow.setVisibility(View.GONE);
        }
        initListeners();
    }


    private void setCoacheesCheckboxes() {
        if (user.getCoachees() != null) {
            coacheesTitleRow.setVisibility(View.VISIBLE);
            coacheesCheckBoxesRow.setVisibility(View.VISIBLE);
            coacheesGridLayout.removeAllViews();
            coacheesCheckBoxes = new CheckBox[user.getCoachees().size()];
            if (coachees == null || coachees.isEmpty()) {
                for (int i = 0; i < user.getCoachees().size(); i++) {
                    CheckBox checkBox = new CheckBox(context);
                    checkBox.setText(getString(R.string.text_loading));
                    checkBox.setChecked(true);
                    coacheesCheckBoxes[i] = checkBox;
                    coacheesGridLayout.addView(checkBox);
                }
            } else {
                for (int i = 0; i < coachees.size(); i++) {
                    CheckBox checkBox = new CheckBox(context);
                    checkBox.setText(coachees.get(i).getNaam());
                    checkBox.setChecked(true);
                    coacheesCheckBoxes[i] = checkBox;
                    coacheesGridLayout.addView(checkBox);
                }
            }
        } else {
            coacheesTitleRow.setVisibility(View.GONE);
            coacheesCheckBoxesRow.setVisibility(View.GONE);
            isCoachCheckBox.setClickable(true);
        }
    }

    private void initListeners() {

        if (user != null) {
            verwijderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteUser(user);
                }
            });
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newUser = false;
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
                if (user == null) {
                    user = new User();
                    newUser = true;
                }
                if (warnings.isEmpty()) {
                    user.setNaam(voornaam);
                    user.setAchternaam(achternaam);
                    user.setEmail(email);
                    user.setTelefoon(telefoon);
                    user.setIsCoach(isCoach);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String woonplaats = prefs.getString(KEY_NAAM_STAD,
                            DEFAULT_NAAM_STAD);
                    user.setPlaats(woonplaats);
                    if (mListener != null) {
                        if (newUser) {
                            user.set_id(email.replace('.', '_'));
                            mListener.onUserAdded(user);
                        } else {
                            ArrayList<User> exCoachees = null;
                            if (user.getCoachees() != null) {
                                exCoachees = new ArrayList<>();
                                for (int i = 0; i < coacheesCheckBoxes.length; i++) {
                                    if (!coacheesCheckBoxes[i].isChecked()) {
                                        exCoachees.add(coachees.get(i));
                                    }
                                }
                            }
                            mListener.onUpdateUser(user, exCoachees);
                        }

                    }
                } else {
                    warning.setVisibility(View.VISIBLE);
//                    for (String warning : warnings) {
//                    }
                }


            }
        });
        if (coacheesCheckBoxes != null) {
            for (CheckBox coacheesCheckBoxes : this.coacheesCheckBoxes) {
                coacheesCheckBoxes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean nietsGeselecteerd = true;
                        for (CheckBox checkbox : UserDetailFragment.this.coacheesCheckBoxes) {
                            if (checkbox.isChecked()) {
                                nietsGeselecteerd = false;
                                break;
                            }
                        }
                        if (nietsGeselecteerd) {
                            isCoachCheckBox.setClickable(true);
                        } else {
                            isCoachCheckBox.setChecked(true);
                            isCoachCheckBox.setClickable(false);
                        }
                    }
                });
            }
        }
        if (profielFoto != null) {
            profielFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Profielfoto")
                            .setMessage("Wil je je profielfoto veranderen of instellen?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String profielUrl = "https://aboutme.google.com/";
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(profielUrl));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });
        }

        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning.setVisibility(View.GONE);
            }
        });

    }


    private void onDeleteUser(User user) {
        if (mListener != null) {
            mListener.onDeleteUser(user);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserDetailFragmentInteractionListener) {
            mListener = (OnUserDetailFragmentInteractionListener) context;
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnUserDetailFragmentInteractionListener {
        void onDeleteUser(User user);
        void onUpdateUser(User user, @Nullable List<User> exCoachees);
        void onUserAdded(User user);
    }

    // Deze listener triggert eventueel de overige listeners
    private void setUserListener() {
        mDatabase
                .child(FB_USERS)
                .child(user.get_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getCoachees() != null) {
                        getCoachees();
                    } else {
                        if (user.getCoachId() != null) {
                            getCoach();

                        // user heeft geen coachees en geen coach
                        } else {
                            coach = null;
                            initViews(rootView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getCoachees() {
        coachees = new ArrayList<>();
        for (String keyCoachee : user.getCoachees().keySet()) {

            mDatabase
                    .child(FB_USERS)
                    .child(keyCoachee)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User coachee = dataSnapshot.getValue(User.class);
                    coachees.add(coachee);

                    if (user != null) {
                        if ((user.getCoachees() == null) ||
                                coachees.size() == user.getCoachees().size()) {
                            if (user.getCoachId() != null) {
                                getCoach();
                            // user heeft geen coach
                            } else {
                                initViews(rootView);
                            }

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }
    }

    private void getCoach() {
        mDatabase.child(FB_USERS).child(user.getCoachId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                coach = dataSnapshot.getValue(User.class);
                initViews(rootView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
