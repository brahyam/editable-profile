package com.dvipersquad.editableprofile.profile;

import android.support.annotation.NonNull;

import com.dvipersquad.editableprofile.BasePresenter;
import com.dvipersquad.editableprofile.BaseView;
import com.dvipersquad.editableprofile.data.User;

/**
 * Specifies the contract between the view and the presenter
 */
public interface ProfileContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showProfile(User user);

        void showLocation(String cityName);

        void showMissingUser();

        void showEditProfileUI(String userId);

        boolean isActive();
    }

    interface Presenter extends BasePresenter<View> {

        void takeView(ProfileContract.View profileFragment);

        void openEditProfile(@NonNull User user);

        void dropView();
    }

}
