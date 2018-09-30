package com.dvipersquad.editableprofile.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.LocationCoordinate;
import com.dvipersquad.editableprofile.data.Profile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Retrieves data from different sources into a cache
 */
@Singleton
public class ProfilesRepository implements ProfilesDataSource {

    private final ProfilesDataSource profilesRemoteDataSource;

    private final ProfilesDataSource profilesLocalDataSource;

    Map<String, Profile> cachedProfiles;
    Map<String, Attribute> cachedAttributes;
    Map<LocationCoordinate, City> cachedCities;

    boolean profileCacheIsDirty = false;
    boolean attributeCacheIsDirty = false;
    boolean cityCacheIsDirty = false;

    @Inject
    ProfilesRepository(@Remote ProfilesDataSource profilesRemoteDataSource, @Local ProfilesDataSource profilesLocalDataSource) {
        this.profilesRemoteDataSource = profilesRemoteDataSource;
        this.profilesLocalDataSource = profilesLocalDataSource;
    }

    /**
     * Gets profile from cache, or local/remote data source if not available
     *
     * @param profileId hotel Id to search for
     * @param callback  callback to execute if process is completes/fails
     */
    @Override
    public void getProfile(@NonNull final String profileId, @NonNull final GetProfileCallback callback) {
        // Try cache first
        if (cachedProfiles != null && !cachedProfiles.isEmpty()) {
            final Profile cachedProfile = cachedProfiles.get(profileId);
            if (cachedProfile != null) {
                callback.onProfileLoaded(cachedProfile);
                return;
            }
        }

        // Try local source
        profilesLocalDataSource.getProfile(profileId, new GetProfileCallback() {
            @Override
            public void onProfileLoaded(Profile profile) {
                addProfileToCache(profile);
                callback.onProfileLoaded(profile);
            }

            @Override
            public void onDataNotAvailable(String message) {
                profilesRemoteDataSource.getProfile(profileId, new GetProfileCallback() {
                    @Override
                    public void onProfileLoaded(Profile profile) {
                        addProfileToCache(profile);
                        profilesLocalDataSource.saveProfile(profile, null);
                        callback.onProfileLoaded(profile);
                    }

                    @Override
                    public void onDataNotAvailable(String message) {
                        callback.onDataNotAvailable(message);
                    }
                });

            }
        });
    }

    private void addProfileToCache(Profile profile) {
        if (cachedProfiles == null) {
            cachedProfiles = new LinkedHashMap<>();
        }
        cachedProfiles.put(profile.getId(), profile);
    }

    @Override
    public void saveProfile(@NonNull Profile profile, final ModifyProfileCallback callback) {
        profilesRemoteDataSource.saveProfile(profile, new ModifyProfileCallback() {
            @Override
            public void onProfileModified(Profile profile) {
                if (profile != null) {
                    addProfileToCache(profile);
                    profilesLocalDataSource.saveProfile(profile, null);
                    if (callback != null) {
                        callback.onProfileModified(profile);
                    }
                }
            }

            @Override
            public void onOperationFailed(String message) {
                //TODO: Implement mechanism that stores profiles and tries to save then later
                // If remote operation failed do nothing
                if (callback != null) {
                    callback.onOperationFailed(message);
                }
            }
        });
    }

    @Override
    public void updateProfile(@NonNull Profile profile, @Nullable final ModifyProfileCallback callback) {
        profilesRemoteDataSource.updateProfile(profile, new ModifyProfileCallback() {
            @Override
            public void onProfileModified(Profile profile) {
                if (profile != null) {
                    addProfileToCache(profile);
                    profilesLocalDataSource.updateProfile(profile, null);
                    if (callback != null) {
                        callback.onProfileModified(profile);
                    }

                }
            }

            @Override
            public void onOperationFailed(String message) {
                //TODO: Implement mechanism that stores profiles and tries to save then later
                // If remote operation failed do nothing
                if (callback != null) {
                    callback.onOperationFailed(message);
                }
            }
        });
    }

    public void forceRemoteLoading() {
        profileCacheIsDirty = true;
    }

    @Override
    public void deleteProfile(@NonNull String profileId, final ModifyProfileCallback callback) {
        profilesLocalDataSource.deleteProfile(profileId, null);
        cachedProfiles.remove(profileId);
        profilesRemoteDataSource.deleteProfile(profileId, new ModifyProfileCallback() {
            @Override
            public void onProfileModified(Profile profile) {
                if (profile != null) {
                    callback.onProfileModified(profile);
                } else {
                    callback.onOperationFailed("Not found");
                }
            }

            @Override
            public void onOperationFailed(String message) {
                callback.onOperationFailed(message);
            }
        });
    }

    @Override
    public void getAttributes(@NonNull final GetAttributesCallback callback) {
        if (cachedAttributes != null && !attributeCacheIsDirty) {
            callback.onAttributesLoaded(new ArrayList<>(cachedAttributes.values()));
            return;
        }

        if (attributeCacheIsDirty) {
            getAttributesFromRemoteDataSource(callback);
        } else {
            profilesLocalDataSource.getAttributes(new GetAttributesCallback() {
                @Override
                public void onAttributesLoaded(List<Attribute> attributes) {
                    refreshAttributesCache(attributes);
                    callback.onAttributesLoaded(new ArrayList<>(cachedAttributes.values()));
                }

                @Override
                public void onDataNotAvailable(String message) {
                    getAttributesFromRemoteDataSource(callback);
                }
            });
        }
    }

