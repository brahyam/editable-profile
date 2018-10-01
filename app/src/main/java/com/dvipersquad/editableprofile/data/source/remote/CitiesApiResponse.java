package com.dvipersquad.editableprofile.data.source.remote;

import com.dvipersquad.editableprofile.data.City;

import java.util.List;

public class CitiesApiResponse {

    private List<City> cities;

    public CitiesApiResponse(List<City> cities) {
        this.cities = cities;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
