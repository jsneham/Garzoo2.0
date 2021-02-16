package com.garzoopvt.garzoo.Employement.Services;


import androidx.annotation.Nullable;
import com.garzoopvt.garzoo.Employement.Model.Employment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class EmploymentResponse {

    @SerializedName("employment")
    @Expose()
    private List<Employment> employment;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<Employment> getEmployment(){
        return employment;
    }

}
