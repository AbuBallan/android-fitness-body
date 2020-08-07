package com.jordan.fitnessbody.view.editarticle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jordan.fitnessbody.FitnessApplication;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.adapter.prefs.PrefsHelper;
import com.jordan.fitnessbody.utils.AppConstants;

import java.util.HashMap;
import java.util.Map;

public class EditArticleActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID";

    public static final String EXTRA_ARTICLE_ID = "EXTRA_ARTICLE_ID";

    public static final String EXTRA_CONTENT = "EXTRA_CONTENT";

    public static final String EXTRA_STATE = "EXTRA_STATE";

    private Toolbar mToolbar;
    private EditText mContentEditText;
    private FirebaseHelper mFirebaseHelper;
    private PrefsHelper mPrefsHelper;
    private String mCategoryId;
    private String mArticleId;
    private String mContent;
    private AppConstants.ArticleState mState;
    private RadioGroup mStateRadioGroup;
    private Button mDeleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);

        mCategoryId = getIntent().getStringExtra(EXTRA_CATEGORY_ID);
        mArticleId = getIntent().getStringExtra(EXTRA_ARTICLE_ID);
        mContent = getIntent().getStringExtra(EXTRA_CONTENT);
        mState = AppConstants.ArticleState.valueOf(getIntent().getStringExtra(EXTRA_STATE));

        mFirebaseHelper = ((FitnessApplication) getApplication()).getFirebaseHelper();
        mPrefsHelper = ((FitnessApplication) getApplication()).getPrefsHelper();

        bindViews();
        setupViews();
        renderData();
    }

    private void bindViews() {
        mToolbar = findViewById(R.id.toolbar);
        mContentEditText = findViewById(R.id.content_edit_text);
        mStateRadioGroup = findViewById(R.id.state_radio_group);
        mDeleteButton = findViewById(R.id.delete_button);
    }

    private void setupViews() {
        setupToolbar();

        mContentEditText.requestFocus();

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseHelper.deleteArticle(mCategoryId, mArticleId);
                finish();
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void renderData() {
        mContentEditText.setText(mContent);

        if (mState == AppConstants.ArticleState.ACTIVE){
            mStateRadioGroup.check(R.id.active_radio_button);
        }else if (mState == AppConstants.ArticleState.PENDING){
            mStateRadioGroup.check(R.id.pending_radio_button);
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
        } else if (itemId == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void onNextClicked() {
        String content = mContentEditText.getText().toString();

        AppConstants.ArticleState state;
        int checkedRadioButtonId = mStateRadioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.active_radio_button){
            state = AppConstants.ArticleState.ACTIVE;
        }else{
            state = AppConstants.ArticleState.PENDING;
        }

        if (TextUtils.isEmpty(content)){
            showToast("there is no content");
            return;
        }

        Map<String, Object> edits = new HashMap<>();

        if (!content.equals(mContent)){
            edits.put("content", content);
        }

        if (!state.equals(mState)){
            edits.put("state", state.name());
        }

        mFirebaseHelper.updateArticle(mCategoryId, mArticleId, edits);

        finish();

    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}