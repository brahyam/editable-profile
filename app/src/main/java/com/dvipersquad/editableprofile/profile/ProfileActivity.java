package com.dvipersquad.editableprofile.profile;

import android.os.Bundle;

import com.dvipersquad.editableprofile.R;
import com.dvipersquad.editableprofile.utils.ActivityUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Displays profile screen
 */
public class ProfileActivity extends DaggerAppCompatActivity {
    public static final String EXTRA_PROFILE_ID = "PROFILE_ID";

    @Inject
    ProfileFragment injectedProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_act);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ProfileFragment profileFragment =
                (ProfileFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.contentFrame);

        if (profileFragment == null) {
            profileFragment = this.injectedProfileFragment;
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    profileFragment, R.id.contentFrame);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
