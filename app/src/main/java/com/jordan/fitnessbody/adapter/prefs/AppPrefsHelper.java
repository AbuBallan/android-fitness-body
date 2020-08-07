package com.jordan.fitnessbody.adapter.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefsHelper implements PrefsHelper {

    private final Context mContext;

    private final SharedPreferences mSharedPreferences;

    private final SharedPreferences.Editor mEditor;

    public AppPrefsHelper(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences("fitness", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }


    @Override
    public void setIsAdmin(boolean isAdmin) {
        mEditor.putBoolean("IS_ADMIN", isAdmin);
        mEditor.apply();
    }

    @Override
    public boolean isAdmin() {
        return mSharedPreferences.getBoolean("IS_ADMIN", false);
    }
}
