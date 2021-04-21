package com.garzoopvt.garzoo.Home;



import com.garzoopvt.garzoo.Util.URLs;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface HomeApi {

    @POST(URLs.total_count)
    @FormUrlEncoded
     Call<ResponseBody> getDataList(@Field("unique_id") String unique_id,
                                    @Field("user_id") String user_id);

    @POST(URLs.notification_get_count)
    @FormUrlEncoded
    Call<ResponseBody> GetNotificationCount(@Field("unique_id") String unique_id,
                                            @Field("user_id") String user_id);

    @POST(URLs.device_registration_2_0)
    @FormUrlEncoded
    Call<ResponseBody> registerDeviceToken(@Field("unique_id") String unique_id,
                                           @Field("user_id") String user_id);

}
