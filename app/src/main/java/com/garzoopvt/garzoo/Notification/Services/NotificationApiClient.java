package com.garzoopvt.garzoo.Notification.Services;



import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.garzoopvt.garzoo.Notification.Model.Notification;
import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.Constants;
import com.garzoopvt.garzoo.Util.URLs;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;


import static com.garzoopvt.garzoo.Util.Constants.NETWORK_TIMEOUT;

public class NotificationApiClient {

    private static final String TAG = "NotificationApiClient";
    private static NotificationApiClient instance;
    private MutableLiveData<List<Notification>> mNotification;
    private RetrieveNotificationRunnable mRetrieveNotificationRunnable;

    public static NotificationApiClient getInstance(){
        if(instance == null){
            instance = new NotificationApiClient();
        }
        return instance;
    }

    private NotificationApiClient() {
        mNotification = new MutableLiveData<>();
    }

    public LiveData<List<Notification>> getNotification(){
        return mNotification;
    }


    public void getNotification(String user_id , int pageNumber){

        if(mRetrieveNotificationRunnable != null){
            mRetrieveNotificationRunnable = null;
        }
        mRetrieveNotificationRunnable = new RetrieveNotificationRunnable(user_id,pageNumber);
        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveNotificationRunnable);

        // Set a timeout for the data refresh
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // let the user know it timed out
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }


    private class RetrieveNotificationRunnable implements Runnable{

        private int pageNumber;
        private String user_id;
        private boolean cancelRequest;

        private RetrieveNotificationRunnable(String user_id,int pageNumber) {
            this.pageNumber = pageNumber;
            this.user_id = user_id;
            cancelRequest = false;
        }

        @Override
        public void run() {

            try {
                Response response = getNotification(user_id,pageNumber).execute();
                Log.d(TAG, response.message());
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){
                    List<Notification> list = new ArrayList<>(((NotificationResponse)response.body()).getNotification());

                    if(pageNumber == 1){
                        mNotification.postValue(list);
                    }
                    else{
                        List<Notification> currentRecipes = mNotification.getValue();
                        currentRecipes.addAll(list);
                        mNotification.postValue(currentRecipes);
                    }
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: error: " + error);
                    mNotification.postValue(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mNotification.postValue(null);
            }
        }

        private Call<NotificationResponse> getNotification(String user_id,int pageNumber){
            return ServiceGenerator.getNotificationApi().getNotification(
                    URLs.unique_id,
                    user_id,
                    String.valueOf(pageNumber));
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the retrieval query");
            cancelRequest = true;
        }
    }


    public void cancelRequest(){
        if(mRetrieveNotificationRunnable!=null)
            mRetrieveNotificationRunnable.cancelRequest();
    }
}
