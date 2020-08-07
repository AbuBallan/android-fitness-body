package com.jordan.fitnessbody;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jordan.fitnessbody.adapter.firebase.AppFirebaseHelper;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.adapter.prefs.AppPrefsHelper;
import com.jordan.fitnessbody.adapter.prefs.PrefsHelper;
import com.jordan.fitnessbody.model.User;

public class FitnessApplication extends Application {

    private FirebaseHelper mFirebaseHelper;
    private PrefsHelper mPrefsHelper;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFirebaseHelper = new AppFirebaseHelper();
        mPrefsHelper = new AppPrefsHelper(this);

    }

    public FirebaseHelper getFirebaseHelper() {
        return mFirebaseHelper;
    }

    public PrefsHelper getPrefsHelper() {
        return mPrefsHelper;
    }
}
