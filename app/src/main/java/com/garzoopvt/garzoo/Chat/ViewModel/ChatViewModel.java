package com.garzoopvt.garzoo.Chat.ViewModel;



import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.garzoopvt.garzoo.Chat.Model.ChatGroup;
import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.garzoopvt.garzoo.Chat.Services.ChatRepository;
import com.garzoopvt.garzoo.RetrofitService.Resource;


import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private static final String TAG = "ChatViewModel";

    private ChatRepository chatRepository;
    private boolean mIsPerformingQuery;
    private boolean cancelRequest;
    private long requestStartTime;
    private MediatorLiveData<Resource<List<ChatUser>>> chatUserList = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<ChatGroup>>> chatGroupList = new MediatorLiveData<>();

    // query extras
    private boolean isQueryExhausted;
    private String query;
    private int pageNumber;
    private boolean isPerformingQuery;
    public static final String QUERY_EXHAUSTED = "Query is exhausted.";


    public ChatViewModel(@NonNull Application application) {
        super(application);
        chatRepository = ChatRepository.getInstance(application);
    }


    public int getPageNumber() {
        return pageNumber;
    }



    public LiveData<Resource<List<ChatUser>>> getChatUser(){
        return chatUserList;
    }
    public LiveData<Resource<List<ChatGroup>>> geChatGroup(){
        return chatGroupList;
    }



    public void getChatUserListApi(String user_id , int page_no, String search_name){
        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            this.query = search_name;
            isQueryExhausted = false;
            executeList(user_id, pageNumber,query);
        }
    }


    public void searchNextPage(String user_id, String search_name){
        if(!isQueryExhausted && !isPerformingQuery){
            pageNumber++;
            executeList(user_id, pageNumber,search_name);
        }
    }


    private void executeList(String user_id , int page_no, String search_name){
        requestStartTime = System.currentTimeMillis();
        isPerformingQuery = true;
        cancelRequest = false;
//        final LiveData<Resource<List<DashboardList>>> repositorySource1= dashboardRepository.getDashboardList(user_id, page_no,search_name, latitude,longitude);
//        dashbordlist.addSource(repositorySource1, new Observer<Resource<List<DashboardList>>>() {
//            @Override
//            public void onChanged(@Nullable Resource<List<DashboardList>> listResource) {
//                dashbordlist.setValue(listResource);
//            }
//        });


        final LiveData<Resource<List<ChatUser>>> repositorySource = chatRepository.getChatUserList(user_id, page_no,search_name);
        chatUserList.addSource(repositorySource, new Observer<Resource<List<ChatUser>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ChatUser>> listResource) {
                if(!cancelRequest) {
                    if (listResource != null) {
                        chatUserList.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    chatUserList.setValue(new Resource<List<ChatUser>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            chatUserList.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            chatUserList.removeSource(repositorySource);
                        }
                    } else {
                        chatUserList.removeSource(repositorySource);
                    }
                }
                else{
                    chatUserList.removeSource(repositorySource);
                }
            }
        });

    }

    public void cancelSearchRequest(boolean isPerformingQuery){
        if(isPerformingQuery){
            Log.d(TAG, "cancelSearchRequest: canceling the search request.");
            cancelRequest = true;
            isPerformingQuery = false;
            pageNumber = 1;
        }
    }



    public void getChatGroupListApi(String user_id , int page_no, String search_name){
        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            this.query = search_name;
            isQueryExhausted = false;
            executeListGroup(user_id, pageNumber,query);
        }
    }

    public void searchNextPageGroup(String user_id, String search_name){
        if(!isQueryExhausted && !isPerformingQuery){
            pageNumber++;
            executeList(user_id, pageNumber,search_name);
        }
    }


    private void executeListGroup(String user_id , int page_no, String search_name){
        requestStartTime = System.currentTimeMillis();
        isPerformingQuery = true;
        cancelRequest = false;
//        final LiveData<Resource<List<DashboardList>>> repositorySource1= dashboardRepository.getDashboardList(user_id, page_no,search_name, latitude,longitude);
//        dashbordlist.addSource(repositorySource1, new Observer<Resource<List<DashboardList>>>() {
//            @Override
//            public void onChanged(@Nullable Resource<List<DashboardList>> listResource) {
//                dashbordlist.setValue(listResource);
//            }
//        });


        final LiveData<Resource<List<ChatGroup>>> repositorySource = chatRepository.getChatGroupList(user_id, page_no,search_name);
        chatGroupList.addSource(repositorySource, new Observer<Resource<List<ChatGroup>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ChatGroup>> listResource) {
                if(!cancelRequest) {
                    if (listResource != null) {
                        chatGroupList.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    chatGroupList.setValue(new Resource<List<ChatGroup>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            chatGroupList.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            chatGroupList.removeSource(repositorySource);
                        }
                    } else {
                        chatGroupList.removeSource(repositorySource);
                    }
                }
                else{
                    chatGroupList.removeSource(repositorySource);
                }
            }
        });

    }


}