package com.garzoopvt.garzoo.Chat.Services;



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

public interface ChatApi {




    @GET(URLs.view_users_api_2_0)
    LiveData<ApiResponse<ChatUserResponse>> getUser(
            @Query("unique_id") String unique_id,
            @Query("p") String user_id

    );


    @GET(URLs.view_group_chat_api_2_0)
    LiveData<ApiResponse<ChatGroupResponse>> getGroup(
            @Query("unique_id") String unique_id,
            @Query("p") String user_id,
            @Query("page_no") String page_no
    );

    @GET(URLs.view_chats_2_0)
    LiveData<ApiResponse<ChatIndividualResponse>> getIndividualChat(
            @Query("unique_id") String unique_id,
            @Query("p") String from_uid,
            @Query("q") String to_uid
    );

    @GET(URLs.api_pd_comment_data_2_0)
    LiveData<ApiResponse<ChatGroupListResponse>> getGroupChat(
            @Query("unique_id") String unique_id,
            @Query("pd_id") String pd_id
    );

    @GET(URLs.add_block_2_0)
    Call<ResponseBody> block(
            @Query("unique_id") String unique_id,
            @Query("user_id") String from_uid,
            @Query("blocked_id") String to_uid
    );

    @Multipart
    @POST(URLs.add_chat_2_0)
    Call<ChatIndividualResponse> addChat(
            @Part("unique_id") RequestBody unique_id,
            @Part("from_uid") RequestBody from_uid,
            @Part("to_uid") RequestBody to_uid,
            @Part("message") RequestBody message,
            @Part("file_type") RequestBody file_type,
            @Part MultipartBody.Part image[]
    );

    @FormUrlEncoded
    @POST(URLs.send_single_push)
    Call<ResponseBody> sendSinglePush(
            @Field("title") String title,
            @Field("message") String message,
            @Field("from_uid") String from_uid,
            @Field("to_uid") String to_uid,
            @Field("click_action") String click_action
    );

    @POST(URLs.reset_count_outer)
    @FormUrlEncoded
    Call<ResponseBody> ResetNotificationCount(@Field("unique_id") String unique_id,
                                              @Field("user_id") String user_id);

    @POST(URLs.chat_reset_count)
    @FormUrlEncoded
    Call<ResponseBody> ResetChatCount(@Field("unique_id") String unique_id,
                                              @Field("record_id") String record_id);
}
