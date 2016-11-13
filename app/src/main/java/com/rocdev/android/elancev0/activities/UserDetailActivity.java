package com.rocdev.android.elancev0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.rocdev.android.elancev0.fragments.UserDetailFragment;
import com.rocdev.android.elancev0.models.User;

import java.util.List;

/**
 * Created by piet on 26-10-16.
 *
 */

public class UserDetailActivity extends BaseActivity implements
        UserDetailFragment.OnUserDetailFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_NO_DRAWER);
        Intent intent = getIntent();
        User user = intent.getParcelableExtra(KEY_USER);
        changeFragment(UserDetailFragment.newInstance(user));
    }

    @Override
    public void onDeleteUser(User user) {
        admin.deleteUser(user);
        if (mUser.get_id().equals(user.get_id())) {
            signOut();
        } else {
            fragmentManager.popBackStack();
            finish();
        }
    }

    @Override
    public void onUpdateUser(User user, @Nullable List<User> exCoachees) {
        admin.updateUser(user, exCoachees);
//        fragmentManager.popBackStack();
        finish();
    }
}
