package com.garzoopvt.garzoo.Dashboard.Services;

import androidx.annotation.Nullable;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Notification.Model.Notification;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashboardResponse {

    @SerializedName("dashboard")
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
