package com.dvipersquad.editableprofile.data.source;

import com.dvipersquad.editableprofile.data.LocationCoordinate;
import com.dvipersquad.editableprofile.data.Profile;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class ProfilesRepositoryTests {

    private static final Profile PROFILE = new Profile(
            "c5pQGk6vISfNAPd2", // Hardcoded Id
            "Test Disp Name",
            "Test Real Name",
            "TestPictureUrl.jpg",
            new Date(),
            "testGenderId",
            "testEthnicityId",
            "testReligionId",
            111,
            "testFigureId",
            "testMaritalStatusId",
            "Test Occupation",
            "Test About me text",
            new LocationCoordinate("testLat", "testLong"));

    private ProfilesRepository profilesRepository;

    @Mock
    private ProfilesDataSource profilesRemoteDataSource;

    @Mock
    private ProfilesDataSource profilesLocalDataSource;

    @Mock
    private ProfilesDataSource.GetProfileCallback getProfileCallback;

    @Mock
    private ProfilesDataSource.ModifyProfileCallback modifyProfileCallback;

    @Captor
    private ArgumentCaptor<ProfilesDataSource.GetProfileCallback> getProfileCallbackCaptor;

    @Captor
    private ArgumentCaptor<ProfilesDataSource.ModifyProfileCallback> modifyProfileCallbackCaptor;

    @Before
    public void setupProfilesRepository() {
        MockitoAnnotations.initMocks(this);
        profilesRepository =
                new ProfilesRepository(profilesRemoteDataSource, profilesLocalDataSource);
    }

    @Test
    public void saveProfile_savesProfileToRemote() {
        // When a profile is saved
        profilesRepository.saveProfile(PROFILE, null);
        // Then profile is saved locally and cache is updated
        verify(profilesRemoteDataSource).saveProfile(eq(PROFILE), any(ProfilesDataSource.ModifyProfileCallback.class));
    }

    @Test
    public void saveProfile_savesProfileToLocalDBAndUpdatesCache() {
        // When a profile is saved
        profilesRepository.saveProfile(PROFILE, null);

        setProfileSaved(profilesRemoteDataSource, PROFILE);

        // Then profile is saved locally and cache is updated
        verify(profilesLocalDataSource).saveProfile(PROFILE, null);
        assertThat(profilesRepository.cachedProfiles.size(), is(1));
    }

    @Test
    public void getProfile_requestsSingleProfileFromLocalDataSource() {
        // When a profile is requested from the profiles repository
        profilesRepository.getProfile(PROFILE.getId(), getProfileCallback);

        verify(profilesLocalDataSource).getProfile(eq(PROFILE.getId()),
                any(ProfilesDataSource.GetProfileCallback.class));
    }

    @Test
    public void getProfileWithCacheInvalidated_requestsSingleProfileFromRemoteDataSource() {
        profilesRepository.refreshProfiles();
        // When a profile is requested from the profiles repository
        profilesRepository.getProfile(PROFILE.getId(), getProfileCallback);

        verify(profilesRemoteDataSource).getProfile(eq(PROFILE.getId()),
                any(ProfilesDataSource.GetProfileCallback.class));
    }

    @Test
    public void deleteProfile_deleteProfileFromLocalDBAndRemovedFromCache() {
        // Given a profile in the repository
        profilesRepository.saveProfile(PROFILE, modifyProfileCallback);

        setProfileSaved(profilesRemoteDataSource, PROFILE);

        assertThat(profilesRepository.cachedProfiles.containsKey(PROFILE.getId()), is(true));

        // When deleted
        profilesRepository.deleteProfile(PROFILE.getId(), null);

        // Verify the local data source is called
        verify(profilesLocalDataSource).deleteProfile(PROFILE.getId(), null);

        // Verify it's removed from cache
        assertThat(profilesRepository.cachedProfiles.containsKey(PROFILE.getId()), is(false));
    }

    @Test
    public void getProfileWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        String WRONG_PROFILE_ID = "123";

        // Request wrong profile
        profilesRepository.getProfile(WRONG_PROFILE_ID, getProfileCallback);

        // Data is not available locally
        setProfileNotAvailable(profilesLocalDataSource, WRONG_PROFILE_ID);

        setProfileNotAvailable(profilesRemoteDataSource, WRONG_PROFILE_ID);

        // Verify no data is returned
        verify(getProfileCallback).onDataNotAvailable(any(String.class));
    }

    private void setProfileNotAvailable(ProfilesDataSource dataSource, String profileId) {
        verify(dataSource).getProfile(eq(profileId), getProfileCallbackCaptor.capture());
        getProfileCallbackCaptor.getValue().onDataNotAvailable("");
    }

    private void setProfileSaved(ProfilesDataSource dataSource, Profile profile) {
        verify(dataSource).saveProfile(eq(profile), modifyProfileCallbackCaptor.capture());
        modifyProfileCallbackCaptor.getValue().onProfileModified(profile);
    }

}
