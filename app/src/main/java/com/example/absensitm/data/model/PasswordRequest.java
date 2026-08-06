package com.example.absensitm.data.model;

import com.google.gson.annotations.SerializedName;

public class PasswordRequest {
    @SerializedName("oldPassword")
    private String oldPassword;

    @SerializedName("newPassword")
    private String newPassword;

    public PasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
