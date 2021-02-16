package com.garzoopvt.garzoo.Chat.Services;


import androidx.annotation.Nullable;


import com.garzoopvt.garzoo.Chat.Model.ChatIndividual;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatIndividualResponse {

    @SerializedName("chat")
    @Expose()
    private List<ChatIndividual> chatIndividuals;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<ChatIndividual> getChat(){
        return chatIndividuals;
    }



}
