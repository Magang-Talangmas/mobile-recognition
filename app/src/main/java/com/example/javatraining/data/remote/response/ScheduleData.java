package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class ScheduleData {
    @SerializedName("id")
    private String id;
    
    @SerializedName("shiftName")
    private String shiftName;
    
    @SerializedName("startTime")
    private String startTime;
    
    @SerializedName("endTime")
    private String endTime;

    public String getId() { return id; }
    public String getShiftName() { return shiftName; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}