    private void refreshAttributesCache(List<Attribute> attributes) {
        if (cachedAttributes == null) {
            cachedAttributes = new LinkedHashMap<>();
        }
        cachedAttributes.clear();
        for (Attribute attribute : attributes) {
            cachedAttributes.put(attribute.getId(), attribute);
        }
        attributeCacheIsDirty = false;
    }

    private void getAttributesFromRemoteDataSource(final GetAttributesCallback callback) {
        profilesRemoteDataSource.getAttributes(new GetAttributesCallback() {
            @Override
            public void onAttributesLoaded(List<Attribute> attributes) {
                refreshAttributesCache(attributes);
                refreshAttributesLocalDataSource(attributes);
                callback.onAttributesLoaded(new ArrayList<>(cachedAttributes.values()));
            }

            @Override
            public void onDataNotAvailable(String message) {
                callback.onDataNotAvailable(message);
            }
        });
    }

    private void refreshAttributesLocalDataSource(List<Attribute> attributes) {
        profilesLocalDataSource.deleteAllAttributes();
        for (Attribute attribute : attributes) {
            profilesLocalDataSource.saveAttribute(attribute);
        }
    }

    @Override
    public void getAttributesByType(@NonNull String type, final GetAttributesCallback callback) {
        if (cachedAttributes != null && !attributeCacheIsDirty) {
            List<Attribute> filteredAttributes = new ArrayList<>();
            for (Attribute attribute : cachedAttributes.values()) {
                if (attribute.getType().equals(type)) {
                    filteredAttributes.add(attribute);
                }
            }
            callback.onAttributesLoaded(new ArrayList<>(filteredAttributes));
            return;
        }

        // Do not refresh cached because new ones are filtered
        profilesLocalDataSource.getAttributesByType(type, callback);
    }

    @Override
    public void getAttribute(@NonNull String attributeId, @NonNull final GetAttributeCallback callback) {
        // Try cache first
        if (cachedAttributes != null && !cachedAttributes.isEmpty()) {
            final Attribute cachedAttribute = cachedAttributes.get(attributeId);
            if (cachedAttribute != null) {
                callback.onAttributeLoaded(cachedAttribute);
                return;
            }
        }

        // Try local source
        profilesLocalDataSource.getAttribute(attributeId, new GetAttributeCallback() {
            @Override
            public void onAttributeLoaded(Attribute attribute) {
                if (cachedAttributes == null) {
                    cachedAttributes = new LinkedHashMap<>();
                }
                cachedAttributes.put(attribute.getId(), attribute);
                callback.onAttributeLoaded(attribute);
            }

            @Override
            public void onDataNotAvailable(String message) {
                // Get individual attribute not implemented by remote source
                // so dont try to fetch from server
                callback.onDataNotAvailable(message);
            }
        });
    }

    @Override
    public void saveAttribute(@NonNull Attribute attribute) {
        profilesLocalDataSource.saveAttribute(attribute);
        if (cachedAttributes == null) {
            cachedAttributes = new LinkedHashMap<>();
        }
        cachedAttributes.put(attribute.getId(), attribute);
    }

    @Override
    public void deleteAttribute(@NonNull String attributeId) {
        profilesLocalDataSource.deleteAttribute(attributeId);
        cachedAttributes.remove(attributeId);
    }

    @Override
    public void deleteAllAttributes() {
        profilesLocalDataSource.deleteAllAttributes();
        if (cachedAttributes == null) {
            cachedAttributes = new LinkedHashMap<>();
        }
        cachedAttributes.clear();
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
            profilesLocalDataSource.getCities(new GetCitiesCallback() {
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
        attributeCacheIsDirty = false;
    }

    private void getCitiesFromRemoteDataSource(final GetCitiesCallback callback) {
        profilesRemoteDataSource.getCities(new GetCitiesCallback() {
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
        profilesLocalDataSource.deleteAllCities();
        for (City city : cities) {
            profilesLocalDataSource.saveCity(city);
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
        profilesLocalDataSource.getCity(latitude, longitude, new GetCityCallback() {
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
        profilesLocalDataSource.saveCity(city);
        if (cachedCities == null) {
            cachedCities = new LinkedHashMap<>();
        }
        cachedCities.put(new LocationCoordinate(city.getLatitude(), city.getLongitude()), city);
    }

    @Override
    public void deleteCity(@NonNull String latitude, @NonNull String longitude) {
        profilesLocalDataSource.deleteCity(latitude, longitude);
        cachedCities.remove(new LocationCoordinate(latitude, longitude));
    }

    @Override
    public void deleteAllCities() {
        profilesLocalDataSource.deleteAllCities();
        if (cachedCities == null) {
            cachedCities = new LinkedHashMap<>();
        }
        cachedCities.clear();
    }
}
