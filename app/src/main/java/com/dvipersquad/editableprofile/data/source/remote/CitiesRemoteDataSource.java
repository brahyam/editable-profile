package com.dvipersquad.editableprofile.data.source.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.dvipersquad.editableprofile.BuildConfig;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.source.CitiesDataSource;

import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

@Singleton
public class CitiesRemoteDataSource implements CitiesDataSource {

    private static final String TAG = CitiesRemoteDataSource.class.getSimpleName();
    private CitiesApi citiesApi;

    public CitiesRemoteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        citiesApi = retrofit.create(CitiesApi.class);
    }

    @Override
    public void getCities(@NonNull final GetCitiesCallback callback) {
        Call<CitiesApiResponse> call = citiesApi.getCities();
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

    private interface CitiesApi {
        @GET("cities.json")
        Call<CitiesApiResponse> getCities();
    }
}
