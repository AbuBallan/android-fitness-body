package com.jordan.fitnessbody.view.editprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jordan.fitnessbody.FitnessApplication;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.adapter.prefs.PrefsHelper;
import com.jordan.fitnessbody.model.User;
import com.jordan.fitnessbody.service.UserImageUploadService;
import com.jordan.fitnessbody.utils.AppConstants;
import com.jordan.fitnessbody.utils.CommonUtils;
import com.jordan.fitnessbody.view.main.MainActivity;

public class EditProfileActivity extends AppCompatActivity {

    public static final String EXTRA_FROM_SETTINGS = "EXTRA_FROM_SETTINGS";

    private boolean mIsFromSettings;
    private ImageView mUserImageView;
    private EditText mUserNameEditText;
    private Uri mSelectedImageUri;
    private FirebaseHelper mFirebaseHelper;
    private PrefsHelper mPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mIsFromSettings  =getIntent().getBooleanExtra(EXTRA_FROM_SETTINGS, false);

        // get data manager
        mFirebaseHelper = ((FitnessApplication) getApplication()).getFirebaseHelper();
        mPrefsHelper = ((FitnessApplication) getApplication()).getPrefsHelper();

        // get views
        Toolbar toolbar = findViewById(R.id.toolbar);
        mUserImageView = findViewById(R.id.user_image_view);
        mUserNameEditText = findViewById(R.id.user_name_edit_text);

        // init views
        initToolbar(toolbar);
        initUserImageView(mUserImageView);

        // render data
        renderUserImage(mFirebaseHelper.getCurrentUserProfileImage());

        if (mFirebaseHelper.getCurrentUserDisplayName() != null)
            mUserNameEditText.setText(mFirebaseHelper.getCurrentUserDisplayName());
    }

    private void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);

        if (mIsFromSettings) {
            getSupportActionBar().setTitle("Edit Profile");
        }else{
            getSupportActionBar().setTitle("Registration");
        }

    }

    private void initUserImageView(ImageView userImageView) {
        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.openGalleryIntentForSingleImage(EditProfileActivity.this);
            }
        });
    }

    private void renderUserImage(Object o){
        Glide
                .with(this)
                .load(o)
                .placeholder(R.drawable.default_user)
                .circleCrop()
                .into(mUserImageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_GET_GALLERY_SINGLE_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                mSelectedImageUri = CommonUtils.handleGalleryIntentResultForSingleImage(this, data);
                renderUserImage(mSelectedImageUri);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_next){
            onNextClicked();
            return true;
        }

        return false;
    }

    private void onNextClicked() {

        String userName = mUserNameEditText.getText().toString();

        if (TextUtils.isEmpty(userName)){
            showToast("Please enter your name");
            return;
        }

        if (mSelectedImageUri != null){
            startService(new Intent(this, UserImageUploadService.class)
                    .putExtra(UserImageUploadService.EXTRA_FILE_URI, mSelectedImageUri)
                    .putExtra(UserImageUploadService.EXTRA_USER_ID, mFirebaseHelper.getCurrentUserId())
                    .setAction(UserImageUploadService.ACTION_UPLOAD));
        }

        boolean isAdmin = mPrefsHelper.isAdmin();

        User user = new User(userName, mFirebaseHelper.getCurrentUserPhoneNumber(), isAdmin);

        mFirebaseHelper.updateCurrentUser(user);

        if (mIsFromSettings){
            finish();
        }else {
            openMainActivity();
        }

    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}