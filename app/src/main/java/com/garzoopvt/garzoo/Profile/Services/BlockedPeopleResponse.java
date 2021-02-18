package com.garzoopvt.garzoo.Profile.Services;

import androidx.annotation.Nullable;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Profile.Model.BlockedPeople;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BlockedPeopleResponse {

    @SerializedName("blockedpeople")
    @Expose()
    private List<BlockedPeople> blocked;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<BlockedPeople> getBlocked(){
        return blocked;
    }


}
