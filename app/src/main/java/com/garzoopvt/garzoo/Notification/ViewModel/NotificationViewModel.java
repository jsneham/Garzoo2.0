package com.garzoopvt.garzoo.Notification.ViewModel;



import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.garzoopvt.garzoo.Chat.Services.ChatRepository;
import com.garzoopvt.garzoo.Notification.Model.Notification;
import com.garzoopvt.garzoo.Notification.Services.NotificationRepository;
import com.garzoopvt.garzoo.RetrofitService.Resource;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.garzoopvt.garzoo.Util.URLs.language;

public class NotificationViewModel extends AndroidViewModel {
    private static final String TAG = "NotificationViewModel";

    private NotificationRepository notificationRepository;
    private boolean mIsPerformingQuery;

    // query extras
    private boolean isQueryExhausted;
    private String query;
    private int pageNumber;
    private boolean isPerformingQuery;
    private long requestStartTime;
    private boolean cancelRequest;
    public static final String QUERY_EXHAUSTED = "Query is exhausted.";
    private MediatorLiveData<Resource<List<Notification>>> notificationList = new MediatorLiveData<>();

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        notificationRepository = NotificationRepository.getInstance(application);
    }


    public int getPageNumber() {
        return pageNumber;
    }


    public LiveData<Resource<List<Notification>>> getNotification(){
        return notificationList;
    }



    public void getNotificationApi(String user_id , int page_no, String search_name, String language){
        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            this.query = search_name;
            isQueryExhausted = false;
            executeList(user_id, pageNumber,query, language);
        }

    }



    private void executeList(String user_id , int page_no, String search_name, String language){
        requestStartTime = System.currentTimeMillis();
        isPerformingQuery = true;
        cancelRequest = false;


        final LiveData<Resource<List<Notification>>> repositorySource = notificationRepository.getList(user_id, page_no,search_name,language);
        notificationList.addSource(repositorySource, new Observer<Resource<List<Notification>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Notification>> listResource) {
                if(!cancelRequest) {
                    if (listResource != null) {
                        notificationList.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    notificationList.setValue(new Resource<List<Notification>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            notificationList.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            notificationList.removeSource(repositorySource);
                        }
                    } else {
                        notificationList.removeSource(repositorySource);
                    }
                }
                else{
                    notificationList.removeSource(repositorySource);
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




    public void setIsPerformingQuery(Boolean isPerformingQuery){
        mIsPerformingQuery = isPerformingQuery;
    }

    public boolean isPerformingQuery(){
        return mIsPerformingQuery;
    }


    public boolean onBackPressed(){
        if(mIsPerformingQuery){
            // cancel the query
            notificationRepository.cancelRequest();
            mIsPerformingQuery = false;
        }
        return true;
    }


    public Call<ResponseBody> ResetNotificationCount(String user_id){
        return notificationRepository.resetNotificationCount(user_id);
    }
}