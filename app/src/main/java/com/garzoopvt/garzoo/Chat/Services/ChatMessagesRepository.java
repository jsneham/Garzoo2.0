package com.garzoopvt.garzoo.Chat.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.Chat.Model.ChatGroupList;
import com.garzoopvt.garzoo.Chat.Model.ChatIndividual;
import com.garzoopvt.garzoo.Chat.Persistence.ChatGroupListDao;
import com.garzoopvt.garzoo.Chat.Persistence.ChatIGroupListDatabase;
import com.garzoopvt.garzoo.Chat.Persistence.ChatIndividualDao;
import com.garzoopvt.garzoo.Chat.Persistence.ChatIndividualDatabase;
import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
import com.garzoopvt.garzoo.RetrofitService.NetworkBoundResource;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.URLs;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ChatMessagesRepository {

    private static final String TAG = "ChatMessagesRepository";
    private static ChatMessagesRepository instance;

    private ChatIndividualDao chatIndividualDao;
    private ChatGroupListDao  chatGroupListDao;


    public static ChatMessagesRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ChatMessagesRepository(context);
        }
        return instance;
    }

    private ChatMessagesRepository(Context context) {
        chatIndividualDao = ChatIndividualDatabase.getInstance(context).getListDao();
        chatGroupListDao = ChatIGroupListDatabase.getInstance(context).getListDao();

    }


    public LiveData<Resource<List<ChatIndividual>>>getChats(final String user_id , final int pageNumber, final String tuid){
        return new NetworkBoundResource<List<ChatIndividual>, ChatIndividualResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull ChatIndividualResponse item) {


                if(item.getChat() != null){ //  list will be null if api key is expired
                    ChatIndividual[] chatDta = new ChatIndividual[item.getChat().size()];

                    int index = 0;
                    for(long rowId: chatIndividualDao.insertData((ChatIndividual[])(item.getChat().toArray(chatDta)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            chatIndividualDao.updateList(
                                    chatDta[index].getId(),
                                    chatDta[index].getUfname(),
                                    chatDta[index].getUlname(),
                                    chatDta[index].getMessage(),
                                    chatDta[index].getFrom_uid(),
                                    chatDta[index].getTo_uid(),
                                    chatDta[index].getFile_type()
                            );
                        }
                        else{
                            chatDta[index].setTimestamp((int)(System.currentTimeMillis() / 1000)); // save time in seconds
                            chatIndividualDao.insertList(chatDta[index]);

                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<ChatIndividual> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<ChatIndividual>> loadFromDb() {
                return chatIndividualDao.getLIst(user_id,tuid);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<ChatIndividualResponse>> createCall() {
                return ServiceGenerator.getChatApi().getIndividualChat(
                        URLs.unique_id,
                        user_id,
                        tuid
                );
            }

        }.getAsLiveData();
    }


    public Call<ResponseBody> block(String user_id, String to_id){

        return  ServiceGenerator.getChatApi().block(
                URLs.unique_id,
                user_id,
                to_id
        );

    }

    public Call<ChatIndividualResponse> addChat(RequestBody unique_id,RequestBody from_uid, RequestBody to_uid, RequestBody message,
                                       RequestBody file_type,MultipartBody.Part list[]){

        return  ServiceGenerator.getChatApi().addChat(
             unique_id,
                from_uid,
                to_uid,
                message,
                file_type,
                list
        );

    }

    public Call<ResponseBody> sendSinglePush(String app_name,String message,String from_uid, String to_uid,
                                                String chat_action){

        return  ServiceGenerator.getChatApi().sendSinglePush(
             app_name,message,from_uid,to_uid,chat_action
        );

    }



    public LiveData<Resource<List<ChatGroupList>>>getGroupChats(final String user_id , final int page_no , final String pd_id){
        return new NetworkBoundResource<List<ChatGroupList>, ChatGroupListResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull ChatGroupListResponse item) {


                if(item.getChat() != null){ //  list will be null if api key is expired
                    ChatGroupList[] chatDta = new ChatGroupList[item.getChat().size()];

                    int index = 0;
                    for(long rowId: chatGroupListDao.insertData((ChatGroupList[])(item.getChat().toArray(chatDta)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            chatGroupListDao.updateList(
                                    chatDta[index].getId(),
                                    chatDta[index].getFname(),
                                    chatDta[index].getLname(),
                                    chatDta[index].getComment(),
                                    chatDta[index].getUser_id(),
                                    chatDta[index].getPd_id(),
                                    chatDta[index].getFile_type()
                            );
                        }
                        else{
                            chatDta[index].setTimestamp((int)(System.currentTimeMillis() / 1000)); // save time in seconds
                            chatGroupListDao.insertList(chatDta[index]);

                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<ChatGroupList> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<ChatGroupList>> loadFromDb() {
                return chatGroupListDao.getLIst(pd_id);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<ChatGroupListResponse>> createCall() {
                return ServiceGenerator.getChatApi().getGroupChat(
                        URLs.unique_id,
                        pd_id
                );
            }

        }.getAsLiveData();
    }

    public Call<ResponseBody> resetChatCount(final String record_id ){

        return  ServiceGenerator.getChatApi().ResetChatCount(
                URLs.unique_id,
                record_id
        );

    }

}












