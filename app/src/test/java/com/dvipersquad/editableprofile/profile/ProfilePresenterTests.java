package com.dvipersquad.editableprofile.profile;

import com.dvipersquad.editableprofile.data.LocationCoordinate;
import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.data.source.AttributesRepository;
import com.dvipersquad.editableprofile.data.source.CitiesRepository;
import com.dvipersquad.editableprofile.data.source.ProfilesDataSource;
import com.dvipersquad.editableprofile.data.source.ProfilesRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfilePresenterTests {

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

    @Mock
    private ProfilesRepository profilesRepository;

    @Mock
    private CitiesRepository citiesRepository;

    @Mock
    private AttributesRepository attributesRepository;


    @Mock
    private ProfileContract.View profileView;

    @Captor
    private ArgumentCaptor<ProfilesDataSource.GetProfileCallback> getProfileCallbackCaptor;

    private ProfilePresenter profilePresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // Presenter needs active view
        when(profileView.isActive()).thenReturn(true);
    }

    @Test
    public void getProfileFromRepositoryAndLoadIntoView() {

        profilePresenter = new ProfilePresenter(
                PROFILE.getId(), profilesRepository, citiesRepository, attributesRepository);
        profilePresenter.takeView(profileView);

        // Verify loading indicator is activated
        verify(profilesRepository).getProfile(eq(PROFILE.getId()), getProfileCallbackCaptor.capture());
        InOrder inOrder = inOrder(profileView);
        inOrder.verify(profileView).setLoadingIndicator(true);

        getProfileCallbackCaptor.getValue().onProfileLoaded(PROFILE);

        // Verify loading indicator is disabled
        inOrder.verify(profileView).setLoadingIndicator(false);
        // Profile is shown
        verify(profileView).showProfile(PROFILE);
    }

    @Test
    public void getInvalidProfileFromRepositoryAndLoadIntoView() {
        // When loading of a profile is requested with an invalid profile ID.
        profilePresenter = new ProfilePresenter(
                null, profilesRepository, citiesRepository, attributesRepository);
        profilePresenter.takeView(profileView);
        verify(profileView).showMissingProfile();
    }

    @Test
    public void clickOnEditProfilePicture_ShowsImageSelectionDialog() {
        profilePresenter = new ProfilePresenter(
                PROFILE.getId(), profilesRepository, citiesRepository, attributesRepository);
        profilePresenter.takeView(profileView);

        // When open profile photo is requested
        profilePresenter.showImageSelectionDialog();
        // Then Full Screen Photo UI is shown
        verify(profileView).showImageSelectionUI();
    }

    @Test
    public void clickOnEditProfileMenu_ShowsEditProfileView() {
        profilePresenter = new ProfilePresenter(
                PROFILE.getId(), profilesRepository, citiesRepository, attributesRepository);
        profilePresenter.takeView(profileView);
        // When booking button is pressed
        profilePresenter.showEditProfileView();
        // Then website is opened
        verify(profileView).showEditProfileUI(eq(PROFILE.getId()));
    }
}
