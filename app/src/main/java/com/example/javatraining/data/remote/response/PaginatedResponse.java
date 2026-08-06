package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PaginatedResponse<T> extends BaseResponse<List<T>> {
    @SerializedName("pagination")
    private PaginationMeta pagination;

    public PaginationMeta getPagination() {
        return pagination;
    }
}
