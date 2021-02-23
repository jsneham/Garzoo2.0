package com.garzoopvt.garzoo.Profile.Services;

import androidx.annotation.Nullable;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InterestedResponse {

    @SerializedName("interest")
    @Expose()
    private List<DashboardList> dashboard;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<DashboardList> getDashboard(){
        return dashboard;
    }


}
