package com.dvipersquad.editableprofile.data.source.remote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.data.source.ProfilesDataSource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class ProfilesRemoteDataSource implements ProfilesDataSource {

    private final static String API_URL = "http://10.0.2.2:3030/"; //PC Localhost from Emulator
    private static final String TAG = ProfilesRemoteDataSource.class.getSimpleName();
    private ProfilesApi profilesApi;

    public ProfilesRemoteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
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
    public void updateProfile(@NonNull Profile profile, @Nullable ModifyProfileCallback callback) {
        Call<Profile> call = profilesApi.updateProfile(profile);
        executeProfileModificationCall(call, callback);
    }

    @Override
    public void deleteProfile(@NonNull String profileId, @Nullable ModifyProfileCallback callback) {
        Call<Profile> call = profilesApi.deleteProfile(profileId);
        executeProfileModificationCall(call, callback);
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

    @Override
    public void getAttributes(@NonNull final GetAttributesCallback callback) {
        Call<AttributesApiResponse> call = profilesApi.getAttributes();
        call.enqueue(new Callback<AttributesApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<AttributesApiResponse> call, @NonNull Response<AttributesApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Assign correct type to each attribute and store then together
                    List<Attribute> attributes = new ArrayList<>();
                    if (response.body().getGender() != null) {
                        for (Attribute attribute : response.body().getGender()) {
                            attribute.setType(Attribute.TYPE_GENDER);
                        }
                        attributes.addAll(response.body().getGender());
                    }
                    if (response.body().getEthnicity() != null) {
                        for (Attribute attribute : response.body().getEthnicity()) {
                            attribute.setType(Attribute.TYPE_ETHNICITY);
                        }
                        attributes.addAll(response.body().getEthnicity());
                    }
                    if (response.body().getReligion() != null) {
                        for (Attribute attribute : response.body().getReligion()) {
                            attribute.setType(Attribute.TYPE_RELIGION);
                        }
                        attributes.addAll(response.body().getReligion());
                    }
                    if (response.body().getFigure() != null) {
                        for (Attribute attribute : response.body().getFigure()) {
                            attribute.setType(Attribute.TYPE_FIGURE);
                        }
                        attributes.addAll(response.body().getFigure());
                    }
                    if (response.body().getMaritalStatus() != null) {
                        for (Attribute attribute : response.body().getMaritalStatus()) {
                            attribute.setType(Attribute.TYPE_MARITAL_STATUS);
                        }
                        attributes.addAll(response.body().getMaritalStatus());
                    }
                    callback.onAttributesLoaded(attributes);
                } else {
                    callback.onDataNotAvailable(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AttributesApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getAttributes call failed:" + t.getMessage());
                callback.onDataNotAvailable(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void getAttributesByType(@NonNull String type, GetAttributesCallback callback) {
        // Not implemented by remote service
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAttribute(@NonNull String attributeId, @NonNull GetAttributeCallback callback) {
        // Not implemented by remote service
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveAttribute(@NonNull Attribute attribute) {
        // Not implemented by remote service
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAttribute(@NonNull String attributeId) {
        // Not implemented by remote service
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllAttributes() {
        // Not implemented by remote service
        throw new UnsupportedOperationException();
    }

    @Override
    public void getCities(@NonNull final GetCitiesCallback callback) {
        Call<CitiesApiResponse> call = profilesApi.getCities();
        call.enqueue(new Callback<CitiesApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<CitiesApiResponse> call, @NonNull Response<CitiesApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCities() != null) {
                    callback.onCitiesLoaded(response.body().getCities());
                } else {
                    callback.onDataNotAvailable(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CitiesApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getCities call failed:" + t.getMessage());
                callback.onDataNotAvailable(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void getCity(@NonNull String latitude, @NonNull String longitude, @NonNull GetCityCallback callback) {
        // Not implemented by remote service
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveCity(@NonNull City city) {
        // Not implemented by remote service
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteCity(@NonNull String latitude, @NonNull String longitude) {
        // Not implemented by remote service
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllCities() {
        // Not implemented by remote service
        throw new UnsupportedOperationException();
    }

    private interface ProfilesApi {
        @GET("profiles/{id}")
        Call<Profile> getProfile(@Path("id") String profileId);

        @POST("profiles")
        Call<Profile> saveProfile(@Body Profile profile);

        @PATCH("profiles")
        Call<Profile> updateProfile(@Body Profile profile);

        @DELETE("profiles/{id}")
        Call<Profile> deleteProfile(@Path("id") String profileId);

        @GET("cities.json")
        Call<CitiesApiResponse> getCities();

        @GET("single_choice_attributes.json")
        Call<AttributesApiResponse> getAttributes();
    }
}
