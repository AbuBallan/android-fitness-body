package com.jordan.fitnessbody.view.comment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.adapter.prefs.PrefsHelper;
import com.jordan.fitnessbody.model.Comment;
import com.jordan.fitnessbody.view.article.adapter.ArticleAdapter;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Map;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder> {

    private final Context mContext;

    private final FirebaseHelper mFirebaseHelper;

    private final PrefsHelper mPrefsHelper;

    private final Map<String, String> usernameMapper;

    private final PrettyTime mPrettyTime;

    private LayoutInflater mLayoutInflater;

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, Context context, FirebaseHelper firebaseHelper, PrefsHelper prefsHelper, Map<String, String> usernameMapper) {
        super(options);
        mContext = context;
        mFirebaseHelper = firebaseHelper;
        mPrefsHelper = prefsHelper;
        this.usernameMapper = usernameMapper;
        mPrettyTime = new PrettyTime();
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
        String userId = model.getUserId();
        String username = usernameMapper.get(userId);

        if (username != null)
            holder.usernameTextView.setText(username);

        holder.dateTextView.setText(mPrettyTime.format(model.getTimestamp()));

        Glide
                .with(mContext)
                .load(mFirebaseHelper.getUserProfileImage(model.getUserId()))
                .placeholder(R.drawable.default_user)
                .circleCrop()
                .into(holder.userImageView);

        holder.contentTextView.setText(model.getContent());

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null){
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        return new CommentViewHolder(mLayoutInflater.inflate(R.layout.item_comment, parent, false));
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{

        ImageView userImageView;

        TextView usernameTextView;

        TextView dateTextView;

        TextView contentTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            bindViews();
        }

        private void bindViews() {
            userImageView = itemView.findViewById(R.id.user_image_view);
            usernameTextView = itemView.findViewById(R.id.user_name_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
        }

    }
}
