package com.jordan.fitnessbody.view.article.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.button.MaterialButton;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.adapter.prefs.PrefsHelper;
import com.jordan.fitnessbody.model.Article;
import com.jordan.fitnessbody.view.comment.CommentActivity;
import com.jordan.fitnessbody.view.editarticle.EditArticleActivity;
import com.jordan.fitnessbody.view.singlearticle.SingleArticleActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Map;

public class ArticleAdapter extends FirestorePagingAdapter<Article, ArticleAdapter.ArticleViewHolder> {

    private final Context mContext;

    private final FirebaseHelper mFirebaseHelper;

    private final PrefsHelper mPrefsHelper;

    private final Map<String, String> usernameMapper;

    private final PrettyTime mPrettyTime;

    private LayoutInflater mLayoutInflater;


    public ArticleAdapter(@NonNull FirestorePagingOptions<Article> options, Context context, FirebaseHelper firebaseHelper, PrefsHelper prefsHelper, Map<String, String> usernameMapper) {
        super(options);
        mContext = context;
        mFirebaseHelper = firebaseHelper;
        mPrefsHelper = prefsHelper;
        this.usernameMapper = usernameMapper;
        mPrettyTime = new PrettyTime();
    }

    @NonNull
    @Override
    public ArticleAdapter.ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null){
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        return new ArticleViewHolder(mLayoutInflater.inflate(R.layout.item_article, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull final ArticleViewHolder holder, int position, @NonNull final Article model) {

        String userId = model.getUserId();
        final String username = usernameMapper.get(userId);

        if (username != null)
            holder.usernameTextView.setText(username);

        holder.dateTextView.setText(mPrettyTime.format(model.getTimestamp()));

        Glide
                .with(mContext)
                .load(mFirebaseHelper.getUserProfileImage(model.getUserId()))
                .placeholder(R.drawable.default_user)
                .circleCrop()
                .into(holder.userImageView);

        if (model.getContent().length() >= 128){
            String subcontent = model.getContent().substring(0, 128);
            holder.contentTextView.setText(subcontent + "... See more");
            holder.contentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SingleArticleActivity.class);
                    intent.putExtra(SingleArticleActivity.EXTRA_ARTICLE, model);
                    intent.putExtra(SingleArticleActivity.EXTRA_USERNAME, username);
                    mContext.startActivity(intent);
                }
            });
        }else{
            holder.contentTextView.setText(model.getContent());
        }

        holder.likeCountTextView.setText(String.valueOf(model.getLikes().size()));

        boolean isLiked = model.getLikes().contains(mFirebaseHelper.getCurrentUserId());

        if (isLiked){
            holder.likeButton.setText("Liked");
            holder.likeButton.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.likeButton.setIconTintResource(R.color.colorPrimary);
        }else{
            holder.likeButton.setText("Like");
            holder.likeButton.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
            holder.likeButton.setIconTintResource(android.R.color.darker_gray);
        }

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLiked = model.getLikes().contains(mFirebaseHelper.getCurrentUserId());
                if (isLiked){
                    model.getLikes().remove(mFirebaseHelper.getCurrentUserId());
                    holder.likeButton.setText("Like");
                    holder.likeButton.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
                    holder.likeButton.setIconTintResource(android.R.color.darker_gray);
                    mFirebaseHelper.dislike(model.getCategoryId(), model.getId());
                }else{
                    model.getLikes().add(mFirebaseHelper.getCurrentUserId());
                    holder.likeButton.setText("Liked");
                    holder.likeButton.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    holder.likeButton.setIconTintResource(R.color.colorPrimary);
                    mFirebaseHelper.like(model.getCategoryId(), model.getId());
                }

                holder.likeCountTextView.setText(String.valueOf(model.getLikes().size()));
            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra(CommentActivity.EXTRA_CATEGORY_ID, model.getCategoryId());
                intent.putExtra(CommentActivity.EXTRA_ARTICLE_ID, model.getId());
                mContext.startActivity(intent);
            }
        });

        boolean isAdmin = mPrefsHelper.isAdmin();

        if (isAdmin){
            holder.editImageView.setVisibility(View.VISIBLE);
        }

        holder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditArticleActivity.class);
                intent.putExtra(EditArticleActivity.EXTRA_CATEGORY_ID, model.getCategoryId());
                intent.putExtra(EditArticleActivity.EXTRA_ARTICLE_ID, model.getId());
                intent.putExtra(EditArticleActivity.EXTRA_CONTENT, model.getContent());
                intent.putExtra(EditArticleActivity.EXTRA_STATE, model.getState().name());
                mContext.startActivity(intent);
            }
        });
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder{

        ImageView userImageView;

        TextView usernameTextView;

        TextView dateTextView;

        ImageView editImageView;

        TextView contentTextView;

        ImageView likeCountImageView;

        TextView likeCountTextView;

        MaterialButton likeButton;

        Button commentButton;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            bindViews();
        }

        private void bindViews() {
            userImageView = itemView.findViewById(R.id.user_image_view);
            usernameTextView = itemView.findViewById(R.id.user_name_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            editImageView = itemView.findViewById(R.id.edit_image_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
            likeCountImageView = itemView.findViewById(R.id.like_count_image_view);
            likeCountTextView = itemView.findViewById(R.id.like_count_text_view);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
        }

    }
}
