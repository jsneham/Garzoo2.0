package com.garzoopvt.garzoo.Business.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.garzoopvt.garzoo.Business.Model.Business;
import com.garzoopvt.garzoo.Business.Services.BusinessRepository;

import com.garzoopvt.garzoo.RetrofitService.Resource;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;


public class BusinessViewModel extends AndroidViewModel {

    private static final String TAG = "BusinessViewModel";

    private BusinessRepository businessRepository;
    private boolean mIsPerformingQuery;
    private boolean cancelRequest;
    private long requestStartTime;



    public boolean onBackPressed(){
        if(mIsPerformingQuery){
            // cancel the query
            businessRepository.cancelRequest();
            mIsPerformingQuery = false;
        }
        return true;
    }


    //New
    private MediatorLiveData<Resource<List<Business>>> list = new MediatorLiveData<>();

    // query extras
    private boolean isQueryExhausted;
    private String query;
    private int pageNumber;
    private boolean isPerformingQuery;
    public static final String QUERY_EXHAUSTED = "Query is exhausted.";

    public BusinessViewModel(@NonNull Application application) {
        super(application);
        businessRepository = BusinessRepository.getInstance(application);
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



    public LiveData<Resource<List<Business>>> getBusiness(){
        return list;
    }



    public void getBusinessListApi(String user_id , int page_no, String search_name, String latitude, String longitude, String category_id){
        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            this.query = search_name;
            isQueryExhausted = false;
            executeList(user_id, pageNumber,query, latitude,longitude,category_id);
        }
    }


    public void searchNextPage(String user_id, String search_name, String latitude, String longitude, String category_id){
        if(!isQueryExhausted && !isPerformingQuery){
            pageNumber++;
            executeList(user_id, pageNumber,search_name, latitude,longitude,category_id);
        }
    }


    private void executeList(String user_id , int page_no, String search_name, String latitude, String longitude, String category_id){
        requestStartTime = System.currentTimeMillis();
        isPerformingQuery = true;
        cancelRequest = false;


        final LiveData<Resource<List<Business>>> repositorySource = businessRepository.getBusinessList(user_id, page_no,search_name, latitude,longitude,category_id);
        list.addSource(repositorySource, new Observer<Resource<List<Business>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Business>> listResource) {
                if(!cancelRequest) {
                    if (listResource != null) {
                        list.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    list.setValue(new Resource<List<Business>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            list.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            list.removeSource(repositorySource);
                        }
                    } else {
                        list.removeSource(repositorySource);
                    }
                }
                else{
                    list.removeSource(repositorySource);
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

    public Call<ResponseBody> uploadData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                             RequestBody description, RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file){

        return businessRepository.uploadData(list, user_id,mobile_status,category_id,title,description,latitude,longitude,address,video_file);

    }


    public Call<ResponseBody> interest( RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                        RequestBody listing_id, RequestBody type, RequestBody listing_title){

        return businessRepository.interest(unique_id, user_id,to_user_id,full_name,listing_id,type,listing_title);

    }

    public Call<ResponseBody> deleteImage( String image_id, String path){

        return businessRepository.deleteImage(image_id, path);

    }

    public Call<ResponseBody> editData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                         RequestBody description, RequestBody latitude, RequestBody longitude,
                                       RequestBody address, MultipartBody.Part video_file, RequestBody product_id){

        return businessRepository.editData(list, user_id,mobile_status,category_id,title,description,latitude,longitude,address,video_file, product_id);

    }


    public Call<ResponseBody> block(String self_user_id, String to_user_id){

        return businessRepository.block( self_user_id,to_user_id);

    }
    public Call<ResponseBody> deletePost (String to_user_id){

        return businessRepository.deletePost(to_user_id);

    }
    public Call<ResponseBody> ReportPost(String user_id, String post_id, String employment_id,String business_id,String report){

        return businessRepository.ReportPost(user_id, post_id, employment_id,business_id, report);

    }


}