package com.jordan.fitnessbody.adapter.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jordan.fitnessbody.model.Article;
import com.jordan.fitnessbody.model.Comment;
import com.jordan.fitnessbody.model.User;

import java.util.Map;

public class AppFirebaseHelper implements FirebaseHelper {

    private static final String TAG = "AppFirebaseHelper";

    private final FirebaseAuth mAuth;

    private final FirebaseFirestore mFirestore;

    private final FirebaseStorage mStorage;

    private final FirebaseDatabase mDatabase;

    private String cacheUsername;

    public AppFirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    @Override
    public String getCurrentUserId(){
        return mAuth.getCurrentUser().getUid();
    }

    @Override
    public String getCurrentUserPhoneNumber(){
        return mAuth.getCurrentUser().getPhoneNumber();
    }

    @Override
    public String getCurrentUserDisplayName(){
        if (cacheUsername == null)
            return mAuth.getCurrentUser().getDisplayName();
        else
            return cacheUsername;
    }

    @Override
    public void setCurrentUserDisplayName(String displayName) {
        mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(displayName).build());
    }

    @Override
    public Task<DocumentSnapshot> currentUser(){
        return mFirestore.collection("users").document(getCurrentUserId()).get();
    }

    @Override
    public void updateCurrentUser(User user) {

        cacheUsername = user.getName();

        setCurrentUserDisplayName(user.getName());

        mFirestore.collection("users").document(getCurrentUserId()).set(
                user
        );

        mDatabase.getReference().child("username_mapper").child(getCurrentUserId()).setValue(user.getName());

    }

    @Override
    public StorageReference getCurrentUserProfileImage() {
        return mStorage.getReference().child("users").child(getCurrentUserId());
    }

    @Override
    public StorageReference getUserProfileImage(String userId) {
        return mStorage.getReference().child("users").child(userId);
    }

    @Override
    public Task<QuerySnapshot> getGymLocations() {
        return mFirestore.collection("gym_locator").get();
    }

    @Override
    public CollectionReference getCategoryArticles(String categoryId){
        return mFirestore.collection("category").document(categoryId).collection("articles");
    }

    @Override
    public void addArticle(Article article) {
        getCategoryArticles(article.getCategoryId()).add(article);
    }

    @Override
    public void usernameMapper(ValueEventListener listener){
        mDatabase.getReference().child("username_mapper").addListenerForSingleValueEvent(listener);
    }

    @Override
    public DocumentReference getArticle(String categoryId, String articleId){
        return getCategoryArticles(categoryId).document(articleId);
    }

    @Override
    public void dislike(String categoryId, String articleId) {
        getArticle(categoryId, articleId).update("likes", FieldValue.arrayRemove(getCurrentUserId()));
    }

    @Override
    public void like(String categoryId, String articleId) {
        getArticle(categoryId, articleId).update("likes", FieldValue.arrayUnion(getCurrentUserId()));
    }

    @Override
    public CollectionReference getArticleComments(String categoryId, String articleId) {
        return getArticle(categoryId, articleId).collection("comments");
    }

    @Override
    public void addComment(Comment comment) {
        getArticleComments(comment.getCategoryId(), comment.getArticleId()).add(comment);
    }

    @Override
    public void deleteArticle(String categoryId, String articleId) {
        getArticle(categoryId, articleId).delete();
    }

    @Override
    public void updateArticle(String categoryId, String articleId, Map<String, Object> edits) {
        getArticle(categoryId, articleId).update(edits);
    }

    @Override
    public CollectionReference getUsers() {
        return mFirestore.collection("users");
    }

    @Override
    public void updateUserRole(String userId, boolean admin) {
        mFirestore.collection("users").document(userId).update("admin", admin);
    }


}
