package com.garzoopvt.garzoo.Employement.Services;


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


public interface EmploymentApi {

    // GET Employment REQUEST
    @GET(URLs.api_emp_data_2_0)
    LiveData<ApiResponse<EmploymentResponse>> getEmployment(
            @Query("unique_id") String unique_id,
            @Query("user_id") String user_id,
            @Query("page_no") String page_no,
            @Query("search_name") String search_name,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude
    );

    @Multipart
    @POST(URLs.api_emp_add_record)
    Call<ResponseBody> upload(
            @Part("user_id") RequestBody user_id,
            @Part("category_id") RequestBody  category_id,
            @Part("mobile_status") RequestBody  mobile_status,
            @Part("title") RequestBody  title,
            @Part("description") RequestBody  description,
            @Part("price") RequestBody  price,
            @Part("latitude") RequestBody  latitude,
            @Part("longitude") RequestBody  longitude,
            @Part("address") RequestBody  address,
            @Part MultipartBody.Part image[],
            @Part MultipartBody.Part video_file,
            @Part("emp_status") RequestBody  emp_status

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

    @Multipart
    @POST(URLs.api_emp_edit_record_2_0)
    Call<ResponseBody> edit(
            @Part("user_id") RequestBody user_id,
            @Part("category_id") RequestBody  category_id,
            @Part("mobile_status") RequestBody  mobile_status,
            @Part("title") RequestBody  title,
            @Part("description") RequestBody  description,
            @Part("price") RequestBody  price,
            @Part("latitude") RequestBody  latitude,
            @Part("longitude") RequestBody  longitude,
            @Part("address") RequestBody  address,
            @Part MultipartBody.Part image[],
            @Part MultipartBody.Part video_file,
            @Part("emp_status") RequestBody  emp_status,
            @Part("p") RequestBody  p

    );

    @GET(URLs.api_emp_available_2_0)
    Call<ResponseBody> available(
            @Query("unique_id") String unique_id,
            @Query("user_id") String user_id,
            @Query("p") String record_id
    );


    @GET(URLs.api_emp_unavailable_2_0)
    Call<ResponseBody> unavailable(
            @Query("unique_id") String unique_id,
            @Query("user_id") String user_id,
            @Query("p") String record_id
    );
    @POST(URLs.api_sell_delete_image)
    @FormUrlEncoded
    Call<ResponseBody> deleteImage(@Field("image_id") String image_id,
                                   @Field("image") String image);
}
