package com.rocdev.android.elancev0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


import com.rocdev.android.elancev0.R;
import com.rocdev.android.elancev0.fragments.AddUserFragment;
import com.rocdev.android.elancev0.fragments.UserDetailFragment;
import com.rocdev.android.elancev0.fragments.UsersFragment;
import com.rocdev.android.elancev0.models.User;

import java.util.List;

public class UsersActivity extends BaseSideNavActivity implements
        UsersFragment.OnFragmentInteractionListener,
        AddUserFragment.OnAddUserFragmentInterActionListener,
        UserDetailFragment.OnUserDetailFragmentInteractionListener {

    private UsersFragment usersFragment;
    private UserDetailFragment userDetailFragment;
    private AddUserFragment addUserFragment;
    private String fragmentTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_WITH_DRAWER);
        isTwoPane = findViewById(R.id.fragments_container) != null;
        fragmentTitle = getString(R.string.app_bar_titel_users);
        if (usersFragment == null) {
            usersFragment = UsersFragment.newInstance(mUser);
        }

        if (isTwoPane) {
            // eventueel oude fragment
            Fragment fragment = fragmentManager.findFragmentById(R.id.content_container);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
            clearRightFragment();

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_pane_left, usersFragment)
                    .commit();
        } else {
            changeFragment(usersFragment);
        }
    }



    @Override
    protected void onResume() {
        activityActive = ACTIVITY_USERS;
        super.onResume();

    }

    //Deze methode reageert op kliklistener listItem in UsersFragment
    @Override
    public void onUserSelected(User user) {
        if (isTwoPane) {
            userDetailFragment = UserDetailFragment.newInstance(user);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_pane_right, userDetailFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Intent intent = new Intent(this, UserDetailActivity.class);
            intent.putExtra(KEY_USER, user);
            startActivity(intent);
        }
    }

    @Override
    public void onNewUser() {
        if (isTwoPane) {
            addUserFragment = AddUserFragment.newInstance(null);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_pane_right, addUserFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            startActivity(new Intent(this, AddUserActivity.class));
        }
    }

    @Override
    public void onUserAdded(User user) {
        if (isTwoPane) {
            admin.addUser(user);
            clearRightFragment();
            toolbar.setTitle(fragmentTitle);
        }
    }


    @Override
    public void onDeleteUser(User user) {
        if (isTwoPane) {
            admin.deleteUser(user);
            clearRightFragment();
            toolbar.setTitle(fragmentTitle);
        }
    }

    @Override
    public void onUpdateUser(User user, List<User> exCoachees) {
        if (isTwoPane) {
            admin.updateUser(user, exCoachees);
            clearRightFragment();
            toolbar.setTitle(fragmentTitle);
        }
    }

    @Override
    public void onBackPressed() {
        if (userDetailFragment != null || addUserFragment != null) {
            toolbar.setTitle(fragmentTitle);
        }
        super.onBackPressed();
    }
}
