package com.garzoopvt.garzoo.BuySell.Services;

import androidx.annotation.Nullable;

import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryResponse {

    @SerializedName("category")
    @Expose()
    private List<Category> category;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<Category> getCategory(){
        return category;
    }

}
