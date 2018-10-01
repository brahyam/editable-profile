package com.dvipersquad.editableprofile.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.Profile;

/**
 * The Room Database that contains the profiles, attributes and cities tables.
 */
@Database(entities = {Profile.class, Attribute.class, City.class}, version = 2, exportSchema = false)
@TypeConverters({Profile.Converters.class})
public abstract class ProfilesDatabase extends RoomDatabase {
    public abstract ProfilesDao profilesDao();
}
