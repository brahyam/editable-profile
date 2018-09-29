package com.dvipersquad.editableprofile.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dvipersquad.editableprofile.R;
import com.dvipersquad.editableprofile.data.User;
import com.dvipersquad.editableprofile.di.ActivityScoped;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * View for Profiles
 */
@ActivityScoped
public class ProfileFragment extends DaggerFragment implements ProfileContract.View {

    @NonNull
    private static final String ARGUMENT_USER_ID = "USER_ID";

    @Inject
    @Nullable
    String UserId;

    @Inject
    ProfileContract.Presenter presenter;

    ImageView imgProfilePicture;

    TextView txtProfileDisplayName;
    TextView txtProfileGender;
    TextView txtProfileHeight;
    TextView txtProfileAge;
    TextView txtProfileLocation;
    TextView txtProfileReligion;
    TextView txtProfileEthnicity;
    TextView txtProfileFigure;
    TextView txtProfileAboutMe;

    Button btnEditProfilePicture;

    @Inject
    public ProfileFragment() {
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_frag, container, false);
        imgProfilePicture = root.findViewById(R.id.imgProfilePicture);

        txtProfileDisplayName = root.findViewById(R.id.txtProfileDisplayName);
        txtProfileGender = root.findViewById(R.id.txtProfileGender);
        txtProfileHeight = root.findViewById(R.id.txtProfileHeight);
        txtProfileAge = root.findViewById(R.id.txtProfileAge);
        txtProfileLocation = root.findViewById(R.id.txtProfileLocation);
        txtProfileReligion = root.findViewById(R.id.txtProfileReligion);
        txtProfileEthnicity = root.findViewById(R.id.txtProfileEthnicity);
        txtProfileFigure = root.findViewById(R.id.txtProfileFigure);
        txtProfileAboutMe = root.findViewById(R.id.txtProfileAboutMe);

        btnEditProfilePicture = root.findViewById(R.id.btnEditProfilePicture);
        return root;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            txtProfileDisplayName.setText(null);
            txtProfileGender.setText(null);
            txtProfileHeight.setText(null);
            txtProfileAge.setText(null);
            txtProfileLocation.setText(null);
            txtProfileReligion.setText(null);
            txtProfileEthnicity.setText(null);
            txtProfileFigure.setText(null);
            txtProfileAboutMe.setText(null);
            btnEditProfilePicture.setEnabled(false);
        } else {
            btnEditProfilePicture.setEnabled(true);
        }
    }

    @Override
    public void showProfile(final User user) {
        txtProfileDisplayName.setText(user.getDisplayName());
        txtProfileGender.setText(user.getGender());
        txtProfileHeight.setText(String.format(Locale.getDefault(), "%.0f cms %s", user.getHeight(), getString(R.string.tall)));
        txtProfileAge.setText(String.format(Locale.getDefault(), "%d %s", getAgeFromDate(user.getBirthday()), getString(R.string.years_old)));
//        txtProfileLocation.setText(null); Needs to translate coordinates to city
        txtProfileReligion.setText(user.getReligion());
        txtProfileEthnicity.setText(user.getEthnicity());
        txtProfileFigure.setText(user.getFigure());
        txtProfileAboutMe.setText(user.getAboutMe());
        btnEditProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.openEditProfile(user);
            }
        });

        if (!TextUtils.isEmpty(user.getProfilePictureUrl())) {
            Picasso.get()
                    .load(user.getProfilePictureUrl())
                    .into(imgProfilePicture);
        }
    }

    @Override
    public void showLocation(String cityName) {
        txtProfileLocation.setText(cityName);
    }

    private int getAgeFromDate(Date date) {
        if (date != null) {
            Calendar birthDate = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            birthDate.setTime(date);
            int years = now.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
            if (now.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH)) {
                years--;
            }
            return years;
        }
        return 0;
    }

    @Override
    public void showMissingUser() {
        if (isActive() && getView() != null) {
            Snackbar.make(getView(), getString(R.string.error_loading_profile), Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void showEditProfileUI(String userId) {
        // Call Edit Activity
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
