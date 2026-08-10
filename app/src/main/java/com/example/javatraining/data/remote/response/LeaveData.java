package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class LeaveData {
    @SerializedName("id")
    private String id;

    @SerializedName(value = "employeeId", alternate = {"employee_id"})
    private String employeeId;

    @SerializedName("date")
    private String date;

    @SerializedName("type")
    private String type;

    @SerializedName("reason")
    private String reason;

    @SerializedName(value = "photoUrl", alternate = {"photo_url"})
    private String photoUrl;

    @SerializedName("status")
    private String status;

    @SerializedName(value = "createdAt", alternate = {"created_at"})
    private String createdAt;

    public String getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getReason() { return reason; }
    public String getPhotoUrl() { return photoUrl; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}
