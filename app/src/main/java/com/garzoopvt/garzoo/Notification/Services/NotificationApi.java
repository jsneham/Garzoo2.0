package com.garzoopvt.garzoo.Notification.Services;



import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.Chat.Services.ChatUserResponse;
import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.Util.URLs;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotificationApi {



    // GET Notification REQUEST
    @GET(URLs.api_notification_2_0)
    LiveData<ApiResponse<NotificationResponse>> getNotification(
            @Query("unique_id") String unique_id,
            @Query("user_id") String user_id,
            @Query("language") String language
    );

    @POST(URLs.notification_reset_count)
    @FormUrlEncoded
    Call<ResponseBody> ResetNotificationCount(@Field("unique_id") String unique_id,
                                              @Field("user_id") String user_id);
}
