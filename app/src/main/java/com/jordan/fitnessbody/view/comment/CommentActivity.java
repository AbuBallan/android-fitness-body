package com.jordan.fitnessbody.view.comment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
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
import com.jordan.fitnessbody.model.Comment;
import com.jordan.fitnessbody.utils.AppConstants;
import com.jordan.fitnessbody.view.article.adapter.ArticleAdapter;
import com.jordan.fitnessbody.view.comment.adapter.CommentAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "FOFOCommentActivity";

    public static final String EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID";

    public static final String EXTRA_ARTICLE_ID = "EXTRA_ARTICLE_ID";

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private FirebaseHelper mFirebaseHelper;
    private PrefsHelper mPrefsHelper;
    private String mCategoryId;
    private String mArticleId;
    private Map<String, String> mUsernameMapper;
    private Button mCommentButton;
    private EditText mCommentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mCategoryId = getIntent().getStringExtra(EXTRA_CATEGORY_ID);
        mArticleId = getIntent().getStringExtra(EXTRA_ARTICLE_ID);

        mFirebaseHelper = ((FitnessApplication) getApplication()).getFirebaseHelper();
        mPrefsHelper = ((FitnessApplication) getApplication()).getPrefsHelper();

        bindViews();
        setupViews();
    }

    private void bindViews() {
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        mCommentEditText = findViewById(R.id.comment_edit_text);
        mCommentButton = findViewById(R.id.comment_button);
    }

    private void setupViews() {
        setupToolbar();

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

        setupCommentBar();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        Query baseQuery = mFirebaseHelper.getArticleComments(mCategoryId, mArticleId).orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, new SnapshotParser<Comment>() {
                            @NonNull
                            @Override
                            public Comment parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                Comment comment = snapshot.toObject(Comment.class);
                                comment.setCategoryId(mCategoryId);
                                comment.setArticleId(mArticleId);
                                comment.setId(snapshot.getId());
                                return comment;
                            }
                        })
                .build();

        CommentAdapter adapter = new CommentAdapter(options, this, mFirebaseHelper, mPrefsHelper, mUsernameMapper);

        mRecyclerView.setAdapter(adapter);
    }

    private void setupCommentBar() {
        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentContent = mCommentEditText.getText().toString();

                if (TextUtils.isEmpty(commentContent)){
                    showToast("Please enter your comment");
                    return;
                }

                String userId = mFirebaseHelper.getCurrentUserId();

                Comment comment = new Comment(
                        mCategoryId,
                        mArticleId,
                        userId,
                        commentContent,
                        new Date()
                );

                mFirebaseHelper.addComment(comment);

                mCommentEditText.setText("");
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

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}