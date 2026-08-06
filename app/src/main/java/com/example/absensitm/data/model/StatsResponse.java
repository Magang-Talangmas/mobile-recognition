package com.example.absensitm.data.model;

import com.google.gson.annotations.SerializedName;

public class StatsResponse extends BaseResponse {
    @SerializedName("data")
    private StatsData data;

    public StatsData getData() {
        return data;
    }

    public static class StatsData {
        @SerializedName("presentCount")
        private int presentCount;

        @SerializedName("lateCount")
        private int lateCount;

        public int getPresentCount() {
            return presentCount;
        }

        public int getLateCount() {
            return lateCount;
        }
    }
}
