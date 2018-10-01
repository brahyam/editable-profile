package com.dvipersquad.editableprofile.data.source.local;

import android.support.annotation.NonNull;

import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.source.CitiesDataSource;
import com.dvipersquad.editableprofile.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CitiesLocalDataSource implements CitiesDataSource {

    private final ProfilesDao dao;
    private final AppExecutors appExecutors;

    @Inject
    public CitiesLocalDataSource(@NonNull AppExecutors executors, @NonNull ProfilesDao dao) {
        this.dao = dao;
        this.appExecutors = executors;
    }

    @Override
    public void getCities(@NonNull final LoadCitiesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<City> cities = dao.getCities();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (cities.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable("Not found");
                        } else {
                            callback.onCitiesLoaded(cities);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getCity(@NonNull final String latitude, @NonNull final String longitude, @NonNull final GetCityCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final City city = dao.getCityByLatLon(latitude, longitude);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (city != null) {
                            callback.onCityLoaded(city);
                        } else {
                            callback.onDataNotAvailable("Not found");
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveCity(@NonNull final City city) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.insertCity(city);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteCity(@NonNull final String latitude, @NonNull final String longitude) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteCityByLatLon(latitude, longitude);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllCities() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteCities();
            }
        };
        appExecutors.diskIO().execute(runnable);
    }
}
