package com.garzoopvt.garzoo.Chat.Services;


import androidx.annotation.Nullable;


import com.garzoopvt.garzoo.Chat.Model.ChatGroupList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatGroupListResponse {

    @SerializedName("chat")
    @Expose()
    private List<ChatGroupList> chatGroupLists;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<ChatGroupList> getChat(){
        return chatGroupLists;
    }



}
