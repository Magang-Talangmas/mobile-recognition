package com.example.javatraining.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class ManualAttendanceRequest {
    @SerializedName("status")
    private String status;

    @SerializedName("time")
    private String time;

    @SerializedName("location")
    private String location;

    @SerializedName("reason")
    private String reason;

    public ManualAttendanceRequest(String status, String time, String location, String reason) {
        this.status = status;
        this.time = time;
        this.location = location;
        this.reason = reason;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
