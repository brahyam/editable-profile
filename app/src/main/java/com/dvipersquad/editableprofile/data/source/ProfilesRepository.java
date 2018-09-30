package com.dvipersquad.editableprofile.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.Profile;

import java.util.LinkedHashMap;
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


    boolean profileCacheIsDirty = false;
    boolean attributeCacheIsDirty = false;


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
}
