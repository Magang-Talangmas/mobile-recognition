package com.example.javatraining.data.model;

public class NotificationItem {
    private String title;
    private String message;
    private String time;
    private boolean isWarning;

    public NotificationItem(String title, String message, String time, boolean isWarning) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.isWarning = isWarning;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public boolean isWarning() { return isWarning; }
}
