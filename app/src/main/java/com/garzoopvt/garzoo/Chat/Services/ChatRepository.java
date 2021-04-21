package com.garzoopvt.garzoo.Chat.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;


import com.garzoopvt.garzoo.Chat.Model.ChatGroup;
import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.garzoopvt.garzoo.Chat.Persistence.ChatGroupDao;
import com.garzoopvt.garzoo.Chat.Persistence.ChatGroupDatabase;
import com.garzoopvt.garzoo.Chat.Persistence.ChatUserDao;
import com.garzoopvt.garzoo.Chat.Persistence.ChatUserDatabase;
import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
import com.garzoopvt.garzoo.RetrofitService.NetworkBoundResource;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.URLs;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ChatRepository {

    private static final String TAG = "ChatRepository";
    private static ChatRepository instance;

    private ChatUserDao chatUserDao;
    private ChatGroupDao chatGroupyDao;

    public static ChatRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ChatRepository(context);
        }
        return instance;
    }

    private ChatRepository(Context context) {
        chatUserDao = ChatUserDatabase.getInstance(context).getListDao();
        chatGroupyDao = ChatGroupDatabase.getInstance(context).getListDao();
    }


    public LiveData<Resource<List<ChatUser>>> getChatUserList(final String user_id , final int pageNumber, final String search_name){
        return new NetworkBoundResource<List<ChatUser>, ChatUserResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull ChatUserResponse item) {
                ArrayList<String> ids=new ArrayList<>();
                if(item.getChat() != null){ //  list will be null if api key is expired
                    ChatUser[] list = new ChatUser[item.getChat().size()];
                    chatUserDao.delete();
                    int index = 0;
                    for(long rowId: chatUserDao.insertData((ChatUser[])(item.getChat().toArray(list)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            chatUserDao.updateList(
                                    list[index].getId(),
                                    list[index].getFname(),
                                    list[index].getLname(),
                                    list[index].getLast_message(),
                                    list[index].getChat_count(),
                                    list[index].getPhone_no(),
                                    list[index].getTo_uid(),
                                    list[index].getFrom_uid(),
                                    list[index].getDt(),
                                    list[index].getCount_id()

                            );
                        }
                        ids.add(list[index].getId());
                        index++;
                    }

                    if(ids.size()>0) chatUserDao.deleteOldData(ids);
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<ChatUser> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<ChatUser>> loadFromDb() {
                return chatUserDao.getList();
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<ChatUserResponse>> createCall() {
                return ServiceGenerator.getChatApi().getUser(
                        URLs.unique_id,
                        user_id

                );
            }

        }.getAsLiveData();
    }


    public LiveData<Resource<List<ChatGroup>>> getChatGroupList(final String user_id , final int pageNumber, final String search_name){
        return new NetworkBoundResource<List<ChatGroup>, ChatGroupResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull ChatGroupResponse item) {
                ArrayList<String> ids=new ArrayList<>();
                if(item.getChat() != null){ //  list will be null if api key is expired
                    ChatGroup[] list = new ChatGroup[item.getChat().size()];
                    chatGroupyDao.delete();
                    int index = 0;
                    for(long rowId: chatGroupyDao.insertData((ChatGroup[])(item.getChat().toArray(list)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            chatGroupyDao.updateList(
                                    list[index].getId(),
                                    list[index].getTitle(),
                                    list[index].getLast_message()

                            );
                        }
                        ids.add(list[index].getId());
                        index++;
                    }

                    if(ids.size()>0) chatGroupyDao.deleteOldData(ids);
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<ChatGroup> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<ChatGroup>> loadFromDb() {
                return chatGroupyDao.getList();
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<ChatGroupResponse>> createCall() {
                return ServiceGenerator.getChatApi().getGroup(
                        URLs.unique_id,
                        user_id,
                        String.valueOf(pageNumber)
                );
            }

        }.getAsLiveData();
    }


    public Call<ResponseBody> resetNotificationCount(final String user_id ){

        return  ServiceGenerator.getChatApi().ResetNotificationCount(
                URLs.unique_id,
                user_id
        );

    }


}












