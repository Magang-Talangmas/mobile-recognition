package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class AttendanceData {
    @SerializedName("id")
    private String id;

    @SerializedName("externalEventId")
    private String externalEventId;

    @SerializedName("employeeId")
    private String employeeId;

    @SerializedName("cameraId")
    private String cameraId;

    @SerializedName("eventType")
    private String eventType;

    @SerializedName("status")
    private String status;

    @SerializedName("similarity")
    private Double similarity;

    @SerializedName(value = "timestamp", alternate = {"createdAt", "detected_at"})
    private String timestamp;

    @SerializedName("confirmationStatus")
    private String confirmationStatus;
    
    @SerializedName("employee")
    private EmployeeData employee;

    public String getId() { return id; }
    public String getExternalEventId() { return externalEventId; }
    public String getEmployeeId() { return employeeId; }
    public String getCameraId() { return cameraId; }
    public String getEventType() { return eventType; }
    public String getStatus() { return status; }
    public Double getSimilarity() { return similarity; }
    public String getTimestamp() { return timestamp; }
    public String getConfirmationStatus() { return confirmationStatus; }
    public EmployeeData getEmployee() { return employee; }
}
