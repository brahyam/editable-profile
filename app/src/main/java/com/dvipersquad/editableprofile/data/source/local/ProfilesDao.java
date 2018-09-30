package com.dvipersquad.editableprofile.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.Profile;

import java.util.List;

/**
 * Data Access Object for the profiles table.
 */
@Dao
public interface ProfilesDao {

    @Query("SELECT * FROM Profiles WHERE id = :profileId")
    Profile getProfileById(String profileId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProfile(Profile profile);

    // Returns the number of elements updated. Should be always > 0
    @Update
    int updateProfile(Profile profile);

    @Query("DELETE FROM Profiles WHERE id = :profileId")
    void deleteProfileById(String profileId);

    @Query("DELETE FROM Profiles")
    void deleteProfiles();

    @Query("SELECT * FROM Attributes")
    List<Attribute> getAttributes();

    @Query("SELECT * FROM Attributes WHERE type = :type")
    List<Attribute> getAttributesByType(String type);

    @Query("SELECT * FROM Attributes WHERE id = :attributeId")
    Attribute getAttributeById(String attributeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAttribute(Attribute attribute);

    // Returns the number of elements updated. Should be always > 0
    @Update
    int updateAttribute(Attribute attribute);

    @Query("DELETE FROM Attributes")
    void deleteAttributes();

    @Query("DELETE FROM Attributes WHERE id = :attributeId")
    void deleteAttributeById(String attributeId);

    @Query("SELECT * FROM Cities")
    List<City> getCities();

    @Query("SELECT * FROM Cities WHERE name = :cityName")
    City getCityByName(String cityName);

    @Query("SELECT * FROM Cities WHERE latitude = :lat AND longitude = :lon")
    City getCityByLatLon(String lat, String lon);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCity(City city);

    // Returns the number of elements updated. Should be always > 0
    @Update
    int updateCity(City city);

    @Query("DELETE FROM Cities")
    void deleteCities();

    @Query("DELETE FROM Cities WHERE latitude = :lat AND longitude = :lon")
    void deleteCityByLatLon(String lat, String lon);


}
