package com.example.javatraining.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class LeaveRequest {
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

    public LeaveRequest(String employeeId, String date, String type, String reason, String photoUrl, String status) {
        this.employeeId = employeeId;
        this.date = date;
        this.type = type;
        this.reason = reason;
        this.photoUrl = photoUrl;
        this.status = status;
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
