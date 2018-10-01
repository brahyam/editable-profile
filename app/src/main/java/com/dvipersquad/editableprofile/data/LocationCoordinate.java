package com.dvipersquad.editableprofile.data;

import com.google.gson.annotations.SerializedName;

public class LocationCoordinate {

    @SerializedName("lat")
    private String latitudeDMS;

    @SerializedName("lon")
    private String longitudeDMS;

    public LocationCoordinate(String latitudeDMS, String longitudeDMS) {
        this.latitudeDMS = latitudeDMS;
        this.longitudeDMS = longitudeDMS;
    }

    public String getLatitudeDMS() {
        return latitudeDMS;
    }

    public void setLatitudeDMS(String latitudeDMS) {
        this.latitudeDMS = latitudeDMS;
    }

    public String getLongitudeDMS() {
        return longitudeDMS;
    }

    public void setLongitudeDMS(String longitudeDMS) {
        this.longitudeDMS = longitudeDMS;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LocationCoordinate) {
            return this.getLatitudeDMS().equals(((LocationCoordinate) obj).getLatitudeDMS()) && this.getLongitudeDMS().equals(((LocationCoordinate) obj).getLongitudeDMS());
        } else {
            return super.equals(obj);
        }
    }
}
