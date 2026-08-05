package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class BaseResponse<T> {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private T data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
