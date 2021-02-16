package com.garzoopvt.garzoo.Business.Services;


import androidx.annotation.Nullable;

import com.garzoopvt.garzoo.Business.Model.Business;
import com.garzoopvt.garzoo.Employement.Model.Employment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class BusinessResponse {

    @SerializedName("business")
    @Expose()
    private List<Business> business;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<Business> getBusiness(){
        return business;
    }

}
