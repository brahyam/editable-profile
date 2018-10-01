package com.dvipersquad.editableprofile.profile;

import com.dvipersquad.editableprofile.BasePresenter;
import com.dvipersquad.editableprofile.BaseView;
import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.Profile;

import java.util.List;

/**
 * Specifies the contract between the view and the presenter
 */
public interface ProfileContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showProfile(Profile profile);

        void showProfileImage(String imageUrl);

        void showCity(String cityName);

        void showAttributes(List<Attribute> attributes);

        void showMissingProfile();

        void showErrorMessage(String message);

        void showEditProfileUI(String profileId);

        void showSelectPictureDialog();

        boolean isActive();
    }

    interface Presenter extends BasePresenter<View> {

        void takeView(ProfileContract.View profileFragment);

        void openSelectImageUI();

        void imageSelected(String path);

        void openEditProfile();

        void dropView();
    }

}
