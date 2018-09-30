package com.dvipersquad.editableprofile.data.source;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.dvipersquad.editableprofile.data.source.local.ProfilesDao;
import com.dvipersquad.editableprofile.data.source.local.ProfilesDatabase;
import com.dvipersquad.editableprofile.data.source.local.ProfilesLocalDataSource;
import com.dvipersquad.editableprofile.data.source.remote.ProfilesRemoteDataSource;
import com.dvipersquad.editableprofile.utils.AppExecutors;
import com.dvipersquad.editableprofile.utils.DiskIOThreadExecutor;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class ProfilesRepositoryModule {

    private static final int THREAD_COUNT = 3;
    private String PROFILES_DB_NAME = "Profiles.db";

    @Singleton
    @Provides
    @Local
    ProfilesDataSource provideProfilesLocalDataSource(ProfilesDao dao, AppExecutors executors) {
        return new ProfilesLocalDataSource(executors, dao);
    }

    @Singleton
    @Provides
    @Remote
    ProfilesDataSource provideProfilesRemoteDataSource() {
        return new ProfilesRemoteDataSource();
    }

    @Singleton
    @Provides
    ProfilesDatabase provideDb(Application context) {
        return Room.databaseBuilder(context.getApplicationContext(), ProfilesDatabase.class, PROFILES_DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    ProfilesDao provideProfilesDao(ProfilesDatabase db) {
        return db.profilesDao();
    }

    @Singleton
    @Provides
    AppExecutors provideAppExecutors() {
        return new AppExecutors(new DiskIOThreadExecutor(),
                Executors.newFixedThreadPool(THREAD_COUNT),
                new AppExecutors.MainThreadExecutor());
    }
}
