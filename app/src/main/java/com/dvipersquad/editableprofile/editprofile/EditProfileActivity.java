package com.dvipersquad.editableprofile.editprofile;

import android.os.Bundle;

import com.dvipersquad.editableprofile.R;
import com.dvipersquad.editableprofile.utils.ActivityUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class EditProfileActivity extends DaggerAppCompatActivity {

    public static final String EXTRA_PROFILE_ID = "PROFILE_ID";

    @Inject
    EditProfileFragment injectedProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile_act);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        EditProfileFragment editProfileFragment =
                (EditProfileFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.contentFrame);

        if (editProfileFragment == null) {
            editProfileFragment = this.injectedProfileFragment;
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    editProfileFragment, R.id.contentFrame);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
