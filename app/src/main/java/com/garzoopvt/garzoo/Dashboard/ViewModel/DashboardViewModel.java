package com.garzoopvt.garzoo.Dashboard.ViewModel;

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


import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Dashboard.Services.DashboardRepository;
import com.garzoopvt.garzoo.RetrofitService.Resource;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DashboardViewModel extends AndroidViewModel {

    private static final String TAG = "DashboardViewModel";

    private DashboardRepository dashboardRepository;
    private boolean mIsPerformingQuery;
    private boolean cancelRequest;
    private long requestStartTime;

//    public DashboardViewModel() {
//        dashboardRepository= DashboardRepository.getInstance();
//        mIsPerformingQuery = false;
//
//    }

    public LiveData<List<DashboardList>> getDashboard1(){
        return dashboardRepository.getDashboard();
    }

    public void getDashboardListApi1(String user_id , int page_no, String search_name, String latitude, String longitude){
        mIsPerformingQuery = true;
        dashboardRepository.getDashboardList(user_id, page_no,search_name, latitude,longitude);
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
            dashboardRepository.cancelRequest();
            mIsPerformingQuery = false;
        }
        return true;
    }

    public void searchNextPage1(String user_id, String search_name, String latitude, String longitude){
        Log.d(TAG, "searchNextPage: called.");
//        if(mIsPerformingQuery){
            dashboardRepository.searchNextPage(user_id, search_name, latitude,longitude);
//        }
    }



    //New
    public enum ViewState {CATEGORIES, RECIPES}
   // private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<Resource<List<DashboardList>>> dashbordlist = new MediatorLiveData<>();

    // query extras
    private boolean isQueryExhausted;
    private String query;
    private int pageNumber;
    private boolean isPerformingQuery;
    public static final String QUERY_EXHAUSTED = "Query is exhausted.";

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        dashboardRepository = DashboardRepository.getInstance(application);
        init();
    }

    private void init(){
//        if(viewState == null){
//            viewState = new MutableLiveData<>();
//            viewState.setValue(ViewState.CATEGORIES);
//        }
    }

    public int getPageNumber() {
        return pageNumber;
    }



    public LiveData<Resource<List<DashboardList>>> getDashboard(){
        return dashbordlist;
    }



    public void getDashboardListApi(String user_id , int page_no, String search_name, String latitude, String longitude){
        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            this.query = search_name;
            isQueryExhausted = false;
            executeList(user_id, pageNumber,query, latitude,longitude);
        }
    }


    public void searchNextPage(String user_id, String search_name, String latitude, String longitude){
        if(!isQueryExhausted && !isPerformingQuery){
            pageNumber++;
            executeList(user_id, pageNumber,search_name, latitude,longitude);
        }
    }


    private void executeList(String user_id , int page_no, String search_name, String latitude, String longitude){
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


        final LiveData<Resource<List<DashboardList>>> repositorySource = dashboardRepository.getDashboardList(user_id, page_no,search_name, latitude,longitude);
        dashbordlist.addSource(repositorySource, new Observer<Resource<List<DashboardList>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<DashboardList>> listResource) {
                if(!cancelRequest) {
                    if (listResource != null) {
                        dashbordlist.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    dashbordlist.setValue(new Resource<List<DashboardList>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            dashbordlist.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            dashbordlist.removeSource(repositorySource);
                        }
                    } else {
                        dashbordlist.removeSource(repositorySource);
                    }
                }
                else{
                    dashbordlist.removeSource(repositorySource);
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


    public Call<ResponseBody> interest( RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                         RequestBody listing_id, RequestBody type, RequestBody listing_title){

        return dashboardRepository.interest(unique_id, user_id,to_user_id,full_name,listing_id,type,listing_title);

    }


    public Call<ResponseBody> uploadToken(String user_id, String username, String token, String IMEINumber){

        return dashboardRepository.uploadToken( user_id,username,token,IMEINumber);

    }

    public Call<ResponseBody> block(String self_user_id, String to_user_id){

        return dashboardRepository.block( self_user_id,to_user_id);

    }
    public Call<ResponseBody> deletePost(String type, String to_user_id){

        return dashboardRepository.deletePost( type,to_user_id);

    }
    public Call<ResponseBody> ReportPost(String user_id, String post_id, String employment_id,String business_id,String report){

        return dashboardRepository.ReportPost(user_id, post_id, employment_id,business_id, report);

    }

}