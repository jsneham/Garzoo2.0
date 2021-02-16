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

import java.util.List;

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

                if(item.getChat() != null){ //  list will be null if api key is expired
                    ChatUser[] recipes = new ChatUser[item.getChat().size()];

                    int index = 0;
                    for(long rowId: chatUserDao.insertData((ChatUser[])(item.getChat().toArray(recipes)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            chatUserDao.updateList(
                                    recipes[index].getId(),
                                    recipes[index].getFname(),
                                    recipes[index].getLname(),
                                    recipes[index].getLast_message(),
                                    recipes[index].getChat_count(),
                                    recipes[index].getPhone_no()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<ChatUser> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<ChatUser>> loadFromDb() {
                return chatUserDao.searchList(search_name, pageNumber);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<ChatUserResponse>> createCall() {
                return ServiceGenerator.getChatApi().getUser(
                        URLs.unique_id,
                        user_id,
                        String.valueOf(pageNumber)
                );
            }

        }.getAsLiveData();
    }


    public LiveData<Resource<List<ChatGroup>>> getChatGroupList(final String user_id , final int pageNumber, final String search_name){
        return new NetworkBoundResource<List<ChatGroup>, ChatGroupResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull ChatGroupResponse item) {

                if(item.getChat() != null){ //  list will be null if api key is expired
                    ChatGroup[] recipes = new ChatGroup[item.getChat().size()];

                    int index = 0;
                    for(long rowId: chatGroupyDao.insertData((ChatGroup[])(item.getChat().toArray(recipes)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            chatGroupyDao.updateList(
                                    recipes[index].getId(),
                                    recipes[index].getFname(),
                                    recipes[index].getLname(),
                                    recipes[index].getLast_message(),
                                    recipes[index].getChat_count()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<ChatGroup> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<ChatGroup>> loadFromDb() {
                return chatGroupyDao.searchList(search_name, pageNumber);
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

}












