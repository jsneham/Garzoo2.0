package com.garzoopvt.garzoo.Dashboard.Services;



import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.Business.Services.BusinessResponse;
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
            @Query("longitude") String longitude,
            @Query("type") String type,
            @Query("district") String district,
            @Query("taluka") String taluka,
            @Query("city") String city,
            @Query("listing_status") String listing_status
    );

    @GET(URLs.api_advance_search_2_0)
    LiveData<ApiResponse<DashboardResponse>>  getAdvanceDataList(@Field("unique_id") String unique_id,
                                           @Field("user_id") String user_id,
                                           @Field("page_no") String page_no,
                                           @Field("keyword") String keyword,
                                           @Field("type") String type ,
                                           @Field("district") String district,
                                           @Field("taluka") String taluka,
                                           @Field("city") String city,
                                           @Field("latitude") String latitude,
                                           @Field("longitude") String longitude,
                                           @Field("listing_status") String listing_status);


    @Multipart
    @POST(URLs.api_add_listing_interest_2_0)
    Call<ResponseBody> interest(
            @Part("unique_id") RequestBody unique_id,
            @Part("user_id") RequestBody user_id,
            @Part("to_user_id") RequestBody  to_user_id,
            @Part("full_name") RequestBody  full_name,
            @Part("listing_id") RequestBody  listing_id,
            @Part("type") RequestBody  type,
            @Part("listing_title") RequestBody  listing_title,
            @Part("listing_type") RequestBody  listing_type,
            @Part("language") RequestBody  language

    );

    @Multipart
    @POST(URLs.api_add_listing_interest_2_0)
    LiveData<ApiResponse<DashboardResponse>> interest1(
            @Part("unique_id") RequestBody unique_id,
            @Part("user_id") RequestBody user_id,
            @Part("to_user_id") RequestBody  to_user_id,
            @Part("full_name") RequestBody  full_name,
            @Part("listing_id") RequestBody  listing_id,
            @Part("type") RequestBody  type,
            @Part("listing_title") RequestBody  listing_title,
            @Part("listing_type") RequestBody  listing_type

    );


    @FormUrlEncoded
    @POST(URLs.device_registration_2_0)
    Call<ResponseBody> uploadToken(
            @Field("user_id") String user_id,
            @Field("name") String  name,
            @Field("token") String  token,
            @Field("imei") String  imei,
            @Field("language") String  language

    );

    @GET(URLs.add_block_2_0)
    Call<ResponseBody> block(
            @Query("unique_id") String unique_id,
            @Query("user_id") String from_uid,
            @Query("blocked_id") String to_uid
    );


    @GET(URLs.dashboard_delete_record_2_0)
    LiveData<ApiResponse<DashboardResponse>> Dashboard_delete_record_2_0(
            @Query("unique_id") String unique_id,
            @Query("p") String post_id,
            @Query("type") String type,
            @Query("master_id") String master_id

    );


    @GET(URLs.Business_delete_record_2_0)
    Call<ResponseBody> Business_delete_record_2_0(
            @Query("unique_id") String unique_id,
            @Query("p") String post_id);

    @GET(URLs.Employment_delete_record_2_0)
    Call<ResponseBody> Employment_delete_record_2_0(
            @Query("unique_id") String unique_id,
            @Query("p") String post_id);

    @GET(URLs.Listing_rent_delete_record_2_0)
    Call<ResponseBody> Listing_rent_delete_record_2_0(
            @Query("unique_id") String unique_id,
            @Query("p") String post_id);

    @GET(URLs.Listing_sell_delete_record_2_0)
    Call<ResponseBody> Listing_sell_delete_record_2_0(
            @Query("unique_id") String unique_id,
            @Query("p") String post_id);

    @GET(URLs.Pd_delete_record_2_0)
    Call<ResponseBody> Pd_delete_record_2_0(
            @Query("unique_id") String unique_id,
            @Query("p") String post_id);


    @GET(URLs.report_post)
    Call<ResponseBody> ReportPost(
            @Query("unique_id") String unique_id,
            @Query("user_id") String user_id,
            @Query("listing_id") String listing_id,
            @Query("employment_id") String employment_id,
            @Query("business_id") String business_id,
            @Query("report") String report
    );


    @GET(URLs.log_activity)
    Call<ResponseBody> LogActivity(
            @Query("unique_id") String unique_id,
            @Query("post_id") String post_id,
            @Query("post_user_id") String post_user_id,
            @Query("user_id") String user_id,
            @Query("type") String type
    );

    @POST(URLs.api_sell_delete_image)
    @FormUrlEncoded
    Call<ResponseBody> deleteImage(@Field("image_id") String image_id,
                                   @Field("image") String image);

}
