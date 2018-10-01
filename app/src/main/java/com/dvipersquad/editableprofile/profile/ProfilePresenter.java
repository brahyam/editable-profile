package com.dvipersquad.editableprofile.profile;


import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.data.source.AttributesDataSource;
import com.dvipersquad.editableprofile.data.source.AttributesRepository;
import com.dvipersquad.editableprofile.data.source.CitiesDataSource;
import com.dvipersquad.editableprofile.data.source.CitiesRepository;
import com.dvipersquad.editableprofile.data.source.ProfilesDataSource;
import com.dvipersquad.editableprofile.data.source.ProfilesRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

final class ProfilePresenter implements ProfileContract.Presenter {

    private ProfilesRepository profilesRepository;
    private CitiesRepository citiesRepository;
    private AttributesRepository attributesRepository;

    @Nullable
    private ProfileContract.View profileView;

    @Nullable
    private String profileId;

    private Profile activeProfile;

    private AttributesDataSource.GetAttributeCallback getAttributeCallback =
            new AttributesDataSource.GetAttributeCallback() {
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

    private CitiesDataSource.GetCityCallback getCityCallback =
            new CitiesDataSource.GetCityCallback() {
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
    ProfilePresenter(@Nullable String profileId, ProfilesRepository profilesRepository,
                     CitiesRepository citiesRepository,
                     AttributesRepository attributesRepository) {
        this.profileId = profileId;
        this.profilesRepository = profilesRepository;
        this.citiesRepository = citiesRepository;
        this.attributesRepository = attributesRepository;
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
                    activeProfile = profile;
                    citiesRepository.getCities(new CitiesDataSource.LoadCitiesCallback() {
                        @Override
                        public void onCitiesLoaded(List<City> cities) {
                            if (profileView == null || !profileView.isActive()) {
                                return;
                            }
                            citiesRepository.getCity(
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
                    attributesRepository.getAttributes(new AttributesDataSource.LoadAttributesCallback() {
                        @Override
                        public void onAttributesLoaded(List<Attribute> attributes) {
                            if (profileView == null || !profileView.isActive()) {
                                return;
                            }
                            attributesRepository.getAttribute(profile.getGenderId(), getAttributeCallback);
                            attributesRepository.getAttribute(profile.getEthnicityId(), getAttributeCallback);
                            attributesRepository.getAttribute(profile.getReligionId(), getAttributeCallback);
                            attributesRepository.getAttribute(profile.getFigureId(), getAttributeCallback);
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
    public void showImageSelectionDialog() {
        if (profileView != null) {
            profileView.showImageSelectionUI();
        }
    }

    @Override
    public void onImageSelected(final String path) {
        profilesRepository.saveProfileImage(path, new ProfilesDataSource.SaveProfileImageCallback() {
            @Override
            public void onProfileImageSaved(String imageUrl) {
                if (activeProfile != null && profileView != null) {
                    activeProfile.setProfilePictureUrl(imageUrl);
                    profilesRepository.updateProfile(activeProfile, null);
                    profileView.showProfileImage(imageUrl);
                }
            }

            @Override
            public void onOperationFailed(String message) {
                if (profileView != null) {
                    profileView.showErrorMessage(message);
                }
            }
        });
    }

    @Override
    public void showEditProfileView() {
        if (profileView == null || !profileView.isActive()) {
            return;
        }
        profileView.showEditProfileUI(profileId);
    }

    @Override
    public void dropView() {
        profileView = null;
    }
}
