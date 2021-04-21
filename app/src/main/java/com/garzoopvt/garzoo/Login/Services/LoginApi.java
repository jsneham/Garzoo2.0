package com.garzoopvt.garzoo.Login.Services;

import com.garzoopvt.garzoo.Login.data.LoginResult;
import com.garzoopvt.garzoo.Util.URLs;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface LoginApi {

    @POST(URLs.translate)
    @FormUrlEncoded
    Call<TransaleOutput> translate(
            @Field("text") String text,
            @Field("language_code") String language_code
    );

    @POST(URLs.user_mobile_check)
    @FormUrlEncoded
    Call<LoginResult> login(
            @Field("unique_id") String unique_id,
            @Field("mobile") String mobile,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );

    @POST(URLs.user_register)
    @FormUrlEncoded
    Call<ResponseBody> getRegister(
            @Field("user_id") String user_id,
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("gender") String gender,
            @Field("age") String age,
            @Field("state") String state,
            @Field("city_area_gav") String city_area_gav,
            @Field("taluka") String taluka,
            @Field("district") String district,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude);

    @POST(URLs.user_mobile_change)
    @FormUrlEncoded
    Call<ResponseBody> user_mobile_change(@Field("mobile") String mobile,
                                          @Field("latitude") String latitude,
                                          @Field("longitude") String longitude,
                                          @Field("user_id") String user_id);

    @POST(URLs.user_mobile_update)
    @FormUrlEncoded
    Call<ResponseBody> user_mobile_update(@Field("mobile") String mobile,
                                          @Field("user_id") String user_id);
}
