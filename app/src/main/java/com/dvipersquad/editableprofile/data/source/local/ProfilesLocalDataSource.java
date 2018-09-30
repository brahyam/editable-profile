package com.dvipersquad.editableprofile.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.data.source.ProfilesDataSource;
import com.dvipersquad.editableprofile.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles access to DB
 */
@Singleton
public class ProfilesLocalDataSource implements ProfilesDataSource {

    private final ProfilesDao dao;
    private final AppExecutors appExecutors;

    @Inject
    public ProfilesLocalDataSource(@NonNull AppExecutors executors, @NonNull ProfilesDao dao) {
        this.dao = dao;
        this.appExecutors = executors;
    }

    @Override
    public void getProfile(@NonNull final String profileId, @NonNull final GetProfileCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Profile profile = dao.getProfileById(profileId);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (profile != null) {
                            callback.onProfileLoaded(profile);
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
    public void saveProfile(@NonNull final Profile profile, @Nullable final ModifyProfileCallback callback) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                dao.insertProfile(profile);
                if (callback != null) {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onProfileModified(profile);
                        }
                    });
                }
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void updateProfile(@NonNull final Profile profile, @Nullable final ModifyProfileCallback callback) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                final int updated = dao.updateProfile(profile);
                if (callback != null) {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (updated > 0) {
                                callback.onProfileModified(profile);
                            } else {
                                callback.onOperationFailed("Update failed");
                            }
                        }
                    });

                }
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void deleteProfile(@NonNull final String profileId, @Nullable final ModifyProfileCallback callback) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteProfileById(profileId);
                if (callback != null) {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onProfileModified(null);
                        }
                    });
                }
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void getAttributes(@NonNull final GetAttributesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Attribute> attributes = dao.getAttributes();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (attributes.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable("Not found");
                        } else {
                            callback.onAttributesLoaded(attributes);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getAttributesByType(@NonNull final String type, final GetAttributesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Attribute> attributes = dao.getAttributesByType(type);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (attributes.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable("Not found");
                        } else {
                            callback.onAttributesLoaded(attributes);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getAttribute(@NonNull final String attributeId, @NonNull final GetAttributeCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Attribute attribute = dao.getAttributeById(attributeId);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (attribute != null) {
                            callback.onAttributeLoaded(attribute);
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
    public void saveAttribute(@NonNull final Attribute attribute) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                dao.insertAttribute(attribute);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void deleteAttribute(@NonNull final String attributeId) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteAttributeById(attributeId);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void deleteAllAttributes() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteAttributes();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void getCities(@NonNull final GetCitiesCallback callback) {
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
