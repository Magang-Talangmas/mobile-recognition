package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class RecognitionEventData {
    @SerializedName("id")
    private String id;

    @SerializedName("thumbnail")
    private String thumbnail;

    public String getId() { return id; }
    public String getThumbnail() { return thumbnail; }
}
