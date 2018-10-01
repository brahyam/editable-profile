package com.dvipersquad.editableprofile.editprofile;

import com.dvipersquad.editableprofile.BasePresenter;
import com.dvipersquad.editableprofile.BaseView;
import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.Profile;

import java.util.Date;
import java.util.List;

public interface EditProfileContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showProfile(Profile profile);

        void showAttributes(List<Attribute> attributes);

        void showCity(String cityName);

        void showMissingProfile();

        void showErrorMessage(String message);

        void showEditSingleChoiceUI(List<Attribute> attributes, Attribute currentValue, boolean mandatory);

        void showEditDateUI(Date currentValue);

        void showEditFreeTextUI(Attribute attribute, int limit, boolean mandatory);

        void showEditLocationUI(List<City> cities, City currentCity);

        boolean isActive();
    }

    interface Presenter extends BasePresenter<View> {

        void takeView(EditProfileContract.View profileFragment);

        void showModifyAttributeView(String attributeType);

        void onAttributeSelected(Attribute attribute);

        void onDateSelected(Date date);

        void onLocationSelected(City city);

        void dropView();

    }

}
