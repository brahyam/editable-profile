package com.dvipersquad.editableprofile.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dvipersquad.editableprofile.data.Profile;

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

    interface SaveProfileImageCallback {

        void onProfileImageSaved(String imageUrl);

        void onOperationFailed(String message);
    }

    void saveProfileImage(@NonNull String imageUrl, @Nullable SaveProfileImageCallback callback);
}
