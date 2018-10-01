package com.dvipersquad.editableprofile.data.source.remote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dvipersquad.editableprofile.BuildConfig;
import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.data.source.ProfilesDataSource;

import java.io.File;
import java.util.List;

import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

@Singleton
public class ProfilesRemoteDataSource implements ProfilesDataSource {

    private static final String TAG = ProfilesRemoteDataSource.class.getSimpleName();
    private ProfilesApi profilesApi;

    public ProfilesRemoteDataSource() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        profilesApi = retrofit.create(ProfilesApi.class);
    }

    @Override
    public void getProfile(@NonNull String profileId, @NonNull final GetProfileCallback callback) {
        Call<Profile> call = profilesApi.getProfile(profileId);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onProfileLoaded(response.body());
                } else {
                    callback.onDataNotAvailable(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Log.e(TAG, "getProfile call failed:" + t.getMessage());
                callback.onDataNotAvailable(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void saveProfile(@NonNull final Profile profile, @Nullable final ModifyProfileCallback callback) {
        Call<Profile> call = profilesApi.saveProfile(profile);
        executeProfileModificationCall(call, callback);
    }

    @Override
    public void updateProfile(@NonNull Profile profile, @Nullable final ModifyProfileCallback callback) {
        Call<List<Profile>> call = profilesApi.updateProfile(profile);
        call.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (callback != null) {
                        callback.onProfileModified(response.body().get(0));
                    }
                } else {
                    if (callback != null) {
                        callback.onOperationFailed(response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                Log.e(TAG, call.request().method() + " profile call failed:" + t.getMessage());
                if (callback != null) {
                    callback.onOperationFailed(t.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public void deleteProfile(@NonNull String profileId, @Nullable ModifyProfileCallback callback) {
        Call<Profile> call = profilesApi.deleteProfile(profileId);
        executeProfileModificationCall(call, callback);
    }

    @Override
    public void saveProfileImage(@NonNull String imageUrl, @Nullable final SaveProfileImageCallback callback) {
        File file = new File(imageUrl);
        final RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profileImage", file.getName(), reqFile);
//        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), new Gson().toJson(new ChangeLogoParams()));
        Call<ProfileImageApiResponse> call = profilesApi.saveProfileImage(body);
        call.enqueue(new Callback<ProfileImageApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileImageApiResponse> call, @NonNull Response<ProfileImageApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (callback != null) {
                        String imageUrl = BuildConfig.API_URL + "profileImages/" + response.body().getFileName();
                        callback.onProfileImageSaved(imageUrl);
                    }
                } else {
                    Log.e(TAG, response.message());
                    if (callback != null) {
                        callback.onOperationFailed(response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileImageApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, t.getMessage());
                if (callback != null) {
                    callback.onOperationFailed(t.getLocalizedMessage());
                }
            }
        });

    }

    private void executeProfileModificationCall(Call<Profile> call, final ModifyProfileCallback callback) {
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (callback != null) {
                        callback.onProfileModified(response.body());
                    }
                } else {
                    if (callback != null) {
                        callback.onOperationFailed(response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Log.e(TAG, call.request().method() + " profile call failed:" + t.getMessage());
                if (callback != null) {
                    callback.onOperationFailed(t.getLocalizedMessage());
                }
            }
        });
    }

    private interface ProfilesApi {
        @GET("profiles/{id}")
        Call<Profile> getProfile(@Path("id") String profileId);

        @POST("profiles")
        Call<Profile> saveProfile(@Body Profile profile);

        @PATCH("profiles")
        Call<List<Profile>> updateProfile(@Body Profile profile);

        @DELETE("profiles/{id}")
        Call<Profile> deleteProfile(@Path("id") String profileId);

        @Multipart
        @POST("upload")
        Call<ProfileImageApiResponse> saveProfileImage(@Part MultipartBody.Part image);
    }
}
