package com.jordan.fitnessbody.view.singlearticle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jordan.fitnessbody.FitnessApplication;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.adapter.prefs.PrefsHelper;
import com.jordan.fitnessbody.model.Article;
import com.jordan.fitnessbody.view.comment.CommentActivity;
import com.jordan.fitnessbody.view.editarticle.EditArticleActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.HashMap;
import java.util.Map;

public class SingleArticleActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE = "EXTRA_ARTICLE";

    public static final String EXTRA_USERNAME = "EXTRA_USERNAME";

    private Toolbar mToolbar;

    private ImageView userImageView;

    private TextView usernameTextView;

    private TextView dateTextView;

    private ImageView editImageView;

    private TextView contentTextView;

    private ImageView likeCountImageView;

    private TextView likeCountTextView;

    private MaterialButton likeButton;

    private Button commentButton;

    private FirebaseHelper mFirebaseHelper;
    private PrefsHelper mPrefsHelper;
    private PrettyTime mPrettyTime;

    private Article model;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_article);

        model = getIntent().getParcelableExtra(EXTRA_ARTICLE);
        username = getIntent().getStringExtra(EXTRA_USERNAME);

        mFirebaseHelper = ((FitnessApplication) getApplication()).getFirebaseHelper();
        mPrefsHelper = ((FitnessApplication) getApplication()).getPrefsHelper();
        mPrettyTime = new PrettyTime();

        bindViews();
        setupViews();

    }

    private void bindViews() {
        mToolbar = findViewById(R.id.toolbar);
        userImageView = findViewById(R.id.user_image_view);
        usernameTextView = findViewById(R.id.user_name_text_view);
        dateTextView = findViewById(R.id.date_text_view);
        editImageView = findViewById(R.id.edit_image_view);
        contentTextView = findViewById(R.id.content_text_view);
        likeCountImageView = findViewById(R.id.like_count_image_view);
        likeCountTextView = findViewById(R.id.like_count_text_view);
        likeButton = findViewById(R.id.like_button);
        commentButton = findViewById(R.id.comment_button);
    }

    private void setupViews() {
        setupToolbar();
        setupArticle();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Article");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupArticle() {
        if (username != null)
            usernameTextView.setText(username);

        dateTextView.setText(mPrettyTime.format(model.getTimestamp()));

        Glide
                .with(this)
                .load(mFirebaseHelper.getUserProfileImage(model.getUserId()))
                .placeholder(R.drawable.default_user)
                .circleCrop()
                .into(userImageView);

        contentTextView.setText(model.getContent());

        likeCountTextView.setText(String.valueOf(model.getLikes().size()));

        boolean isLiked = model.getLikes().contains(mFirebaseHelper.getCurrentUserId());

        if (isLiked) {
            likeButton.setText("Liked");
            likeButton.setTextColor(SingleArticleActivity.this.getResources().getColor(R.color.colorPrimary));
            likeButton.setIconTintResource(R.color.colorPrimary);
        } else {
            likeButton.setText("Like");
            likeButton.setTextColor(SingleArticleActivity.this.getResources().getColor(android.R.color.darker_gray));
            likeButton.setIconTintResource(android.R.color.darker_gray);
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLiked = model.getLikes().contains(mFirebaseHelper.getCurrentUserId());
                if (isLiked) {
                    model.getLikes().remove(mFirebaseHelper.getCurrentUserId());
                    likeButton.setText("Like");
                    likeButton.setTextColor(SingleArticleActivity.this.getResources().getColor(android.R.color.darker_gray));
                    likeButton.setIconTintResource(android.R.color.darker_gray);
                    mFirebaseHelper.dislike(model.getCategoryId(), model.getId());
                } else {
                    model.getLikes().add(mFirebaseHelper.getCurrentUserId());
                    likeButton.setText("Liked");
                    likeButton.setTextColor(SingleArticleActivity.this.getResources().getColor(R.color.colorPrimary));
                    likeButton.setIconTintResource(R.color.colorPrimary);
                    mFirebaseHelper.like(model.getCategoryId(), model.getId());
                }

                likeCountTextView.setText(String.valueOf(model.getLikes().size()));
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleArticleActivity.this, CommentActivity.class);
                intent.putExtra(CommentActivity.EXTRA_CATEGORY_ID, model.getCategoryId());
                intent.putExtra(CommentActivity.EXTRA_ARTICLE_ID, model.getId());
                SingleArticleActivity.this.startActivity(intent);
            }
        });

        boolean isAdmin = mPrefsHelper.isAdmin();

        if (isAdmin) {
            editImageView.setVisibility(View.VISIBLE);
        }

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleArticleActivity.this, EditArticleActivity.class);
                intent.putExtra(EditArticleActivity.EXTRA_CATEGORY_ID, model.getCategoryId());
                intent.putExtra(EditArticleActivity.EXTRA_ARTICLE_ID, model.getId());
                intent.putExtra(EditArticleActivity.EXTRA_CONTENT, model.getContent());
                intent.putExtra(EditArticleActivity.EXTRA_STATE, model.getState().name());
                SingleArticleActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}