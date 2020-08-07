package com.jordan.fitnessbody.view.users.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jordan.fitnessbody.R;
import com.jordan.fitnessbody.adapter.firebase.FirebaseHelper;
import com.jordan.fitnessbody.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private LayoutInflater mLayoutInflater;

    private final Context mContext;

    private final OnUserSelected mOnUserSelected;

    private final FirebaseHelper mFirebaseHelper;

    private List<User> mUsers;

    public UserAdapter(Context context, OnUserSelected onUserSelected, FirebaseHelper firebaseHelper) {
        mContext = context;
        mOnUserSelected = onUserSelected;
        mFirebaseHelper = firebaseHelper;
    }

    public void setItems(List<User> users) {
        mUsers = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null){
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        return new UserViewHolder(mLayoutInflater.inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        final User user = mUsers.get(position);

        Glide
                .with(mContext)
                .load(mFirebaseHelper.getUserProfileImage(user.getId()))
                .placeholder(R.drawable.default_user)
                .circleCrop()
                .into(holder.mUserImageView);

        holder.mUserNameTextView.setText(user.getName());

        if (user.isAdmin()){
            holder.mRoleTextView.setText("Admin");
        }else{
            holder.mRoleTextView.setText("User");
        }

        holder.mUserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnUserSelected.onSelect(position, user);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (mUsers == null)? 0 : mUsers.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        ImageView mUserImageView;
        TextView mUserNameTextView;
        TextView mRoleTextView;
        View mUserView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            bindViews();
        }

        private void bindViews() {
            mUserView = itemView.findViewById(R.id.user_layout);
            mUserImageView = itemView.findViewById(R.id.user_image_view);
            mUserNameTextView = itemView.findViewById(R.id.user_name_text_view);
            mRoleTextView = itemView.findViewById(R.id.role_text_view);
        }

    }

    public interface OnUserSelected {
        void onSelect(int position, User user);
    }
}
