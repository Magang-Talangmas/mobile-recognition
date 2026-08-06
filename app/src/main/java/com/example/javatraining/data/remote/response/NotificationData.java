package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class NotificationData {
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("isRead")
    private boolean isRead;
    @SerializedName("createdAt")
    private String createdAt;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }
}
