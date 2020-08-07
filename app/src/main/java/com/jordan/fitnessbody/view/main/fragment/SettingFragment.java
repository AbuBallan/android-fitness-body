package com.jordan.fitnessbody.view.main.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jordan.fitnessbody.FitnessApplication;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.adapter.prefs.PrefsHelper;
import com.jordan.fitnessbody.view.editprofile.EditProfileActivity;
import com.jordan.fitnessbody.view.main.MainActivity;
import com.jordan.fitnessbody.view.splash.SplashActivity;
import com.jordan.fitnessbody.view.users.UsersActivity;

public class SettingFragment extends Fragment {

    private Toolbar mToolbar;
    private CardView mProfileCardView;
    private FirebaseHelper mFirebaseHelper;
    private PrefsHelper mPrefsHelper;
    private ImageView mUserImageView;
    private TextView mUserNameTextView;
    private TextView mRoleTextView;
    private View mSignOutView;
    private View mManageUsersView;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment create() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseHelper = ((FitnessApplication) getActivity().getApplication()).getFirebaseHelper();
        mPrefsHelper = ((FitnessApplication) getActivity().getApplication()).getPrefsHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        bindViews(view);
        setupToolbar();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
    }

    private void bindViews(View view) {
        mToolbar = view.findViewById(R.id.toolbar);
        mProfileCardView = view.findViewById(R.id.profile_card_view);
        mUserImageView = view.findViewById(R.id.user_image_view);
        mUserNameTextView = view.findViewById(R.id.user_name_text_view);
        mRoleTextView = view.findViewById(R.id.role_text_view);
        mManageUsersView = view.findViewById(R.id.manage_user_layout);
        mSignOutView = view.findViewById(R.id.sign_out_layout);
    }

    private void setupToolbar() {
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Settings");
    }

    private void setupViews() {
        setupProfile();

        mSignOutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                mPrefsHelper.setIsAdmin(false);
                                startActivity(new Intent(getContext(), SplashActivity.class));
                                getActivity().finish();
                            }
                        });
            }
        });

        mManageUsersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UsersActivity.class);
                getContext().startActivity(intent);
            }
        });
    }


    private void setupProfile() {
        mProfileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.putExtra(EditProfileActivity.EXTRA_FROM_SETTINGS, true);
                getContext().startActivity(intent);
            }
        });

    }

    private void renderProfile() {
        renderUserImage(mFirebaseHelper.getCurrentUserProfileImage());

        if (mFirebaseHelper.getCurrentUserDisplayName() != null)
            mUserNameTextView.setText(mFirebaseHelper.getCurrentUserDisplayName());

        if (mPrefsHelper.isAdmin()) {
            mRoleTextView.setVisibility(View.VISIBLE);
            mManageUsersView.setVisibility(View.VISIBLE);
        }
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
    public void onResume() {
        super.onResume();
        renderProfile();
    }
}