package com.garzoopvt.garzoo.Notification.ViewModel;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.garzoopvt.garzoo.Notification.Model.Notification;
import com.garzoopvt.garzoo.Notification.Services.NotificationRepository;

import java.util.List;

public class NotificationViewModel extends ViewModel {
    private static final String TAG = "NotificationViewModel";

    private NotificationRepository notificationRepository;
    private boolean mIsPerformingQuery;

    public NotificationViewModel() {
        notificationRepository= NotificationRepository.getInstance();
        mIsPerformingQuery = false;

    }

    public LiveData<List<Notification>> getNotification(){
        return notificationRepository.getNotificaion();
    }

    public void getNotificationApi(String user_id , int page_no){
        mIsPerformingQuery = true;
        notificationRepository.getNotification(user_id, page_no);
    }


    public void setIsPerformingQuery(Boolean isPerformingQuery){
        mIsPerformingQuery = isPerformingQuery;
    }

    public boolean isPerformingQuery(){
        return mIsPerformingQuery;
    }


    public boolean onBackPressed(){
        if(mIsPerformingQuery){
            // cancel the query
            notificationRepository.cancelRequest();
            mIsPerformingQuery = false;
        }
        return true;
    }
}