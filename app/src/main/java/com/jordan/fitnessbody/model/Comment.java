package com.jordan.fitnessbody.model;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Comment {

    private String id;

    private String categoryId;

    private String articleId;

    private String userId;

    private String content;

    private Date timestamp;

    public Comment() {
    }

    public Comment(String categoryId, String articleId, String userId, String content, Date timestamp) {
        this.categoryId = categoryId;
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public String getCategoryId() {
        return categoryId;
    }

    @Exclude
    public String getArticleId() {
        return articleId;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
}
