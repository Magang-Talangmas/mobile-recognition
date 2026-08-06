package com.example.javatraining.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("employeeId")
    private String employeeId;
    
    @SerializedName("department")
    private String department;
    
    @SerializedName("position")
    private String position;
    
    @SerializedName("role")
    private String role; // ADMIN, EMPLOYEE, Staff
    
    private String shift;
    private String avatar;
    private String password;

    // Full constructor (for AbsensiTMRepository mock login)
    public User(String id, String name, String email, String role, String shift, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.shift = shift;
        this.avatar = avatar;
    }

    // Simple constructor (for MockDatabase internal use)
    public User(String id, String email, String password, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = email; // fallback name
        this.shift = "";
        this.avatar = "";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return email; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmployeeId() { return employeeId; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public String getShift() { return shift != null ? shift : ""; }
    public String getAvatar() { return avatar != null ? avatar : ""; }
}
