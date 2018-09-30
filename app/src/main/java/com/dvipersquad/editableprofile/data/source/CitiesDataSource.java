package com.dvipersquad.editableprofile.data.source;

import android.support.annotation.NonNull;

import com.dvipersquad.editableprofile.data.City;

import java.util.List;

public interface CitiesDataSource {

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
