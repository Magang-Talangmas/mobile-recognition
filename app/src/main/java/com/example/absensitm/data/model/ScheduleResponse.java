package com.example.absensitm.data.model;

import com.google.gson.annotations.SerializedName;

public class ScheduleResponse extends BaseResponse {
    @SerializedName("data")
    private ScheduleData data;

    public ScheduleData getData() {
        return data;
    }

    public static class ScheduleData {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("checkInTime")
        private String checkInTime;

        @SerializedName("checkOutTime")
        private String checkOutTime;

        @SerializedName("toleranceMinutes")
        private int toleranceMinutes;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getCheckInTime() { return checkInTime; }
        public String getCheckOutTime() { return checkOutTime; }
        public int getToleranceMinutes() { return toleranceMinutes; }
    }
}
