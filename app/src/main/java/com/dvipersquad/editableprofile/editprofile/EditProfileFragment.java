package com.dvipersquad.editableprofile.editprofile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dvipersquad.editableprofile.R;
import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.di.ActivityScoped;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

@ActivityScoped
public class EditProfileFragment extends DaggerFragment implements EditProfileContract.View {

    @NonNull
    private static final String ARGUMENT_PROFILE_ID = "PROFILE_ID";
    private static final String ELLIPSIS = "...";

    private TextView txtEditProfileInfo;
    private Button txtEditProfileRealName;
    private Button txtEditProfileDisplayName;
    private Button txtEditProfileGender;
    private Button txtEditProfileBirthday;
    private Button txtEditProfileLocation;
    private Button txtEditProfileMaritalStatus;
    private Button txtEditProfileReligion;
    private Button txtEditProfileEthnicity;
    private Button txtEditProfileFigure;
    private Button txtEditProfileOccupation;
    private Button txtEditProfileAboutMe;

    @Inject
    @Nullable
    String profileId;

    @Inject
    EditProfileContract.Presenter presenter;

    @Inject
    public EditProfileFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.dropView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.editprofile_frag, container, false);

        txtEditProfileInfo = rootView.findViewById(R.id.txtEditProfileInfo);
        txtEditProfileRealName = rootView.findViewById(R.id.txtEditProfileRealName);
        txtEditProfileDisplayName = rootView.findViewById(R.id.txtEditProfileDisplayName);
        txtEditProfileGender = rootView.findViewById(R.id.txtEditProfileGender);
        txtEditProfileBirthday = rootView.findViewById(R.id.txtEditProfileBirthday);
        txtEditProfileLocation = rootView.findViewById(R.id.txtEditProfileLocation);
        txtEditProfileMaritalStatus = rootView.findViewById(R.id.txtEditProfileMaritalStatus);
        txtEditProfileReligion = rootView.findViewById(R.id.txtEditProfileReligion);
        txtEditProfileEthnicity = rootView.findViewById(R.id.txtEditProfileEthnicity);
        txtEditProfileFigure = rootView.findViewById(R.id.txtEditProfileFigure);
        txtEditProfileOccupation = rootView.findViewById(R.id.txtEditProfileOccupation);
        txtEditProfileAboutMe = rootView.findViewById(R.id.txtEditProfileAboutMe);

        return rootView;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        txtEditProfileRealName.setText(ELLIPSIS);
        txtEditProfileDisplayName.setText(ELLIPSIS);
        txtEditProfileGender.setText(ELLIPSIS);
        txtEditProfileBirthday.setText(ELLIPSIS);
        txtEditProfileLocation.setText(ELLIPSIS);
        txtEditProfileMaritalStatus.setText(ELLIPSIS);
        txtEditProfileReligion.setText(ELLIPSIS);
        txtEditProfileEthnicity.setText(ELLIPSIS);
        txtEditProfileFigure.setText(ELLIPSIS);
        txtEditProfileOccupation.setText(ELLIPSIS);
        txtEditProfileAboutMe.setText(ELLIPSIS);
    }

    @Override
    public void showProfile(Profile profile) {
        txtEditProfileRealName.setText(profile.getRealName());
        txtEditProfileDisplayName.setText(profile.getDisplayName());
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        txtEditProfileBirthday.setText(format.format(profile.getBirthday()));
        txtEditProfileOccupation.setText(profile.getOccupation());
        txtEditProfileAboutMe.setText(profile.getAboutMe());
    }

    @Override
    public void showAttributes(List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            switch (attribute.getType()) {
                case Attribute.TYPE_GENDER:
                    txtEditProfileGender.setText(attribute.getName());
                    break;
                case Attribute.TYPE_ETHNICITY:
                    txtEditProfileEthnicity.setText(attribute.getName());
                    break;
                case Attribute.TYPE_RELIGION:
                    txtEditProfileReligion.setText(attribute.getName());
                    break;
                case Attribute.TYPE_FIGURE:
                    txtEditProfileFigure.setText(attribute.getName());
                    break;
                case Attribute.TYPE_MARITAL_STATUS:
                    txtEditProfileMaritalStatus.setText(attribute.getName());
                    break;
            }
        }
    }

    @Override
    public void showCity(String cityName) {
        txtEditProfileLocation.setText(cityName);
    }

    @Override
    public void showMissingProfile() {
        txtEditProfileInfo.setText(getString(R.string.error_loading_profile));
    }

    @Override
    public void showErrorMessage(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
