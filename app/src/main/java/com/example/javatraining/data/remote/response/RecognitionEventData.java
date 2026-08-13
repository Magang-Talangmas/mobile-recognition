package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class RecognitionEventData {
    @SerializedName("id")
    private String id;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("cameraId")
    private String cameraId;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("status")
    private String status;

    @SerializedName("employeeId")
    private String employeeId;

    public String getId() { return id; }
    public String getThumbnail() { return thumbnail; }
    public String getCameraId() { return cameraId; }
    public String getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
    public String getEmployeeId() { return employeeId; }
}
