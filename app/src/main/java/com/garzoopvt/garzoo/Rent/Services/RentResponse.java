package com.garzoopvt.garzoo.Rent.Services;


import androidx.annotation.Nullable;
import com.garzoopvt.garzoo.Rent.Model.Rent;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;



public class RentResponse {

    @SerializedName("rent")
    @Expose()
    private List<Rent> rent;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<Rent> getRent(){
        return rent;
    }

}
