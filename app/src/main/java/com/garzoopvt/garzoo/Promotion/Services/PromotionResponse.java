package com.garzoopvt.garzoo.Promotion.Services;


import androidx.annotation.Nullable;

import com.garzoopvt.garzoo.Employement.Model.Employment;
import com.garzoopvt.garzoo.Promotion.Model.Promotion;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class PromotionResponse {

    @SerializedName("promotion")
    @Expose()
    private List<Promotion> promotion;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<Promotion> getPromotion(){
        return promotion;
    }

}
