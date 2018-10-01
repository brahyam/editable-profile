package com.dvipersquad.editableprofile.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "attributes")
public class Attribute {
    // Single Choice
    public static final String TYPE_GENDER = "Gender";
    public static final String TYPE_ETHNICITY = "Ethnicity";
    public static final String TYPE_RELIGION = "Religion";
    public static final String TYPE_FIGURE = "Figure";
    public static final String TYPE_MARITAL_STATUS = "MaritalStatus";
    // Not Single Choice
    public static final String TYPE_REAL_NAME = "RealName";
    public static final String TYPE_DISPLAY_NAME = "DisplayName";
    public static final String TYPE_BIRTHDAY = "Birthday";
    public static final String TYPE_LOCATION = "Location";
    public static final String TYPE_OCCUPATION = "Occupation";
    public static final String TYPE_ABOUT_ME = "AboutMe";

    @PrimaryKey
    @NonNull
    private String id;

    private String name;

    private String type;

    public Attribute(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
