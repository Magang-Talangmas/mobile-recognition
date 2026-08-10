package com.example.javatraining.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class ManualAttendanceRequest {
    @SerializedName("id")
    public String id;

    @SerializedName("cameraId")
    public String cameraId;
    
    @SerializedName("employeeId")
    public String employeeId;
    
    @SerializedName("eventType")
    public String eventType;
    
    @SerializedName("similarity")
    public double similarity;
    
    @SerializedName("status")
    public String status;
    
    @SerializedName("confirmationStatus")
    public String confirmationStatus;
    
    @SerializedName("timestamp")
    public String timestamp;

    @SerializedName("photoUrl")
    public String photoUrl;

    @SerializedName("createdAt")
    public String createdAt;

    @SerializedName("updatedAt")
    public String updatedAt;

    public ManualAttendanceRequest(String employeeId, String direction, String eventType, String status, String detectedAt, String photoUrl) {
        this.id = java.util.UUID.randomUUID().toString();
        this.cameraId = "MANUAL";
        this.employeeId = employeeId;
        this.eventType = eventType;
        this.similarity = 1.0;
        this.status = status;
        this.confirmationStatus = "CONFIRMED";
        this.timestamp = detectedAt;
        this.createdAt = detectedAt;
        this.updatedAt = detectedAt;
        this.photoUrl = photoUrl;
    }

    public ManualAttendanceRequest(String employeeId, String direction, String eventType, String status, String detectedAt) {
        this(employeeId, direction, eventType, status, detectedAt, null);
    }
}
