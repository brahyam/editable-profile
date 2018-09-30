package com.dvipersquad.editableprofile.profile;

import android.support.annotation.NonNull;

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

        void showCity(String cityName);

        void showAttributes(List<Attribute> attributes);

        void showMissingProfile();

        void showErrorMessage(String message);

        void showEditProfileUI(String profileId);

        boolean isActive();
    }

    interface Presenter extends BasePresenter<View> {

        void takeView(ProfileContract.View profileFragment);

        void openEditProfile(@NonNull Profile profile);

        void dropView();
    }

}
