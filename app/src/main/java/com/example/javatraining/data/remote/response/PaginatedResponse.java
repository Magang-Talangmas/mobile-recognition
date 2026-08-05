package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PaginatedResponse<T> {
    @SerializedName("items")
    private List<T> items;

    @SerializedName("total")
    private int total;

    @SerializedName("page")
    private int page;

    @SerializedName("per_page")
    private int perPage;

    @SerializedName("total_pages")
    private int totalPages;

    public List<T> getItems() { return items; }
    public int getTotal() { return total; }
    public int getPage() { return page; }
    public int getPerPage() { return perPage; }
    public int getTotalPages() { return totalPages; }
}
