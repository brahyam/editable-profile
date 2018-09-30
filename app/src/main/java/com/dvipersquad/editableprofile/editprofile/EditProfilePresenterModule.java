package com.dvipersquad.editableprofile.editprofile;

import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.di.ActivityScoped;
import com.dvipersquad.editableprofile.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class EditProfilePresenterModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract EditProfileFragment editProfileFragment();

    @ActivityScoped
    @Binds
    abstract EditProfileContract.Presenter editProfilePresenter(EditProfilePresenter presenter);

    @Provides
    @ActivityScoped
    @Nullable
    static String provideEditProfileId(EditProfileActivity activity) {
        return activity.getIntent().getStringExtra(EditProfileActivity.EXTRA_PROFILE_ID);
    }
}
