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

    @SerializedName("toleranceMinutes")
    private Integer toleranceMinutes;

    @SerializedName("workDays")
    private java.util.List<String> workDays;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCheckInTime() { return checkInTime; }
    public String getCheckOutTime() { return checkOutTime; }
    public Integer getToleranceMinutes() { return toleranceMinutes; }
    public java.util.List<String> getWorkDays() { return workDays; }
}
