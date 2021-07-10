package com.chatapp.example.flamingoapp.models;

public class Story {

    private String imageUri;
    private long timeStart;
    private long timeEnd;
    private String storyId;
    private String userId;

    public Story(){ }

    public Story(String imageUri, long timeStart, long timeEnd, String storyId, String userId) {
        this.imageUri = imageUri;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.storyId = storyId;
        this.userId = userId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
