package com.dvipersquad.editableprofile.editprofile;

import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.LocationCoordinate;
import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.data.source.AttributesDataSource;
import com.dvipersquad.editableprofile.data.source.AttributesRepository;
import com.dvipersquad.editableprofile.data.source.CitiesDataSource;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditProfilePresenterTests {

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

    private static final Attribute SINGLE_CHOICE_ATTRIBUTE = new Attribute(
            "1",
            "Test Single Choice Attribute",
            Attribute.TYPE_GENDER);

    private static final Attribute DATE_ATTRIBUTE = new Attribute(
            "1",
            "Test Date Attribute",
            Attribute.TYPE_BIRTHDAY);

    private static final Attribute FREE_TEXT_ATTRIBUTE = new Attribute(
            "1",
            "Test Free Text Attribute",
            Attribute.TYPE_DISPLAY_NAME);

    @Mock
    private ProfilesRepository profilesRepository;

    @Mock
    private CitiesRepository citiesRepository;

    @Mock
    private AttributesRepository attributesRepository;


    @Mock
    private EditProfileContract.View editProfileView;

    @Captor
    private ArgumentCaptor<ProfilesDataSource.GetProfileCallback> getProfileCallbackCaptor;

    @Captor
    private ArgumentCaptor<AttributesDataSource.LoadAttributesCallback> getAttributesCallbackCaptor;

    @Captor
    private ArgumentCaptor<CitiesDataSource.LoadCitiesCallback> getCitiesCallbackCaptor;

    private EditProfilePresenter editProfilePresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // Presenter needs active view
        when(editProfileView.isActive()).thenReturn(true);
    }

    @Test
    public void getProfileFromRepositoryAndLoadIntoView() {

        editProfilePresenter = new EditProfilePresenter(
                PROFILE.getId(), profilesRepository, citiesRepository, attributesRepository);
        editProfilePresenter.takeView(editProfileView);

        // Verify loading indicator is activated
        verify(profilesRepository).getProfile(eq(PROFILE.getId()), getProfileCallbackCaptor.capture());
        InOrder inOrder = inOrder(editProfileView);
        inOrder.verify(editProfileView).setLoadingIndicator(true);

        getProfileCallbackCaptor.getValue().onProfileLoaded(PROFILE);

        // Verify loading indicator is disabled
        inOrder.verify(editProfileView).setLoadingIndicator(false);
        // Profile is shown
        verify(editProfileView).showProfile(PROFILE);
    }

    @Test
    public void getInvalidProfileFromRepositoryAndLoadIntoView() {
        // When loading of a profile is requested with an invalid profile ID.
        editProfilePresenter = new EditProfilePresenter(
                null, profilesRepository, citiesRepository, attributesRepository);
        editProfilePresenter.takeView(editProfileView);
        verify(editProfileView).showMissingProfile();
    }

    @Test
    public void clickOnEditFreeTextAttribute_ShowEditFreeTextUI() {
        editProfilePresenter = new EditProfilePresenter(
                PROFILE.getId(), profilesRepository, citiesRepository, attributesRepository);
        editProfilePresenter.takeView(editProfileView);

        //Load profile
        verify(profilesRepository).getProfile(eq(PROFILE.getId()), getProfileCallbackCaptor.capture());
        getProfileCallbackCaptor.getValue().onProfileLoaded(PROFILE);

        // When open modify attribute is requested
        editProfilePresenter.showModifyAttributeView(FREE_TEXT_ATTRIBUTE.getType());

        // Then modify attribute single choice is shown
        verify(editProfileView).showEditFreeTextUI(any(Attribute.class), any(Integer.class), any(Boolean.class));
    }

    @Test
    public void clickOnEditDateAttribute_ShowEditDateUI() {
        editProfilePresenter = new EditProfilePresenter(
                PROFILE.getId(), profilesRepository, citiesRepository, attributesRepository);
        editProfilePresenter.takeView(editProfileView);

        //Load profile
        verify(profilesRepository).getProfile(eq(PROFILE.getId()), getProfileCallbackCaptor.capture());
        getProfileCallbackCaptor.getValue().onProfileLoaded(PROFILE);

        // When open modify attribute is requested
        editProfilePresenter.showModifyAttributeView(DATE_ATTRIBUTE.getType());

        // Then modify attribute single choice is shown
        verify(editProfileView).showEditDateUI(eq(PROFILE.getBirthday()));
    }
}
