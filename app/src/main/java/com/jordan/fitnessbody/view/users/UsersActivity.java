package com.jordan.fitnessbody.view.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jordan.fitnessbody.FitnessApplication;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.model.User;
import com.jordan.fitnessbody.view.users.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserAdapter.OnUserSelected {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private FirebaseHelper mFirebaseHelper;
    private UserAdapter mAdapter;
    private List<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mFirebaseHelper = ((FitnessApplication) getApplication()).getFirebaseHelper();

        bindViews();
        setupViews();
        getData();
    }

    private void bindViews() {
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    private void setupViews() {
        setupToolbar();
        setRecyclerView();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new UserAdapter(this, this, mFirebaseHelper);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Manage Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData() {
        mFirebaseHelper.getUsers().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mUsers = new ArrayList<>();
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    User user = queryDocumentSnapshot.toObject(User.class);
                    user.setId(queryDocumentSnapshot.getId());
                    mUsers.add(user);
                }
                mAdapter.setItems(mUsers);
            }
        });
    }

    @Override
    public void onSelect(final int position, final User user) {

        if (user.isAdmin()){
            new AlertDialog.Builder(this)
                    .setTitle("Update " + user.getName() + " role")
                    .setMessage("update " + user.getName() + " role to User")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (user.getId().equals(mFirebaseHelper.getCurrentUserId())){
                                showToast("You cannot update your role");
                            }else {
                                user.setAdmin(false);
                                mAdapter.notifyItemChanged(position);
                                mFirebaseHelper.updateUserRole(user.getId(), user.isAdmin());
                            }

                        }
                    })
                    .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Update " + user.getName() + " role")
                    .setMessage("update " + user.getName() + " role to Admin")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (user.getId().equals(mFirebaseHelper.getCurrentUserId())){
                                showToast("You cannot update your role");
                            }else {
                                user.setAdmin(true);
                                mAdapter.notifyItemChanged(position);
                                mFirebaseHelper.updateUserRole(user.getId(), user.isAdmin());
                            }
                        }
                    })
                    .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}