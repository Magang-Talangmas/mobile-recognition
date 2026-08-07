package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class ScheduleData {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("checkInTime")
    private String checkInTime;
    
    @SerializedName("checkOutTime")
    private String checkOutTime;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCheckInTime() { return checkInTime; }
    public String getCheckOutTime() { return checkOutTime; }
}
