package com.example.javatraining.data.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String role;
    private String shift;
    private String faceImageUrl;

    public User(String id, String name, String email, String role, String shift, String faceImageUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.shift = shift;
        this.faceImageUrl = faceImageUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getShift() { return shift; }
    public String getFaceImageUrl() { return faceImageUrl; }
}
