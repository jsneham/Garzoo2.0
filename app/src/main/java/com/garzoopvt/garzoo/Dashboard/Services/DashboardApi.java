package com.garzoopvt.garzoo.Dashboard.Services;



import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.Util.URLs;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface DashboardApi {


    // GET Dashboard REQUEST
    @GET(URLs.api_dashboard_data_2_0)
    LiveData<ApiResponse<DashboardResponse>> getDashboard(
            @Query("unique_id") String unique_id,
            @Query("user_id") String user_id,
            @Query("page_no") String page_no,
            @Query("search_name") String search_name,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude
    );


    @Multipart
    @POST(URLs.api_add_listing_interest_2_0)
    Call<ResponseBody> interest(
            @Part("unique_id") RequestBody unique_id,
            @Part("user_id") RequestBody user_id,
            @Part("to_user_id") RequestBody  to_user_id,
            @Part("full_name") RequestBody  full_name,
            @Part("listing_id") RequestBody  listing_id,
            @Part("type") RequestBody  type,
            @Part("listing_title") RequestBody  listing_title

    );


    @FormUrlEncoded
    @POST(URLs.api_add_listing_interest_2_0)
    Call<ResponseBody> uploadToken(
            @Field("user_id") String user_id,
            @Field("name") String  name,
            @Field("token") String  token,
            @Field("imei") String  imei

    );

}
