package com.example.javatraining.data.model;

public class User {
    private String id;
    private String name;
    private String username; // email
    private String role; // ADMIN, EMPLOYEE, Staff
    private String shift;
    private String avatar;
    private String password;

    // Full constructor (for AbsensioRepository mock login)
    public User(String id, String name, String username, String role, String shift, String avatar) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
        this.shift = shift;
        this.avatar = avatar;
    }

    // Simple constructor (for MockDatabase internal use)
    public User(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = username; // fallback name = username
        this.shift = "";
        this.avatar = "";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getShift() { return shift; }
    public String getAvatar() { return avatar; }
}
