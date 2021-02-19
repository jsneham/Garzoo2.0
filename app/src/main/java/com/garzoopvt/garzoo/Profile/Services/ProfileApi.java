package com.garzoopvt.garzoo.Profile.Services;

import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.Dashboard.Services.DashboardResponse;
import com.garzoopvt.garzoo.Login.Services.TransaleOutput;
import com.garzoopvt.garzoo.Login.data.LoginResult;
import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.Util.URLs;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProfileApi {


    @POST(URLs.add_record)
    @FormUrlEncoded
    Call<ResponseBody> contactUs(
            @Field("user_id") String user_id,
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("gender") String gender
    );

    @POST(URLs.add_feedback)
    @FormUrlEncoded
    Call<ResponseBody> feedback(
            @Field("user_id") String user_id,
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("gender") String gender
    );

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


    // GET Interested REQUEST
    @GET(URLs.api_listing_interest_2_0)
    LiveData<ApiResponse<DashboardResponse>> getInterested(
            @Query("unique_id") String unique_id,
            @Query("user_id") String user_id,
            @Query("page_no") String page_no,
            @Query("search_name") String search_name,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude
    );


    // GET Blocked REQUEST
    @GET(URLs.get_blocked_user_list_2_0)
    LiveData<ApiResponse<BlockedPeopleResponse>> getBlocked(
            @Query("unique_id") String unique_id,
            @Query("user_id") String user_id,
            @Query("page_no") String page_no
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
}
