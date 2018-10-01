package com.dvipersquad.editableprofile.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(tableName = "profiles")
public class Profile {

    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;

    private String displayName;

    private String realName;

    private String profilePictureUrl;

    private Date birthday;

    private String genderId;

    private String ethnicityId;

    private String religionId;

    private double height;

    private String figureId;

    private String maritalStatusId;

    private String occupation;

    private String aboutMe;

    @Embedded
    private LocationCoordinate location;

    public Profile(String id, String displayName, String realName, String profilePictureUrl,
                   Date birthday, String genderId, String ethnicityId, String religionId, double height,
                   String figureId, String maritalStatusId, String occupation, String aboutMe,
                   LocationCoordinate location) {
        this.id = id;
        this.displayName = displayName;
        this.realName = realName;
        this.profilePictureUrl = profilePictureUrl;
        this.birthday = birthday;
        this.genderId = genderId;
        this.ethnicityId = ethnicityId;
        this.religionId = religionId;
        this.height = height;
        this.figureId = figureId;
        this.maritalStatusId = maritalStatusId;
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

    public String getGenderId() {
        return genderId;
    }

    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }

    public String getEthnicityId() {
        return ethnicityId;
    }

    public void setEthnicityId(String ethnicityId) {
        this.ethnicityId = ethnicityId;
    }

    public String getReligionId() {
        return religionId;
    }

    public void setReligionId(String religionId) {
        this.religionId = religionId;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getFigureId() {
        return figureId;
    }

    public void setFigureId(String figureId) {
        this.figureId = figureId;
    }

    public String getMaritalStatusId() {
        return maritalStatusId;
    }

    public void setMaritalStatusId(String maritalStatusId) {
        this.maritalStatusId = maritalStatusId;
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

    public static class Converters {
        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    }
}
