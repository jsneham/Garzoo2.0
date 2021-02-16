package com.garzoopvt.garzoo.Chat.Services;


import androidx.annotation.Nullable;

import com.garzoopvt.garzoo.Chat.Model.ChatGroup;
import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatGroupResponse {

    @SerializedName("chat")
    @Expose()
    private List<ChatGroup> chatgroup;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<ChatGroup> getChat(){
        return chatgroup;
    }



}
