package com.jordan.fitnessbody.view.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jordan.fitnessbody.FitnessApplication;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.adapter.prefs.PrefsHelper;
import com.jordan.fitnessbody.model.User;
import com.jordan.fitnessbody.view.editprofile.EditProfileActivity;
import com.jordan.fitnessbody.view.main.MainActivity;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "FOFOSplashActivity";

    private static final int RC_SIGN_IN = 159;

    private FirebaseHelper mFirebaseHelper;

    private PrefsHelper mPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseHelper = ((FitnessApplication) getApplication()).getFirebaseHelper();
        mPrefsHelper = ((FitnessApplication) getApplication()).getPrefsHelper();

        if (mFirebaseHelper.isUserSignedIn()) {
            updateUserLocalData();
        } else {
            openSignInActivity();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void openSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.PhoneBuilder().build()))
                        .build(),
                RC_SIGN_IN);
    }

    private void openEditProfileActivity() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                updateUserLocalData();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    openSignInActivity();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showToast("Check your internet connection");
                    return;
                }

                showToast("Unknown Error");
                finish();

            }
        }
    }

    private void updateUserLocalData() {

        final boolean isAdmin = mPrefsHelper.isAdmin();
        final String currentUserDisplayName = mFirebaseHelper.getCurrentUserDisplayName();

        mFirebaseHelper.currentUser().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null){

                        if (user.isAdmin() != isAdmin){
                            mPrefsHelper.setIsAdmin(user.isAdmin());
                        }

                        if (!user.getName().equals(currentUserDisplayName)){
                            mFirebaseHelper.setCurrentUserDisplayName(user.getName());
                        }

                        openMainActivity();

                    }else{
                        openEditProfileActivity();
                    }
                }else{
                    openEditProfileActivity();
                }
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}