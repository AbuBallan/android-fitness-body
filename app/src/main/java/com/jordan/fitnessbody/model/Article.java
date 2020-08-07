package com.jordan.fitnessbody.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.jordan.fitnessbody.utils.AppConstants;

import java.util.Date;
import java.util.List;

public class Article implements Parcelable {

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    private String id;

    private String categoryId;

    private String userId;

    private String content;

    private List<String> likes;

    private AppConstants.ArticleState state;

    private Date timestamp;


    public Article() {
    }

    public Article(String categoryId, String userId, String content, List<String> likes, AppConstants.ArticleState state, Date timestamp) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.content = content;
        this.likes = likes;
        this.state = state;
        this.timestamp = timestamp;
    }

    protected Article(Parcel in) {
        this.id = in.readString();
        this.categoryId = in.readString();
        this.userId = in.readString();
        this.content = in.readString();
        this.likes = in.createStringArrayList();
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : AppConstants.ArticleState.values()[tmpState];
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public String getCategoryId() {
        return categoryId;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public List<String> getLikes() {
        return likes;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public AppConstants.ArticleState getState() {
        return state;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.categoryId);
        dest.writeString(this.userId);
        dest.writeString(this.content);
        dest.writeStringList(this.likes);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeLong(this.timestamp != null ? this.timestamp.getTime() : -1);
    }
}
