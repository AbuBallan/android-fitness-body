package com.jordan.fitnessbody.adapter.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.jordan.fitnessbody.model.Article;
import com.jordan.fitnessbody.model.Comment;
import com.jordan.fitnessbody.model.User;

import java.util.Map;

public interface FirebaseHelper {

    boolean isUserSignedIn();

    String getCurrentUserId();

    String getCurrentUserPhoneNumber();

    String getCurrentUserDisplayName();

    void setCurrentUserDisplayName(String displayName);

    Task<DocumentSnapshot> currentUser();

    void updateCurrentUser(User user);

    StorageReference getCurrentUserProfileImage();

    StorageReference getUserProfileImage(String userId);

    Task<QuerySnapshot> getGymLocations();

    CollectionReference getCategoryArticles(String categoryId);

    void addArticle(Article article);

    void usernameMapper(ValueEventListener listener);

    DocumentReference getArticle(String categoryId, String articleId);

    void dislike(String categoryId, String articleId);

    void like(String categoryId, String articleId);

    CollectionReference getArticleComments(String categoryId, String articleId);

    void addComment(Comment comment);

    void deleteArticle(String categoryId, String articleId);

    void updateArticle(String categoryId, String articleId, Map<String, Object> edits);

    CollectionReference getUsers();

    void updateUserRole(String userId, boolean admin);
}
