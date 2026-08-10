package com.example.javatraining.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class LeaveRequest {
    @SerializedName("id")
    private String id;

    @SerializedName("employeeId")
    private String employeeId;

    @SerializedName("date")
    private String date;

    @SerializedName("type")
    private String type;

    @SerializedName("reason")
    private String reason;

    @SerializedName("photoUrl")
    private String photoUrl;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public LeaveRequest(String employeeId, String date, String type, String reason, String photoUrl, String status) {
        this.id = java.util.UUID.randomUUID().toString();
        this.employeeId = employeeId;
        this.date = date;
        this.type = type;
        this.reason = reason;
        this.photoUrl = photoUrl;
        this.status = status;
        
        java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
        isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String now = isoFormat.format(java.util.Calendar.getInstance().getTime());
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getStatus() {
        return status;
    }
}
