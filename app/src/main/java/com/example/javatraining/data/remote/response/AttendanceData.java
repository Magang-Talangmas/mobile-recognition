package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class AttendanceData {
    @SerializedName("id")
    private String id;

    @SerializedName(value = "externalEventId", alternate = {"external_event_id"})
    private String externalEventId;

    @SerializedName(value = "employeeId", alternate = {"employee_id"})
    private String employeeId;

    @SerializedName(value = "cameraId", alternate = {"camera_id"})
    private String cameraId;

    @SerializedName(value = "eventType", alternate = {"event_type"})
    private String eventType;

    @SerializedName("similarity")
    private Double similarity;

    @SerializedName(value = "timestamp", alternate = {"createdAt", "detected_at"})
    private String timestamp;

    @SerializedName(value = "confirmationStatus", alternate = {"confirmation_status", "status"})
    private String confirmationStatus;
    
    @SerializedName(value = "isLate", alternate = {"is_late"})
    private Boolean isLate;
    
    @SerializedName(value = "photoUrl", alternate = {"photo_url"})
    private String photoUrl;
    
    @SerializedName(value = "updatedAt", alternate = {"updated_at"})
    private String updatedAt;

    @SerializedName("employee")
    private EmployeeData employee;

    public String getId() { return id; }
    public String getExternalEventId() { return externalEventId; }
    public String getEmployeeId() { return employeeId; }
    public String getCameraId() { return cameraId; }
    public String getEventType() { return eventType; }
    public Double getSimilarity() { return similarity; }
    public String getTimestamp() { return timestamp; }
    public String getConfirmationStatus() { return confirmationStatus; }
    public Boolean getIsLate() { return isLate != null && isLate; }
    public String getPhotoUrl() { return photoUrl; }
    public String getUpdatedAt() { return updatedAt; }
    public EmployeeData getEmployee() { return employee; }
}
