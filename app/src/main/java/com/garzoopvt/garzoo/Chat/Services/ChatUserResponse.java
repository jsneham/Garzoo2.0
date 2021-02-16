package com.garzoopvt.garzoo.Chat.Services;

import androidx.annotation.Nullable;

import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatUserResponse {

    @SerializedName("chat")
    @Expose()
    private List<ChatUser> chatuser;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<ChatUser> getChat(){
        return chatuser;
    }

    @Override
    public String toString() {
        return "ChatUserResponse{" +
                "chatuser=" + chatuser +
                ", error='" + error + '\'' +
                '}';
    }
}
