package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EmployeeData {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("position")
    private String position;

    @SerializedName("department")
    private String department;

    @SerializedName("status")
    private String status;

    @SerializedName("faceRegistered")
    private boolean faceRegistered;

    @SerializedName("joinedAt")
    private String joinedAt;

    @SerializedName("photos")
    private List<String> photos;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPosition() { return position; }
    public String getDepartment() { return department; }
    public String getStatus() { return status; }
    public boolean isFaceRegistered() { return faceRegistered; }
    public String getJoinedAt() { return joinedAt; }
    public List<String> getPhotos() { return photos; }
}
