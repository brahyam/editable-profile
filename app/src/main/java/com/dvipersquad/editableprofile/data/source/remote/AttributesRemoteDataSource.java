package com.dvipersquad.editableprofile.data.source.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.dvipersquad.editableprofile.BuildConfig;
import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.source.AttributesDataSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

@Singleton
public class AttributesRemoteDataSource implements AttributesDataSource {

    private static final String TAG = AttributesRemoteDataSource.class.getSimpleName();
    private AttributesApi attributesApi;

    public AttributesRemoteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        attributesApi = retrofit.create(AttributesApi.class);
    }

    @Override
    public void getAttributes(@NonNull final GetAttributesCallback callback) {
        Call<AttributesApiResponse> call = attributesApi.getAttributes();
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

    private interface AttributesApi {

        @GET("single_choice_attributes.json")
        Call<AttributesApiResponse> getAttributes();
    }
}
