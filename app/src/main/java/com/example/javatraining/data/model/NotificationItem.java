package com.example.javatraining.data.model;

public class NotificationItem {
    private String title;
    private String message;
    private String time;
    private boolean isWarning;
    private String imageUrl;
    private boolean requiresConfirmation;

    public NotificationItem(String title, String message, String time, boolean isWarning, String imageUrl, boolean requiresConfirmation) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.isWarning = isWarning;
        this.imageUrl = imageUrl;
        this.requiresConfirmation = requiresConfirmation;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public boolean isWarning() { return isWarning; }
    public String getImageUrl() { return imageUrl; }
    public boolean requiresConfirmation() { return requiresConfirmation; }
}
