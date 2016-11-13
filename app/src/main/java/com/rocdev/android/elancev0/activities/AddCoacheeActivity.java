package com.rocdev.android.elancev0.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.rocdev.android.elancev0.fragments.UsersFragment;
import com.rocdev.android.elancev0.models.User;


public class AddCoacheeActivity extends BaseActivity implements
        UsersFragment.OnFragmentInteractionListener {

    UsersFragment usersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_NO_DRAWER);
        usersFragment = UsersFragment.newInstance(mUser);
        usersFragment.changeTitle("Koppel coachee");
        changeFragment(usersFragment);
    }


    @Override
    public void onUserSelected(final User user) {
        boolean koppelingAkkoord = true;

        if (user.get_id().equals(mUser.get_id())) {
            koppelingAkkoord = false;
            Toast.makeText(AddCoacheeActivity.this, "Je kunt niet coach van jezelf worden",
                    Toast.LENGTH_SHORT).show();
        }

        else if (user.getCoachId() != null) {
            koppelingAkkoord = false;
            if (user.getCoachId().equals(mUser.get_id())) {
                Toast.makeText(AddCoacheeActivity.this, "Je bent al coach van " + user.getNaam(),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddCoacheeActivity.this, "" + user.getNaam() + " heeft al een coach. " +
                                "Verwijder eerst coachee bij huidige coach.",
                        Toast.LENGTH_SHORT).show();
            }
        }

        if (koppelingAkkoord) {

            new AlertDialog.Builder(this)
                    .setTitle("Koppel coachee?")
                    .setMessage("Je wordt coach van " + user.getNaam() + " " + user.getAchternaam()
                            + " Is dat correct?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (mUser != null && mUser.get_id() != null) {
                                admin.addCoachee(user, mUser);
                                Toast.makeText(AddCoacheeActivity.this, "Je bent nu coach van "
                                        + user.getNaam(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddCoacheeActivity.this, "Er is iets misgegaan, " +
                                        "probeer nogmaals", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }

    }

    @Override
    public void onNewUser() {

    }


}
