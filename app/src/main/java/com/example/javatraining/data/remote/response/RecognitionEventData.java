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
    public void setId(String id) { this.id = id; }
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public String getCameraId() { return cameraId; }
    public void setCameraId(String cameraId) { this.cameraId = cameraId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
}
