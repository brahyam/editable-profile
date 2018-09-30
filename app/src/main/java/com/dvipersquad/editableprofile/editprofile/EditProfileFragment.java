package com.dvipersquad.editableprofile.editprofile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dvipersquad.editableprofile.R;
import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.City;
import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.di.ActivityScoped;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

@ActivityScoped
public class EditProfileFragment extends DaggerFragment implements EditProfileContract.View {

    @NonNull
    private static final String ARGUMENT_PROFILE_ID = "PROFILE_ID";
    private static final String ELLIPSIS = "...";

    private TextView txtEditProfileInfo;
    private Button btnEditProfileRealName;
    private Button btnEditProfileDisplayName;
    private Button btnEditProfileGender;
    private Button btnEditProfileBirthday;
    private Button btnEditProfileLocation;
    private Button btnEditProfileMaritalStatus;
    private Button btnEditProfileReligion;
    private Button btnEditProfileEthnicity;
    private Button btnEditProfileFigure;
    private Button btnEditProfileOccupation;
    private Button btnEditProfileAboutMe;

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

        btnEditProfileRealName = rootView.findViewById(R.id.btnEditProfileRealName);
        btnEditProfileRealName.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_REAL_NAME));
        btnEditProfileDisplayName = rootView.findViewById(R.id.btnEditProfileDisplayName);
        btnEditProfileDisplayName.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_DISPLAY_NAME));
        btnEditProfileGender = rootView.findViewById(R.id.btnEditProfileGender);
        btnEditProfileGender.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_GENDER));
        btnEditProfileBirthday = rootView.findViewById(R.id.btnEditProfileBirthday);
        btnEditProfileBirthday.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_BIRTHDAY));
        btnEditProfileLocation = rootView.findViewById(R.id.btnEditProfileLocation);
        btnEditProfileLocation.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_LOCATION));
        btnEditProfileMaritalStatus = rootView.findViewById(R.id.btnEditProfileMaritalStatus);
        btnEditProfileMaritalStatus.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_MARITAL_STATUS));
        btnEditProfileReligion = rootView.findViewById(R.id.btnEditProfileReligion);
        btnEditProfileReligion.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_RELIGION));
        btnEditProfileEthnicity = rootView.findViewById(R.id.btnEditProfileEthnicity);
        btnEditProfileEthnicity.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_ETHNICITY));
        btnEditProfileFigure = rootView.findViewById(R.id.btnEditProfileFigure);
        btnEditProfileFigure.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_FIGURE));
        btnEditProfileOccupation = rootView.findViewById(R.id.btnEditProfileOccupation);
        btnEditProfileOccupation.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_OCUPATION));
        btnEditProfileAboutMe = rootView.findViewById(R.id.btnEditProfileAboutMe);
        btnEditProfileAboutMe.setOnClickListener(getOnClickListenerForType(Attribute.TYPE_ABOUT_ME));
        return rootView;
    }

    private View.OnClickListener getOnClickListenerForType(final String attributeType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.modifyField(attributeType);
            }
        };
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        btnEditProfileRealName.setText(ELLIPSIS);
        btnEditProfileDisplayName.setText(ELLIPSIS);
        btnEditProfileGender.setText(ELLIPSIS);
        btnEditProfileBirthday.setText(ELLIPSIS);
        btnEditProfileLocation.setText(ELLIPSIS);
        btnEditProfileMaritalStatus.setText(ELLIPSIS);
        btnEditProfileReligion.setText(ELLIPSIS);
        btnEditProfileEthnicity.setText(ELLIPSIS);
        btnEditProfileFigure.setText(ELLIPSIS);
        btnEditProfileOccupation.setText(ELLIPSIS);
        btnEditProfileAboutMe.setText(ELLIPSIS);
    }

    @Override
    public void showProfile(Profile profile) {
        btnEditProfileRealName.setText(profile.getRealName());
        btnEditProfileDisplayName.setText(profile.getDisplayName());
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        btnEditProfileBirthday.setText(format.format(profile.getBirthday()));
        btnEditProfileOccupation.setText(profile.getOccupation());
        btnEditProfileAboutMe.setText(profile.getAboutMe());
    }

    @Override
    public void showAttributes(List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            switch (attribute.getType()) {
                case Attribute.TYPE_DISPLAY_NAME:
                    btnEditProfileDisplayName.setText(attribute.getName());
                    break;
                case Attribute.TYPE_REAL_NAME:
                    btnEditProfileRealName.setText(attribute.getName());
                    break;
                case Attribute.TYPE_OCUPATION:
                    btnEditProfileOccupation.setText(attribute.getName());
                    break;
                case Attribute.TYPE_BIRTHDAY:
                    btnEditProfileBirthday.setText(attribute.getName());
                    break;
                case Attribute.TYPE_GENDER:
                    btnEditProfileGender.setText(attribute.getName());
                    break;
                case Attribute.TYPE_ETHNICITY:
                    btnEditProfileEthnicity.setText(attribute.getName());
                    break;
                case Attribute.TYPE_RELIGION:
                    btnEditProfileReligion.setText(attribute.getName());
                    break;
                case Attribute.TYPE_FIGURE:
                    btnEditProfileFigure.setText(attribute.getName());
                    break;
                case Attribute.TYPE_MARITAL_STATUS:
                    btnEditProfileMaritalStatus.setText(attribute.getName());
                    break;
                case Attribute.TYPE_ABOUT_ME:
                    btnEditProfileAboutMe.setText(attribute.getName());
                    break;
                case Attribute.TYPE_LOCATION:
                    btnEditProfileLocation.setText(attribute.getName());
                    break;
            }
        }
    }

    @Override
    public void showCity(String cityName) {
        btnEditProfileLocation.setText(cityName);
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
    public void showEditSingleChoiceUI(final List<Attribute> attributes, Attribute currentValue, boolean mandatory) {
        if (getActivity() != null && isActive()) {
            String items[] = new String[attributes.size()];
            int selected = -1;
            for (int i = 0; i < attributes.size(); i++) {
                Attribute attribute = attributes.get(i);
                items[i] = attributes.get(i).getName();
                if (attribute.getId().equals(currentValue.getId())) {
                    selected = i;
                }
            }
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(getString(R.string.choose_an_option));
            dialogBuilder.setSingleChoiceItems(items, selected, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    presenter.attributeSelected(attributes.get(i));
                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }
    }

    @Override
    public void showEditDateUI(Date currentValue) {

    }

    @Override
    public void showEditFreeTextUI(Attribute attribute, int limit, boolean mandatory) {

    }

    @Override
    public void showEditLocationUI(City currentValue) {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
