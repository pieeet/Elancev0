package com.rocdev.android.elancev0.activities;

import android.os.Bundle;

import com.rocdev.android.elancev0.fragments.AddUserFragment;
import com.rocdev.android.elancev0.models.User;

/**
 * Created by piet on 27-10-16.
 * Voor single pane layout gebruiker toevoegen
 */

public class AddUserActivity extends BaseActivity implements
        AddUserFragment.OnAddUserFragmentInterActionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_NO_DRAWER);
        if (savedInstanceState == null) {
            changeFragment(AddUserFragment.newInstance(null));
        }
    }

    @Override
    public void onUserAdded(User user) {
        admin.addUser(user);
        finish();
    }


}
