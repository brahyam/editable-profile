package com.dvipersquad.editableprofile.profile;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.data.LocationCoordinate;
import com.dvipersquad.editableprofile.data.User;

import javax.inject.Inject;

final class ProfilePresenter implements ProfileContract.Presenter {


    private static final User USER = new User(
            "qwqffqwgf13",
            "Brahyam Meneses",
            "Brahyam Meneses",
            "https://static1.businessinsider.de/image/589a1771dd0895cb6e8b4a11-400/slide.jpg",
            null,
            "Male",
            "Latino",
            "Christian",
            174.0,
            "Fit",
            "Single",
            "Engineer",
            "No description yet",
            new LocationCoordinate(0.0, 0.0));

    @Nullable
    private ProfileContract.View profileView;

    @Nullable
    private String userId;

    @Inject
    ProfilePresenter(@Nullable String userId) {
        this.userId = userId;
    }

    private void loadProfile() {
        if (profileView != null) {
            profileView.setLoadingIndicator(true);
        }

        profileView.showProfile(USER);

    }

    @Override
    public void takeView(ProfileContract.View profileFragment) {
        profileView = profileFragment;
        loadProfile();
    }

    @Override
    public void openEditProfile(@NonNull User user) {

    }

    @Override
    public void dropView() {
        profileView = null;
    }
}
