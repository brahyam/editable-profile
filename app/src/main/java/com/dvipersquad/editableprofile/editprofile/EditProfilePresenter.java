package com.dvipersquad.editableprofile.editprofile;

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

import javax.inject.Inject;

public class EditProfilePresenter implements EditProfileContract.Presenter {

    private ProfilesRepository profilesRepository;
    private CitiesRepository citiesRepository;
    private AttributesRepository attributesRepository;

    private AttributesDataSource.GetAttributeCallback getAttributeCallback =
            new AttributesDataSource.GetAttributeCallback() {
                @Override
                public void onAttributeLoaded(Attribute attribute) {
                    if (editProfileView == null || !editProfileView.isActive()) {
                        return;
                    }
                    editProfileView.showAttributes(Collections.singletonList(attribute));
                }

                @Override
                public void onDataNotAvailable(String message) {
                    if (editProfileView == null || !editProfileView.isActive()) {
                        return;
                    }
                    editProfileView.showErrorMessage(message);
                }
            };

    @Nullable
    private EditProfileContract.View editProfileView;

    @Nullable
    private String profileId;

    @Inject
    EditProfilePresenter(@Nullable String profileId, ProfilesRepository profilesRepository,
                         CitiesRepository citiesRepository,
                         AttributesRepository attributesRepository) {
        this.profileId = profileId;
        this.profilesRepository = profilesRepository;
        this.citiesRepository = citiesRepository;
        this.attributesRepository = attributesRepository;
    }

    @Override
    public void takeView(EditProfileContract.View profileFragment) {
        editProfileView = profileFragment;
        loadProfile();
    }

    private void loadProfile() {
        if (profileId == null) {
            if (editProfileView != null) {
                editProfileView.showMissingProfile();
            }
            return;
        }

        if (editProfileView != null) {
            editProfileView.setLoadingIndicator(true);
        }

        profilesRepository.getProfile(profileId, new ProfilesDataSource.GetProfileCallback() {
            @Override
            public void onProfileLoaded(final Profile profile) {
                if (editProfileView == null || !editProfileView.isActive()) {
                    return;
                }
                editProfileView.setLoadingIndicator(false);
                if (profile != null) {
                    editProfileView.showProfile(profile);
                    citiesRepository.getCity(
                            profile.getLocation().getLatitudeDMS(),
                            profile.getLocation().getLongitudeDMS(),
                            new CitiesDataSource.GetCityCallback() {
                                @Override
                                public void onCityLoaded(City city) {
                                    editProfileView.showCity(city.getName());
                                }

                                @Override
                                public void onDataNotAvailable(String message) {
                                    editProfileView.showErrorMessage(message);
                                }
                            });

                    attributesRepository.getAttribute(profile.getGenderId(), getAttributeCallback);
                    attributesRepository.getAttribute(profile.getEthnicityId(), getAttributeCallback);
                    attributesRepository.getAttribute(profile.getReligionId(), getAttributeCallback);
                    attributesRepository.getAttribute(profile.getFigureId(), getAttributeCallback);
                    attributesRepository.getAttribute(profile.getMaritalStatusId(), getAttributeCallback);
                }
            }

            @Override
            public void onDataNotAvailable(String message) {
                if (editProfileView == null || !editProfileView.isActive()) {
                    return;
                }
                editProfileView.showErrorMessage(message);
                editProfileView.showMissingProfile();
            }
        });

    }

    @Override
    public void dropView() {
        editProfileView = null;
    }
}
