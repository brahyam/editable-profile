package com.dvipersquad.editableprofile.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.Profile;

import java.util.List;

/**
 * Entry point for accessing data
 */
public interface ProfilesDataSource {

    interface GetProfileCallback {

        void onProfileLoaded(Profile profile);

        void onDataNotAvailable(String message);
    }

    interface ModifyProfileCallback {
        void onProfileModified(Profile profile);

        void onOperationFailed(String message);
    }

    void getProfile(@NonNull String profileId, @NonNull GetProfileCallback callback);

    void saveProfile(@NonNull Profile profile, @Nullable ModifyProfileCallback callback);

    void updateProfile(@NonNull Profile profile, @Nullable ModifyProfileCallback callback);

    void deleteProfile(@NonNull String profileId, @Nullable ModifyProfileCallback callback);

    interface GetAttributesCallback {

        void onAttributesLoaded(List<Attribute> attributes);

        void onDataNotAvailable(String message);

    }

    interface GetAttributeCallback {

        void onAttributeLoaded(Attribute attribute);

        void onDataNotAvailable(String message);
    }

    void getAttributes(@NonNull GetAttributesCallback callback);

    void getAttributesByType(@NonNull String type, GetAttributesCallback callback);

    void getAttribute(@NonNull String attributeId, @NonNull GetAttributeCallback callback);

    void saveAttribute(@NonNull Attribute attribute);

    void deleteAttribute(@NonNull String attributeId);

    void deleteAllAttributes();

    interface GetCitiesCallback {

        void onCitiesLoaded(List<City> cities);

        void onDataNotAvailable(String message);

    }

    interface GetCityCallback {

        void onCityLoaded(City city);

        void onDataNotAvailable(String message);
    }

    void getCities(@NonNull GetCitiesCallback callback);

    void getCity(@NonNull String latitude, @NonNull String longitude, @NonNull GetCityCallback callback);

    void saveCity(@NonNull City city);

    void deleteCity(@NonNull String latitude, @NonNull String longitude);

    void deleteAllCities();
}
