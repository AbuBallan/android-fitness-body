package com.jordan.fitnessbody.view.addarticle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.jordan.fitnessbody.FitnessApplication;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.adapter.prefs.PrefsHelper;
import com.jordan.fitnessbody.model.Article;
import com.jordan.fitnessbody.utils.AppConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class AddArticleActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID";

    private Toolbar mToolbar;
    private EditText mContentEditText;
    private FirebaseHelper mFirebaseHelper;
    private PrefsHelper mPrefsHelper;
    private String mCategoryId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        mCategoryId = getIntent().getStringExtra(EXTRA_CATEGORY_ID);
        mFirebaseHelper = ((FitnessApplication) getApplication()).getFirebaseHelper();
        mPrefsHelper = ((FitnessApplication) getApplication()).getPrefsHelper();

        bindViews();
        setupViews();

    }

    private void bindViews() {
        mToolbar = findViewById(R.id.toolbar);
        mContentEditText = findViewById(R.id.content_edit_text);
    }

    private void setupViews() {
        setupToolbar();
        mContentEditText.requestFocus();
    }


    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add a new article...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        } else if (itemId == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void onNextClicked() {
        String content = mContentEditText.getText().toString();

        if (TextUtils.isEmpty(content)){
            showToast("there is no content");
            return;
        }

        boolean isAdmin = mPrefsHelper.isAdmin();
        String userId = mFirebaseHelper.getCurrentUserId();

        Article article = new Article(
                mCategoryId,
                userId,
                content,
                new ArrayList<String>(),
                (isAdmin)? AppConstants.ArticleState.ACTIVE: AppConstants.ArticleState.PENDING,
                new Date()
        );

        mFirebaseHelper.addArticle(article);

        if (!isAdmin)
            new AlertDialog.Builder(this)
                    .setTitle("Article submitted")
                    .setMessage("Your article has been submitted and is pending approval by an admin.")
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        else{
            finish();
        }
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}