package com.garzoopvt.garzoo.Notification.Services;



import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.Util.URLs;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NotificationApi {



    // GET Notification REQUEST
    @GET(URLs.api_notification_2_0)
    Call<NotificationResponse> getNotification(
            @Query("unique_id") String unique_id,
            @Query("user_id") String user_id,
            @Query("page_no") String page_no
    );
}
