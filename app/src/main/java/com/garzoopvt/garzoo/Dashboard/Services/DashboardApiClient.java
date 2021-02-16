package com.garzoopvt.garzoo.Dashboard.Services;


import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.URLs;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.garzoopvt.garzoo.Util.Constants.NETWORK_TIMEOUT;

public class DashboardApiClient {

    private static final String TAG = "NotificationApiClient";
    private static DashboardApiClient instance;
    private MutableLiveData<List<DashboardList>> mDashboardList;
    private RetrieveDashboardRunnable mRetrieveDashboardRunnable;

    public static DashboardApiClient getInstance(){
        if(instance == null){
            instance = new DashboardApiClient();
        }
        return instance;
    }

    private DashboardApiClient() {
        mDashboardList = new MutableLiveData<>();
    }

    public LiveData<List<DashboardList>> getDashboardList(){
        return mDashboardList;
    }


    public void getDashboardList(String user_id , int pageNumber, String search_name, String latitude, String longitude){

        if(mRetrieveDashboardRunnable != null){
            mRetrieveDashboardRunnable = null;
        }
        mRetrieveDashboardRunnable = new RetrieveDashboardRunnable(user_id,pageNumber, search_name,latitude,longitude);
        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveDashboardRunnable);

        // Set a timeout for the data refresh
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // let the user know it timed out
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }


    private class RetrieveDashboardRunnable implements Runnable{

        private int pageNumber;
        private String user_id;
        private String search_name,  latitude,  longitude;
        private boolean cancelRequest;

        private RetrieveDashboardRunnable(String user_id,int pageNumber, String search_name, String latitude, String  longitude ) {
            this.pageNumber = pageNumber;
            this.user_id = user_id;
            this.search_name = search_name;
            this.latitude = latitude;
            this.longitude = longitude;
            cancelRequest = false;
        }

        @Override
        public void run() {

            try {
                Response response = getDashboardList(user_id,pageNumber).execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){
                    List<DashboardList> list = new ArrayList<>(((DashboardResponse)response.body()).getDashboard());

                    if(pageNumber == 1){
                        mDashboardList.postValue(list);
                    }
                    else{
                        List<DashboardList> currentRecipes = mDashboardList.getValue();
                        currentRecipes.addAll(list);
                        mDashboardList.postValue(currentRecipes);
                    }
                }
                else{
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: error: " + error);
                    mDashboardList.postValue(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mDashboardList.postValue(null);
            }
        }

        private Call<DashboardResponse> getDashboardList(String user_id, int pageNumber){
//            return ServiceGenerator.getDashboardApi().getDashboard(
//                    URLs.unique_id,
//                    user_id,
//                    String.valueOf(pageNumber),
//                    search_name,
//                    latitude,
//                    longitude);

            return null;
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the retrieval query");
            cancelRequest = true;
        }
    }


    public void cancelRequest(){
        if(mRetrieveDashboardRunnable!=null)
            mRetrieveDashboardRunnable.cancelRequest();
    }
}
