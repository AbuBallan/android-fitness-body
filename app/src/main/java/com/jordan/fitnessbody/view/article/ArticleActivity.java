package com.jordan.fitnessbody.view.article;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.jordan.fitnessbody.FitnessApplication;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.adapter.prefs.PrefsHelper;
import com.jordan.fitnessbody.model.Article;
import com.jordan.fitnessbody.view.addarticle.AddArticleActivity;
import com.jordan.fitnessbody.view.article.adapter.ArticleAdapter;

import java.util.HashMap;
import java.util.Map;

public class ArticleActivity extends AppCompatActivity {

    private static final String TAG = "FOFOArticleActivity";

    public static final String EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID";

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddFAB;
    private FirebaseHelper mFirebaseHelper;
    private PrefsHelper mPrefsHelper;
    private String mCategoryId;
    private Map<String, String> mUsernameMapper;
    private ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mCategoryId = getIntent().getStringExtra(EXTRA_CATEGORY_ID);

        mFirebaseHelper = ((FitnessApplication) getApplication()).getFirebaseHelper();
        mPrefsHelper = ((FitnessApplication) getApplication()).getPrefsHelper();

        bindViews();
        setupViews();

    }

    private void bindViews() {
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        mAddFAB = findViewById(R.id.add_fab);
    }

    private void setupViews() {
        setupToolbar();

        setupFAB();

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Articles");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        Query baseQuery = mFirebaseHelper.getCategoryArticles(mCategoryId);

        if (!mPrefsHelper.isAdmin()) {
            baseQuery = baseQuery.whereEqualTo("state", "ACTIVE");
        }

        baseQuery = baseQuery.orderBy("timestamp", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(4)
                .setPageSize(10)
                .build();

        FirestorePagingOptions<Article> options = new FirestorePagingOptions.Builder<Article>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, new SnapshotParser<Article>() {
                    @NonNull
                    @Override
                    public Article parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Article article = snapshot.toObject(Article.class);
                        article.setId(snapshot.getId());
                        article.setCategoryId(mCategoryId);
                        return article;
                    }
                })
                .build();

        ArticleAdapter adapter = new ArticleAdapter(options, this, mFirebaseHelper, mPrefsHelper, mUsernameMapper) {
            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                if (state == LoadingState.LOADED || state == LoadingState.FINISHED) {
                    if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                } else if (state == LoadingState.ERROR) {
                    showToast("Error");
                }
            }
        };

        mRecyclerView.setAdapter(adapter);

    }

    private void setupFAB() {
        mAddFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArticleActivity.this, AddArticleActivity.class);
                intent.putExtra(AddArticleActivity.EXTRA_CATEGORY_ID, mCategoryId);
                ArticleActivity.this.startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();

        mLoadingDialog = ProgressDialog.show(this, "Loading",
                "Please wait...", true);

        mFirebaseHelper.usernameMapper(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsernameMapper = (Map<String, String>) snapshot.getValue();
                setupRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mUsernameMapper = new HashMap<>();
                setupRecyclerView();
            }
        });

    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}