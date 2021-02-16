package com.garzoopvt.garzoo.Notification.Services;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.garzoopvt.garzoo.Notification.Model.Notification;


import java.util.List;

public class NotificationRepository {

    private static final String TAG = "NotificationRepository";

    private static NotificationRepository instance;
    private  NotificationApiClient mNotificationApiClient;


    public static NotificationRepository getInstance(){
        if(instance == null){
            instance = new NotificationRepository();
        }
        return instance;
    }

    private NotificationRepository() {
        mNotificationApiClient= NotificationApiClient.getInstance();

    }

    public LiveData<List<Notification>> getNotificaion(){
        return mNotificationApiClient.getNotification();
    }


    public void getNotification(String user_id , int page_no){

        if(page_no==0){
            page_no=1;
        }
        mNotificationApiClient.getNotification(user_id,page_no);
    }


    public void cancelRequest(){
            mNotificationApiClient.cancelRequest();
    }
}












