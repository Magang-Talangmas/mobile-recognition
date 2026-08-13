package com.example.javatraining.data.model;

import java.util.Date;

public class AttendanceEvent {
    private long id;
    private String cameraId;
    private long trackId;
    private String employeeId;
    private String originalCandidateId;
    private EventDirection direction;
    private LogType eventType;
    private double similarity;
    private EventStatus status;
    private String employeeResponse;
    private Date detectedAt;
    private Date respondedAt;
    private String photoSnapshot; // optional for UI

    public AttendanceEvent(long id, String cameraId, long trackId, String employeeId, String originalCandidateId,
                           EventDirection direction, LogType eventType, double similarity, EventStatus status,
                           String employeeResponse, Date detectedAt, Date respondedAt, String photoSnapshot) {
        this.id = id;
        this.cameraId = cameraId;
        this.trackId = trackId;
        this.employeeId = employeeId;
        this.originalCandidateId = originalCandidateId;
        this.direction = direction;
        this.eventType = eventType;
        this.similarity = similarity;
        this.status = status;
        this.employeeResponse = employeeResponse;
        this.detectedAt = detectedAt;
        this.respondedAt = respondedAt;
        this.photoSnapshot = photoSnapshot;
    }

    public long getId() { return id; }
    public String getCameraId() { return cameraId; }
    public long getTrackId() { return trackId; }
    public String getEmployeeId() { return employeeId; }
    public String getOriginalCandidateId() { return originalCandidateId; }
    public EventDirection getDirection() { return direction; }
    public LogType getEventType() { return eventType; }
    public double getSimilarity() { return similarity; }
    public EventStatus getStatus() { return status; }
    public String getEmployeeResponse() { return employeeResponse; }
    public Date getDetectedAt() { return detectedAt; }
    public Date getRespondedAt() { return respondedAt; }
    public String getPhotoSnapshot() { return photoSnapshot; }

    public void setStatus(EventStatus status) { this.status = status; }
    public void setEmployeeResponse(String employeeResponse) { this.employeeResponse = employeeResponse; }
    public void setRespondedAt(Date respondedAt) { this.respondedAt = respondedAt; }
    public void setEventType(LogType eventType) { this.eventType = eventType; }

    private boolean isLate;
    private String confirmationStatus;
    private String recognitionId;
    private String thumbnailUrl;

    public boolean isLate() { return isLate; }
    public void setLate(boolean late) { isLate = late; }
    
    public String getConfirmationStatus() { return confirmationStatus; }
    public void setConfirmationStatus(String confirmationStatus) { this.confirmationStatus = confirmationStatus; }
    
    public String getRecognitionId() { return recognitionId; }
    public void setRecognitionId(String recognitionId) { this.recognitionId = recognitionId; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}
