package com.garzoopvt.garzoo.BuySell.Services;

import androidx.annotation.Nullable;

import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BuyResponse {

    @SerializedName("sell")
    @Expose()
    private List<Buy> sell;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<Buy> getBuy(){
        return sell;
    }

}
