package com.dvipersquad.editableprofile.di;

import com.dvipersquad.editableprofile.editprofile.EditProfileActivity;
import com.dvipersquad.editableprofile.editprofile.EditProfilePresenterModule;
import com.dvipersquad.editableprofile.profile.ProfileActivity;
import com.dvipersquad.editableprofile.profile.ProfilePresenterModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = ProfilePresenterModule.class)
    abstract ProfileActivity profileActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = EditProfilePresenterModule.class)
    abstract EditProfileActivity editProfileActivity();
}
