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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class EditProfilePresenter implements EditProfileContract.Presenter {

    private ProfilesRepository profilesRepository;
    private CitiesRepository citiesRepository;
    private AttributesRepository attributesRepository;

    private Map<String, Attribute> activeAttributes = new LinkedHashMap<>();
    private City activeCity;

    private AttributesDataSource.GetAttributeCallback getAttributeCallback =
            new AttributesDataSource.GetAttributeCallback() {
                @Override
                public void onAttributeLoaded(Attribute attribute) {
                    if (editProfileView == null || !editProfileView.isActive()) {
                        return;
                    }
                    editProfileView.showAttributes(Collections.singletonList(attribute));
                    activeAttributes.put(attribute.getType(), attribute);
                }

                @Override
                public void onDataNotAvailable(String message) {
                    if (editProfileView == null || !editProfileView.isActive()) {
                        return;
                    }
                    editProfileView.showErrorMessage(message);
                }
            };

    private CitiesDataSource.GetCityCallback getCityCallback =
            new CitiesDataSource.GetCityCallback() {
                @Override
                public void onCityLoaded(City city) {
                    if (editProfileView == null || !editProfileView.isActive()) {
                        return;
                    }
                    editProfileView.showCity(city.getName());
                    activeCity = city;
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
    private Profile activeProfile;

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

    @Override
    public void modifyField(final String attributeType) {
        if (editProfileView == null || !editProfileView.isActive()) {
            return;
        }
        switch (attributeType) {
            case Attribute.TYPE_DISPLAY_NAME:
            case Attribute.TYPE_REAL_NAME:
            case Attribute.TYPE_OCUPATION:
                break;
            case Attribute.TYPE_BIRTHDAY:
                break;
            case Attribute.TYPE_ETHNICITY:
            case Attribute.TYPE_RELIGION:
            case Attribute.TYPE_FIGURE:
                showSingleChoiceUI(attributeType, false);
                break;
            case Attribute.TYPE_GENDER:
            case Attribute.TYPE_MARITAL_STATUS:
                showSingleChoiceUI(attributeType, true);
                break;
            case Attribute.TYPE_ABOUT_ME:
                break;
            case Attribute.TYPE_LOCATION:
                break;
        }
    }

    private void showSingleChoiceUI(final String attributeType, boolean mandatory) {
        if (editProfileView == null || !editProfileView.isActive()) {
            return;
        }
        attributesRepository.getAttributesByType(attributeType, new AttributesDataSource.GetAttributesCallback() {
            @Override
            public void onAttributesLoaded(List<Attribute> attributes) {
                editProfileView.showEditSingleChoiceUI(attributes, activeAttributes.get(attributeType), true);
            }

            @Override
            public void onDataNotAvailable(String message) {
                editProfileView.showErrorMessage(message);
            }
        });
    }

    @Override
    public void attributeSelected(Attribute attribute) {
        if (activeProfile == null || attribute == null || editProfileView == null) {
            return;
        }

        switch (attribute.getType()) {
            case Attribute.TYPE_DISPLAY_NAME:
                activeProfile.setDisplayName(attribute.getName());
                break;
            case Attribute.TYPE_REAL_NAME:
                activeProfile.setRealName(attribute.getName());
                break;
            case Attribute.TYPE_OCUPATION:
                activeProfile.setOccupation(attribute.getName());
                break;
//            Handled on method selectedDate
//            case Attribute.TYPE_BIRTHDAY:
//                break;
            case Attribute.TYPE_GENDER:
                activeProfile.setGenderId(attribute.getId());
                break;
            case Attribute.TYPE_ETHNICITY:
                activeProfile.setEthnicityId(attribute.getId());
                break;
            case Attribute.TYPE_RELIGION:
                activeProfile.setReligionId(attribute.getId());
                break;
            case Attribute.TYPE_FIGURE:
                activeProfile.setFigureId(attribute.getId());
                break;
            case Attribute.TYPE_MARITAL_STATUS:
                activeProfile.setMaritalStatusId(attribute.getId());
                break;
            case Attribute.TYPE_ABOUT_ME:
                activeProfile.setAboutMe(attribute.getName());
                break;
//            Handled on method locationSelected
//            case Attribute.TYPE_LOCATION:
//                break;
        }
        activeAttributes.put(attribute.getType(), attribute);
        profilesRepository.updateProfile(activeProfile, null);
        editProfileView.showAttributes(Collections.singletonList(attribute));
    }

    @Override
    public void dateSelected(Date date) {

    }

    @Override
    public void locationSelected(City city) {

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
                    activeProfile = profile;
                    editProfileView.showProfile(profile);
                    citiesRepository.getCity(
                            profile.getLocation().getLatitudeDMS(),
                            profile.getLocation().getLongitudeDMS(),
                            getCityCallback);

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
