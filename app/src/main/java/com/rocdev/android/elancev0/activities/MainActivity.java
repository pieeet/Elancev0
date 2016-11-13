package com.rocdev.android.elancev0.activities;

import android.os.Bundle;

import com.rocdev.android.elancev0.fragments.WelkomFragment;


/**
 *
 * @author piet on 22/7/2016
 */

public class MainActivity extends BaseSideNavActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_WITH_DRAWER);

        // om te vermijden dat je meerdere fragments over elkaar krijgt. SavedInstance restored
        //ook fragments
        if (savedInstanceState == null) {
            changeFragment(new WelkomFragment());
        }
    }

    @Override
    protected void onResume() {
        activityActive = ACTIVITY_HOME;
        super.onResume();

    }
}
