package com.chatapp.example.flamingoapp.models;

public class Notification {

    public String getUserId() {
        return userId;
    }

    public Notification(){}

    public Notification(String userId, String comment, String postId, boolean isPost) {
        this.userId = userId;
        this.comment = comment;
        this.postId = postId;
        this.isPost = isPost;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    private String userId;
    private String comment;
    private String postId;
    private boolean isPost;
}
