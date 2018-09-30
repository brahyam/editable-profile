package com.dvipersquad.editableprofile.data;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "cities", primaryKeys = {"latitude", "longitude"})
public class City {

    @SerializedName("city")
    private String name;

    @NonNull
    @SerializedName("lat")
    private String latitude;

    @NonNull
    @SerializedName("lon")
    private String longitude;

    public City(String name, @NonNull String latitude, @NonNull String longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(@NonNull String latitude) {
        this.latitude = latitude;
    }

    @NonNull
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(@NonNull String longitude) {
        this.longitude = longitude;
    }
}
