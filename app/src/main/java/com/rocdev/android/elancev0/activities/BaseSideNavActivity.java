package com.rocdev.android.elancev0.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.rocdev.android.elancev0.R;


/**
 * Created by piet on 25-10-16.
 * Baseklasse voor activities met zijmenu
 */

public abstract class BaseSideNavActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    static final int ACTIVITY_HOME = 0;
    static final int ACTIVITY_USERS = 1;
    static final int ACTIVITY_AFSPRAKEN = 2;
    static final int ACTIVITY_LOCATIES = 3;

    static int activityActive = ACTIVITY_HOME;

    NavigationView navigationView;

    int[] sidebarItems = {R.id.nav_home, R.id.nav_users, R.id.nav_afspraken, R.id.nav_attracties};


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        //Zijmenu NavigatieDrawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // tijd voor sidebar sluiten
        int delay = 200;
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                if (activityActive != ACTIVITY_HOME) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = new Intent(BaseSideNavActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }, delay);
                }
                return true;

            case R.id.nav_users:
                if (activityActive != ACTIVITY_USERS) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            startActivity(new Intent(BaseSideNavActivity.this,
                                    UsersActivity.class));
                            if (activityActive > 0) {
                                finish();
                            }
                        }
                    }, delay);
                }
                return true;

            case R.id.nav_afspraken:
                if (activityActive != ACTIVITY_AFSPRAKEN) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            startActivity(new Intent(BaseSideNavActivity.this,
                                    AfsprakenActivity.class));
                            if (activityActive > 0) {
                                finish();
                            }
                        }
                    }, delay);
                }
                return true;

            case R.id.nav_attracties:
                if (activityActive != ACTIVITY_LOCATIES) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            startActivity(new Intent(BaseSideNavActivity.this,
                                    LocatiesActivity.class));
                            if (activityActive > 0) {
                                finish();
                            }
                        }
                    }, delay);
                }
                return true;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(sidebarItems[activityActive]);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
