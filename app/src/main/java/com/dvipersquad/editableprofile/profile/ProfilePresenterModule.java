package com.dvipersquad.editableprofile.profile;

import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.di.ActivityScoped;
import com.dvipersquad.editableprofile.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ProfilePresenterModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ProfileFragment profileFragment();

    @ActivityScoped
    @Binds
    abstract ProfileContract.Presenter profilePresenter(ProfilePresenter presenter);

    @Provides
    @ActivityScoped
    @Nullable
    static String provideUserId(ProfileActivity activity) {
        return activity.getIntent().getStringExtra(ProfileActivity.EXTRA_USER_ID);
    }

}
