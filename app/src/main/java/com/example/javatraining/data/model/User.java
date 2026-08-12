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
    public User(String id, String name, String email, String role, String employeeId, String department, String position) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.employeeId = employeeId;
        this.department = department;
        this.position = position;
    }
    
    public String generateProfilePhotoUrl(String accountName) {
        if (accountName == null || accountName.trim().isEmpty()) {
            return "https://ui-avatars.com/api/?name=U&background=random&color=fff&size=256";
        }
        String[] words = accountName.trim().split("\\s+");

        String nameForAvatar;
        if (words.length >= 2) {
            nameForAvatar = words[0] + "+" + words[1];
        } else {
            nameForAvatar = words[0];
        }

        return "https://ui-avatars.com/api/?name=" 
                + nameForAvatar 
                + "&background=random&color=fff&size=256";
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
    public String getAvatar() { 
        return generateProfilePhotoUrl(this.name != null && !this.name.isEmpty() ? this.name : this.email);
    }
}
