package com.dvipersquad.editableprofile.editprofile;

import com.dvipersquad.editableprofile.BasePresenter;
import com.dvipersquad.editableprofile.BaseView;
import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.Profile;

import java.util.List;

public class EditProfileContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showProfile(Profile profile);

        void showAttributes(List<Attribute> attributes);

        void showCity(String cityName);

        void showMissingProfile();

        void showErrorMessage(String message);

        boolean isActive();
    }

    interface Presenter extends BasePresenter<View> {

        void takeView(EditProfileContract.View profileFragment);

        void dropView();

    }

}
