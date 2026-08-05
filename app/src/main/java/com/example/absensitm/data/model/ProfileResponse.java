package com.example.absensitm.data.model;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse extends BaseResponse {
    @SerializedName("data")
    private EmployeeData data;

    public EmployeeData getData() { return data; }

    public static class EmployeeData {
        @SerializedName("id")
        private String id;
        @SerializedName("employeeId")
        private String employeeId;
        @SerializedName("name")
        private String name;
        @SerializedName("email")
        private String email;
        @SerializedName("department")
        private String department;
        @SerializedName("position")
        private String position;
        @SerializedName("faceRegistered")
        private boolean faceRegistered;

        public String getId() { return id; }
        public String getEmployeeId() { return employeeId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public String getPosition() { return position; }
        public boolean isFaceRegistered() { return faceRegistered; }
    }
}
