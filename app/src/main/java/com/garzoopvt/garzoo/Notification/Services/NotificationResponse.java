package com.garzoopvt.garzoo.Notification.Services;


import androidx.annotation.Nullable;

import com.garzoopvt.garzoo.Notification.Model.Notification;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationResponse {

    @SerializedName("notification")
    @Expose()
    private List<Notification> notification;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public List<Notification> getNotification(){
        return notification;
    }

    @Override
    public String toString() {
        return "NotificationResponse{" +
                "notification=" + notification +
                ", error='" + error + '\'' +
                '}';
    }
}
