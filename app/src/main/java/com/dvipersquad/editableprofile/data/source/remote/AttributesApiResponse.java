package com.dvipersquad.editableprofile.data.source.remote;

import com.dvipersquad.editableprofile.data.Attribute;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttributesApiResponse {

    private List<Attribute> gender;

    private List<Attribute> ethnicity;

    private List<Attribute> religion;

    private List<Attribute> figure;

    @SerializedName("marital_status")
    private List<Attribute> maritalStatus;

    public AttributesApiResponse(List<Attribute> gender, List<Attribute> ethnicity, List<Attribute> religion, List<Attribute> figure, List<Attribute> maritalStatus) {
        this.gender = gender;
        this.ethnicity = ethnicity;
        this.religion = religion;
        this.figure = figure;
        this.maritalStatus = maritalStatus;
    }

    public List<Attribute> getGender() {
        return gender;
    }

    public void setGender(List<Attribute> gender) {
        this.gender = gender;
    }

    public List<Attribute> getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(List<Attribute> ethnicity) {
        this.ethnicity = ethnicity;
    }

    public List<Attribute> getReligion() {
        return religion;
    }

    public void setReligion(List<Attribute> religion) {
        this.religion = religion;
    }

    public List<Attribute> getFigure() {
        return figure;
    }

    public void setFigure(List<Attribute> figure) {
        this.figure = figure;
    }

    public List<Attribute> getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(List<Attribute> maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
}
