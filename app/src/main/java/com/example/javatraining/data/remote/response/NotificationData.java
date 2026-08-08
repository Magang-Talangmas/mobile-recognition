package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class NotificationData {
    @SerializedName("id")
    private String id;
    
    @SerializedName(value = "title", alternate = {"name"})
    private String title;
    
    @SerializedName(value = "body", alternate = {"description", "message"})
    private String body;
    
    @SerializedName(value = "isRead", alternate = {"is_read"})
    private Boolean isRead;
    
    @SerializedName(value = "createdAt", alternate = {"created_at"})
    private String createdAt;
    
    @SerializedName("type")
    private String type;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public boolean isRead() { return isRead != null && isRead; }
    public String getCreatedAt() { return createdAt; }
    public String getType() { return type; }
}
