package com.dvipersquad.editableprofile.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.data.source.ProfilesDataSource;
import com.dvipersquad.editableprofile.utils.AppExecutors;

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
    public void saveProfileImage(@NonNull String imageUrl, @Nullable SaveProfileImageCallback callback) {
        // only implemented by remote service
        throw new UnsupportedOperationException();
    }


}
