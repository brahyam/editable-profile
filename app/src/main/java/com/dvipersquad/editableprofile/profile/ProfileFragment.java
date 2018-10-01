package com.dvipersquad.editableprofile.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dvipersquad.editableprofile.R;
import com.dvipersquad.editableprofile.data.Attribute;
import com.dvipersquad.editableprofile.data.Profile;
import com.dvipersquad.editableprofile.di.ActivityScoped;
import com.dvipersquad.editableprofile.editprofile.EditProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * View for Profiles
 */
@ActivityScoped
public class ProfileFragment extends DaggerFragment implements ProfileContract.View {

    @NonNull
    private static final String ARGUMENT_PROFILE_ID = "PROFILE_ID";
    private static final String ELLIPSIS = "...";
    private static final int REQUEST_SELECT_PICTURE = 100;
    private static final String TAG = ProfileFragment.class.getSimpleName();

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Inject
    @Nullable
    String profileId;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        btnEditProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && isActive()) {
                    // Verify storage permissions are granted.
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        presenter.showImageSelectionDialog();
                    } else {
                        requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                    }
                }
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemEditProfile:
                presenter.showEditProfileView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            txtProfileDisplayName.setText(ELLIPSIS);
            txtProfileGender.setText(ELLIPSIS);
            txtProfileHeight.setText(ELLIPSIS);
            txtProfileAge.setText(ELLIPSIS);
            txtProfileLocation.setText(ELLIPSIS);
            txtProfileReligion.setText(ELLIPSIS);
            txtProfileEthnicity.setText(ELLIPSIS);
            txtProfileFigure.setText(ELLIPSIS);
            txtProfileAboutMe.setText(ELLIPSIS);
            btnEditProfilePicture.setEnabled(false);
        } else {
            btnEditProfilePicture.setEnabled(true);
        }
    }

    @Override
    public void showProfile(final Profile profile) {
        txtProfileDisplayName.setText(profile.getDisplayName());
        txtProfileHeight.setText(String.format(Locale.getDefault(), "%.0f cms %s", profile.getHeight(), getString(R.string.tall)));
        txtProfileAge.setText(String.format(Locale.getDefault(), "%d %s", getAgeFromDate(profile.getBirthday()), getString(R.string.years_old)));
        txtProfileAboutMe.setText(profile.getAboutMe());

        if (!TextUtils.isEmpty(profile.getProfilePictureUrl())) {
            Picasso.get()
                    .load(profile.getProfilePictureUrl())
                    .fit()
                    .centerCrop()
                    .into(imgProfilePicture);
        }
    }

    @Override
    public void showProfileImage(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(imgProfilePicture);
        }
    }

    @Override
    public void showCity(String cityName) {
        txtProfileLocation.setText(String.format(Locale.getDefault(), "%s %s", getString(R.string.from), cityName));
    }

    @Override
    public void showAttributes(List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            switch (attribute.getType()) {
                case Attribute.TYPE_GENDER:
                    txtProfileGender.setText(attribute.getName());
                    break;
                case Attribute.TYPE_ETHNICITY:
                    txtProfileEthnicity.setText(attribute.getName());
                    break;
                case Attribute.TYPE_RELIGION:
                    txtProfileReligion.setText(attribute.getName());
                    break;
                case Attribute.TYPE_FIGURE:
                    txtProfileFigure.setText(attribute.getName());
                    break;
            }
        }
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
    public void showMissingProfile() {
        txtProfileDisplayName.setText(getString(R.string.error_loading_profile));
        txtProfileGender.setText(ELLIPSIS);
        txtProfileHeight.setText(ELLIPSIS);
        txtProfileAge.setText(ELLIPSIS);
        txtProfileLocation.setText(ELLIPSIS);
        txtProfileReligion.setText(ELLIPSIS);
        txtProfileEthnicity.setText(ELLIPSIS);
        txtProfileFigure.setText(ELLIPSIS);
        txtProfileAboutMe.setText(ELLIPSIS);
    }

    @Override
    public void showErrorMessage(String message) {
        if (isActive() && getView() != null) {
            Snackbar.make(getView(), getString(R.string.error_loading_profile), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showEditProfileUI(String profileId) {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        intent.putExtra(EditProfileActivity.EXTRA_PROFILE_ID, profileId);
        startActivity(intent);
    }

    @Override
    public void showImageSelectionUI() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_SELECT_PICTURE);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    presenter.onImageSelected(getRealPathFromURI(getActivity(), selectedImage));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.showImageSelectionDialog();
                }
                break;
        }
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
