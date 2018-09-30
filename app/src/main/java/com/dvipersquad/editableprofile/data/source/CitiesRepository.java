package com.dvipersquad.editableprofile.data.source;

import android.support.annotation.NonNull;

import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.LocationCoordinate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CitiesRepository implements CitiesDataSource {

    private final CitiesDataSource citiesRemoteDataSource;

    private final CitiesDataSource citiesLocalDataSource;

    boolean cityCacheIsDirty = false;
    Map<LocationCoordinate, City> cachedCities;

    @Inject
    public CitiesRepository(@Remote CitiesDataSource citiesRemoteDataSource, @Local CitiesDataSource citiesLocalDataSource) {
        this.citiesRemoteDataSource = citiesRemoteDataSource;
        this.citiesLocalDataSource = citiesLocalDataSource;
    }

    @Override
    public void getCities(@NonNull final GetCitiesCallback callback) {
        if (cachedCities != null && !cityCacheIsDirty) {
            callback.onCitiesLoaded(new ArrayList<>(cachedCities.values()));
            return;
        }

        if (cityCacheIsDirty) {
            getCitiesFromRemoteDataSource(callback);
        } else {
            citiesLocalDataSource.getCities(new GetCitiesCallback() {
                @Override
                public void onCitiesLoaded(List<City> cities) {
                    refreshCitiesCache(cities);
                    callback.onCitiesLoaded(new ArrayList<>(cachedCities.values()));
                }

                @Override
                public void onDataNotAvailable(String message) {
                    getCitiesFromRemoteDataSource(callback);
                }
            });
        }
    }

    private void refreshCitiesCache(List<City> cities) {
        if (cachedCities == null) {
            cachedCities = new LinkedHashMap<>();
        }
        cachedCities.clear();
        for (City city : cities) {
            cachedCities.put(new LocationCoordinate(city.getLatitude(), city.getLongitude()), city);
        }
        cityCacheIsDirty = false;
    }

    private void getCitiesFromRemoteDataSource(final GetCitiesCallback callback) {
        citiesRemoteDataSource.getCities(new GetCitiesCallback() {
            @Override
            public void onCitiesLoaded(List<City> cities) {
                refreshCitiesCache(cities);
                refreshCitiesLocalDataSource(cities);
                callback.onCitiesLoaded(new ArrayList<>(cachedCities.values()));
            }

            @Override
            public void onDataNotAvailable(String message) {
                callback.onDataNotAvailable(message);
            }
        });
    }

    private void refreshCitiesLocalDataSource(List<City> cities) {
        citiesLocalDataSource.deleteAllCities();
        for (City city : cities) {
            citiesLocalDataSource.saveCity(city);
        }
    }

    @Override
    public void getCity(@NonNull final String latitude, @NonNull final String longitude, @NonNull final GetCityCallback callback) {
        // Try cache first
        if (cachedCities != null && !cachedCities.isEmpty()) {
            final City cachedCity = cachedCities.get(new LocationCoordinate(latitude, longitude));
            if (cachedCity != null) {
                callback.onCityLoaded(cachedCity);
                return;
            }
        }

        // Try local source
        citiesLocalDataSource.getCity(latitude, longitude, new GetCityCallback() {
            @Override
            public void onCityLoaded(City city) {
                if (cachedCities == null) {
                    cachedCities = new LinkedHashMap<>();
                }
                cachedCities.put(new LocationCoordinate(city.getLatitude(), city.getLongitude()), city);
                callback.onCityLoaded(city);
            }

            @Override
            public void onDataNotAvailable(String message) {
                // Get individual city not implemented by remote source
                // so dont try to fetch from server
                callback.onDataNotAvailable(message);
            }
        });
    }

    @Override
    public void saveCity(@NonNull City city) {
        citiesLocalDataSource.saveCity(city);
        if (cachedCities == null) {
            cachedCities = new LinkedHashMap<>();
        }
        cachedCities.put(new LocationCoordinate(city.getLatitude(), city.getLongitude()), city);
    }

    @Override
    public void deleteCity(@NonNull String latitude, @NonNull String longitude) {
        citiesLocalDataSource.deleteCity(latitude, longitude);
        cachedCities.remove(new LocationCoordinate(latitude, longitude));
    }

    @Override
    public void deleteAllCities() {
        citiesLocalDataSource.deleteAllCities();
        if (cachedCities == null) {
            cachedCities = new LinkedHashMap<>();
        }
        cachedCities.clear();
    }
}
