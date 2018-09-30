package com.dvipersquad.editableprofile.di;

import android.app.Application;

import com.dvipersquad.editableprofile.EditableProfileApp;
import com.dvipersquad.editableprofile.data.source.ProfilesRepository;
import com.dvipersquad.editableprofile.data.source.ProfilesRepositoryModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        ProfilesRepositoryModule.class,
        AppModule.class,
        ActivityBindingModule.class,
        AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<EditableProfileApp> {

    ProfilesRepository getProfilesRepository();

    // Enable us to doDaggerAppComponent.builder().application(this).build().inject(this);
    // Includes application in graph
    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
