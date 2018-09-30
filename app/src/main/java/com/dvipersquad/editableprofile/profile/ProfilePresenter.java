package com.dvipersquad.editableprofile.profile;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.data.source.ProfilesDataSource;
import com.dvipersquad.editableprofile.data.source.ProfilesRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

final class ProfilePresenter implements ProfileContract.Presenter {

    private static final String PROFILE_ID = "c5pQGk6vISfNAPd2";

    private ProfilesRepository profilesRepository;

    @Nullable
    private ProfileContract.View profileView;

    @Nullable
    private String profileId;

    private ProfilesDataSource.GetAttributeCallback getAttributeCallback = new ProfilesDataSource.GetAttributeCallback() {
        @Override
        public void onAttributeLoaded(Attribute attribute) {
            if (profileView == null || !profileView.isActive()) {
                return;
            }
            profileView.showAttributes(Collections.singletonList(attribute));
        }

        @Override
        public void onDataNotAvailable(String message) {
            if (profileView == null || !profileView.isActive()) {
                return;
            }
            profileView.showErrorMessage(message);
        }
    };

    private ProfilesDataSource.GetCityCallback getCityCallback = new ProfilesDataSource.GetCityCallback() {
        @Override
        public void onCityLoaded(City city) {
            if (profileView == null || !profileView.isActive()) {
                return;
            }
            profileView.showCity(city.getName());
        }

        @Override
        public void onDataNotAvailable(String message) {
            if (profileView == null || !profileView.isActive()) {
                return;
            }
            profileView.showErrorMessage(message);
        }
    };



    @Inject
    ProfilePresenter(@Nullable String profileId, ProfilesRepository profilesRepository) {
        this.profileId = PROFILE_ID; // Temporarily hardcoded to start the app.
//        this.profileId = profileId;
        this.profilesRepository = profilesRepository;
    }

    private void loadProfile() {
        if (profileId == null) {
            if (profileView != null) {
                profileView.showMissingProfile();
            }
            return;
        }

        if (profileView != null) {
            profileView.setLoadingIndicator(true);
        }

        profilesRepository.getProfile(profileId, new ProfilesDataSource.GetProfileCallback() {
            @Override
            public void onProfileLoaded(final Profile profile) {
                if (profileView == null || !profileView.isActive()) {
                    return;
                }
                profileView.setLoadingIndicator(false);
                if (profile != null) {
                    profileView.showProfile(profile);
                    profilesRepository.getCities(new ProfilesDataSource.GetCitiesCallback() {
                        @Override
                        public void onCitiesLoaded(List<City> cities) {
                            if (profileView == null || !profileView.isActive()) {
                                return;
                            }
                            profilesRepository.getCity(
                                    profile.getLocation().getLatitudeDMS(),
                                    profile.getLocation().getLongitudeDMS(),
                                    getCityCallback);
                        }

                        @Override
                        public void onDataNotAvailable(String message) {
                            if (profileView == null || !profileView.isActive()) {
                                return;
                            }
                            profileView.showErrorMessage(message);
                        }
                    });
                    profilesRepository.getAttributes(new ProfilesDataSource.GetAttributesCallback() {
                        @Override
                        public void onAttributesLoaded(List<Attribute> attributes) {
                            if (profileView == null || !profileView.isActive()) {
                                return;
                            }
                            profilesRepository.getAttribute(profile.getGenderId(), getAttributeCallback);
                            profilesRepository.getAttribute(profile.getEthnicityId(), getAttributeCallback);
                            profilesRepository.getAttribute(profile.getReligionId(), getAttributeCallback);
                            profilesRepository.getAttribute(profile.getMaritalStatusId(), getAttributeCallback);
                            profilesRepository.getAttribute(profile.getFigureId(), getAttributeCallback);
                        }

                        @Override
                        public void onDataNotAvailable(String message) {
                            if (profileView == null || !profileView.isActive()) {
                                return;
                            }
                            profileView.showErrorMessage(message);
                        }
                    });
                }
            }

            @Override
            public void onDataNotAvailable(String message) {
                if (profileView == null || !profileView.isActive()) {
                    return;
                }
                profileView.showErrorMessage(message);
                profileView.showMissingProfile();
            }
        });

    }

    @Override
    public void takeView(ProfileContract.View profileFragment) {
        profileView = profileFragment;
        loadProfile();
    }

    @Override
    public void openEditProfile(@NonNull Profile profile) {

    }

    @Override
    public void dropView() {
        profileView = null;
    }
}
