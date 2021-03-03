package com.garzoopvt.garzoo.Rent.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;


import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.garzoopvt.garzoo.Rent.Model.Rent;
import com.garzoopvt.garzoo.Rent.Services.RentRepository;
import com.garzoopvt.garzoo.RetrofitService.Resource;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;


public class RentViewModel extends AndroidViewModel {

    private static final String TAG = "RentViewModel";

    private RentRepository rentRepository;
    private boolean mIsPerformingQuery;
    private boolean cancelRequest;
    private long requestStartTime;



    public boolean onBackPressed(){
        if(mIsPerformingQuery){
            // cancel the query
            rentRepository.cancelRequest();
            mIsPerformingQuery = false;
        }
        return true;
    }


    //New
    private MediatorLiveData<Resource<List<Rent>>> rentList = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Category>>> categoryList = new MediatorLiveData<>();

    // query extras
    private boolean isQueryExhausted;
    private String query;
    private int pageNumber;
    private boolean isPerformingQuery;
    public static final String QUERY_EXHAUSTED = "Query is exhausted.";

    public RentViewModel(@NonNull Application application) {
        super(application);
        rentRepository = RentRepository.getInstance(application);
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
    public void setPageNumber(int page) {
        pageNumber= page;
    }


    public LiveData<Resource<List<Rent>>> getRent(){
        return rentList;
    }



    public void getRentListApi(String user_id , int page_no, String search_name, String latitude, String longitude, String category_id){
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


        final LiveData<Resource<List<Rent>>> repositorySource = rentRepository.getRentList(user_id, page_no,search_name, latitude,longitude,category_id);
        rentList.addSource(repositorySource, new Observer<Resource<List<Rent>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Rent>> listResource) {
                if(!cancelRequest) {
                    if (listResource != null) {
                        rentList.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    rentList.setValue(new Resource<List<Rent>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            rentList.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            rentList.removeSource(repositorySource);
                        }
                    } else {
                        rentList.removeSource(repositorySource);
                    }
                }
                else{
                    rentList.removeSource(repositorySource);
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


    public LiveData<Resource<List<Category>>> getCategory(){
        return categoryList;
    }



    public void getCategoryListApi(){

        final LiveData<Resource<List<Category>>> repositorySource = rentRepository.getCategoryList();
        categoryList.addSource(repositorySource, new Observer<Resource<List<Category>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Category>> listResource) {
                if (listResource != null) {
                    categoryList.setValue(listResource);
                    if (listResource.status == Resource.Status.SUCCESS) {
                        Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                        if (listResource.data != null) {
                            if (listResource.data.size() == 0) {
                                Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                categoryList.setValue(new Resource<List<Category>>(
                                        Resource.Status.ERROR,
                                        listResource.data,
                                        QUERY_EXHAUSTED
                                ));

                            }
                        }
                        // must remove or it will keep listening to repository
                        categoryList.removeSource(repositorySource);
                    } else if (listResource.status == Resource.Status.ERROR) {
                        Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                        categoryList.removeSource(repositorySource);
                    }
                } else {
                    categoryList.removeSource(repositorySource);
                }


            }
        });

    }


    public Call<ResponseBody> uploadData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                             RequestBody description, RequestBody price, RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file, RequestBody listing_status, RequestBody available_status){

        return rentRepository.uploadData(list, user_id,mobile_status,category_id,title,description,price,latitude,longitude,address,video_file,listing_status,available_status);

    }

    public Call<ResponseBody> editData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                       RequestBody description, RequestBody price, RequestBody latitude, RequestBody longitude, RequestBody address,
                                       MultipartBody.Part video_file, RequestBody product_id, RequestBody listing_status){

        return rentRepository.editData(list, user_id,mobile_status,category_id,title,description,price,latitude,longitude,address,video_file,product_id,listing_status);

    }

    public Call<ResponseBody> interest( RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                        RequestBody listing_id, RequestBody type, RequestBody listing_title){

        return rentRepository.interest(unique_id, user_id,to_user_id,full_name,listing_id,type,listing_title);

    }

    public Call<ResponseBody> deleteImage( String image_id, String path){

        return rentRepository.deleteImage(image_id, path);

    }

    public Call<ResponseBody> block(String self_user_id, String to_user_id){

        return rentRepository.block( self_user_id,to_user_id);

    }
    public Call<ResponseBody> deletePost (String to_user_id){

        return rentRepository.deletePost(to_user_id);

    }
    public Call<ResponseBody> ReportPost(String user_id, String post_id, String employment_id,String business_id,String report){

        return rentRepository.ReportPost(user_id, post_id, employment_id,business_id, report);

    }
}