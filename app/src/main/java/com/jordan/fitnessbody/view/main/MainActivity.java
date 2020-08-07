package com.jordan.fitnessbody.view.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.view.main.fragment.HomeFragment;
import com.jordan.fitnessbody.view.main.fragment.LocatorFragment;
import com.jordan.fitnessbody.view.main.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FofoMainActivity";

    private BottomNavigationView mBottomNavigationView;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        setupViews();
        initViews();

    }

    private void bindViews() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupViews() {

        mFragmentManager = getSupportFragmentManager();

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction = mFragmentManager.beginTransaction();

                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.item_home:
                        HomeFragment homeFragment = HomeFragment.create();
                        transaction.replace(R.id.content, homeFragment);
                        transaction.commit();
                        return true;
                    case R.id.item_gym_locator:
                        LocatorFragment locatorFragment = LocatorFragment.create();
                        transaction.replace(R.id.content, locatorFragment);
                        transaction.commit();
                        return true;
                    case R.id.item_settings:
                        SettingFragment settingFragment = SettingFragment.create();
                        transaction.replace(R.id.content, settingFragment);
                        transaction.commit();
                        return true;
                }

                return false;
            }
        });
    }

    private void initViews() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        HomeFragment homeFragment = HomeFragment.create();
        transaction.add(R.id.content, homeFragment);
        transaction.commit();
    }

    public void selectNavigationItem(@IdRes int itemId){
        mBottomNavigationView.setSelectedItemId(itemId);
    }

}