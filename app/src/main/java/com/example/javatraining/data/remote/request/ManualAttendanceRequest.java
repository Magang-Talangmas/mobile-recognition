package com.example.javatraining.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class ManualAttendanceRequest {
    @SerializedName("camera_id")
    public String cameraId;
    
    @SerializedName("track_id")
    public long trackId;
    
    @SerializedName("employee_id")
    public String employeeId;
    
    @SerializedName("direction")
    public String direction;
    
    @SerializedName("event_type")
    public String eventType;
    
    @SerializedName("similarity")
    public double similarity;
    
    @SerializedName("status")
    public String status;
    
    @SerializedName("detected_at")
    public String detectedAt;

    @SerializedName("photoUrl")
    public String photoUrl;

    public ManualAttendanceRequest(String employeeId, String direction, String eventType, String status, String detectedAt, String photoUrl) {
        this.cameraId = "MANUAL";
        this.trackId = 0;
        this.employeeId = employeeId;
        this.direction = direction;
        this.eventType = eventType;
        this.similarity = 1.0;
        this.status = status;
        this.detectedAt = detectedAt;
        this.photoUrl = photoUrl;
    }

    public ManualAttendanceRequest(String employeeId, String direction, String eventType, String status, String detectedAt) {
        this(employeeId, direction, eventType, status, detectedAt, null);
    }
}
