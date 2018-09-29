package com.dvipersquad.editableprofile.data;

import java.util.Date;

public class User {

    private String id;

    private String displayName;

    private String realName;

    private String profilePictureUrl;

    private Date birthday;

    private String gender;

    private String ethnicity;

    private String religion;

    private double height;

    private String figure;

    private String maritalStatus;

    private String occupation;

    private String aboutMe;

    private LocationCoordinate location;

    public User(String id, String displayName, String realName, String profilePictureUrl,
                Date birthday, String gender, String ethnicity, String religion, double height,
                String figure, String maritalStatus, String occupation, String aboutMe,
                LocationCoordinate location) {
        this.id = id;
        this.displayName = displayName;
        this.realName = realName;
        this.profilePictureUrl = profilePictureUrl;
        this.birthday = birthday;
        this.gender = gender;
        this.ethnicity = ethnicity;
        this.religion = religion;
        this.height = height;
        this.figure = figure;
        this.maritalStatus = maritalStatus;
        this.occupation = occupation;
        this.aboutMe = aboutMe;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public LocationCoordinate getLocation() {
        return location;
    }

    public void setLocation(LocationCoordinate location) {
        this.location = location;
    }
}
