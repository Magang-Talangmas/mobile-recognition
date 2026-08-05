package com.example.javatraining.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String username; // email
    
    @SerializedName("role")
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
    public String getShift() { return shift != null ? shift : ""; }
    public String getAvatar() { return avatar != null ? avatar : ""; }
}
