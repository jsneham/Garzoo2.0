package com.garzoopvt.garzoo.Chat.ViewModel;


import android.app.Application;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.garzoopvt.garzoo.Chat.Model.ChatGroupList;
import com.garzoopvt.garzoo.Chat.Model.ChatIndividual;
import com.garzoopvt.garzoo.Chat.Services.ChatIndividualResponse;
import com.garzoopvt.garzoo.Chat.Services.ChatMessagesRepository;
import com.garzoopvt.garzoo.RetrofitService.Resource;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ChatRoomViewModel extends AndroidViewModel {

    private static final String TAG = "ChatViewModel";

    private ChatMessagesRepository chatRepository;
    private boolean mIsPerformingQuery;
    private boolean cancelRequest;
    private long requestStartTime;
    private MediatorLiveData<Resource<List<ChatIndividual>>> chatUserList = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<ChatGroupList>>> chatGroupList = new MediatorLiveData<>();


    // query extras
    private boolean isQueryExhausted;
    private String tuid;
    private int pageNumber;
    private boolean isPerformingQuery;
    public static final String QUERY_EXHAUSTED = "Query is exhausted.";


    public ChatRoomViewModel(@NonNull Application application) {
        super(application);
        chatRepository = ChatMessagesRepository.getInstance(application);
    }


    public int getPageNumber() {
        return pageNumber;
    }


    public LiveData<Resource<List<ChatIndividual>>> getChats() {
        return chatUserList;
    }

    public LiveData<Resource<List<ChatGroupList>>> getGroupChats() {
        return chatGroupList;
    }


    public void getChatListApi(String user_id, int page_no, String tuid) {
        if (!isPerformingQuery) {
            if (pageNumber == 0) {
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            this.tuid = tuid;
            isQueryExhausted = false;
            executeList(user_id, pageNumber, tuid);
        }
    }


    private void executeList(String user_id, int page_no, String tuid) {
        requestStartTime = System.currentTimeMillis();
        isPerformingQuery = true;
        cancelRequest = false;

        final LiveData<Resource<List<ChatIndividual>>> repositorySource = chatRepository.getChats(user_id, page_no, tuid);
        chatUserList.addSource(repositorySource, new Observer<Resource<List<ChatIndividual>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ChatIndividual>> listResource) {
                if (!cancelRequest) {
                    if (listResource != null) {
                        chatUserList.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    chatUserList.setValue(new Resource<List<ChatIndividual>>(
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
                } else {
                    chatUserList.removeSource(repositorySource);
                }
            }
        });

    }

    public void cancelSearchRequest(boolean isPerformingQuery) {
        if (isPerformingQuery) {
            Log.d(TAG, "cancelSearchRequest: canceling the search request.");
            cancelRequest = true;
            isPerformingQuery = false;
            pageNumber = 1;
        }
    }


    public Call<ResponseBody> block(String user_id, String to_id) {

        return chatRepository.block(user_id, to_id);

    }

    public Call<ChatIndividualResponse> addChat(RequestBody unique_id, RequestBody from_uid, RequestBody to_uid, RequestBody message,
                                                RequestBody file_type, MultipartBody.Part list[]) {

        return chatRepository.addChat(unique_id, from_uid, to_uid, message, file_type, list);

    }


    public Call<ResponseBody> sendSinglePush(String app_name, String message, String from_uid, String to_uid,
                                             String chat_action) {

        return chatRepository.sendSinglePush(app_name, message, from_uid, to_uid, chat_action);

    }


    public void getGroupChatListApi(String user_id, int page_no, String tuid) {
        if (!isPerformingQuery) {
            if (pageNumber == 0) {
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            this.tuid = tuid;
            isQueryExhausted = false;
            executeGroupList(user_id, pageNumber, tuid);
        }
    }


    private void executeGroupList(String user_id, int page_no, String tuid) {
        requestStartTime = System.currentTimeMillis();
        isPerformingQuery = true;
        cancelRequest = false;

        final LiveData<Resource<List<ChatGroupList>>> repositorySource = chatRepository.getGroupChats(user_id, page_no, tuid);
        chatGroupList.addSource(repositorySource, new Observer<Resource<List<ChatGroupList>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ChatGroupList>> listResource) {
                if (!cancelRequest) {
                    if (listResource != null) {
                        chatGroupList.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    chatGroupList.setValue(new Resource<List<ChatGroupList>>(
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
                } else {
                    chatGroupList.removeSource(repositorySource);
                }
            }
        });

    }

    public Call<ResponseBody> ResetChatCount(String record_id){
        return chatRepository.resetChatCount(record_id);
    }


}