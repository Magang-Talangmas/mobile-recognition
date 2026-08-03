package com.example.javatraining.data.model;

public class ActivityLog {
    private String name;
    private String initials;
    private String initialsColor; // Hex string e.g. "#F59E0B"
    private String statusText;
    private String time;
    private String statusBadgeText;
    private String statusBadgeColor; // e.g. "green" or "yellow"

    public ActivityLog(String name, String initials, String initialsColor, String statusText, String time, String statusBadgeText, String statusBadgeColor) {
        this.name = name;
        this.initials = initials;
        this.initialsColor = initialsColor;
        this.statusText = statusText;
        this.time = time;
        this.statusBadgeText = statusBadgeText;
        this.statusBadgeColor = statusBadgeColor;
    }

    public String getName() { return name; }
    public String getInitials() { return initials; }
    public String getInitialsColor() { return initialsColor; }
    public String getStatusText() { return statusText; }
    public String getTime() { return time; }
    public String getStatusBadgeText() { return statusBadgeText; }
    public String getStatusBadgeColor() { return statusBadgeColor; }
}
