package com.garzoopvt.garzoo.BuySell.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;


import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.garzoopvt.garzoo.BuySell.Persistence.BuyDao;
import com.garzoopvt.garzoo.BuySell.Persistence.BuyDatabase;
import com.garzoopvt.garzoo.BuySell.Services.BuyRepository;

import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDao;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDatabase;
import com.garzoopvt.garzoo.RetrofitService.Resource;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;


public class BuyViewModel extends AndroidViewModel {

    private static final String TAG = "BuyViewModel";

    private BuyRepository buyRepository;
    private boolean mIsPerformingQuery;
    private boolean cancelRequest;
    private long requestStartTime;



    public boolean onBackPressed(){
        if(mIsPerformingQuery){
            // cancel the query
            buyRepository.cancelRequest();
            mIsPerformingQuery = false;
        }
        return true;
    }


    //New
    private MediatorLiveData<Resource<List<Buy>>> buyList = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Category>>> categoryList = new MediatorLiveData<>();

    // query extras
    private boolean isQueryExhausted;
    private String query;
    private int pageNumber;
    private boolean isPerformingQuery;
    public static final String QUERY_EXHAUSTED = "Query is exhausted.";

    public BuyViewModel(@NonNull Application application) {
        super(application);
        buyRepository = BuyRepository.getInstance(application);
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


    public LiveData<Resource<List<Buy>>> getBuy(){
        return buyList;
    }




 public void getBuyListApi(String user_id , int page_no, String search_name, String latitude, String longitude, String category_id){
//        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            this.query = search_name;
            isQueryExhausted = false;
            executeList(user_id, pageNumber,query, latitude,longitude,category_id);
//        }
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


        final LiveData<Resource<List<Buy>>> repositorySource = buyRepository.getBuyList(user_id, page_no,search_name, latitude,longitude,category_id);
        buyList.addSource(repositorySource, new Observer<Resource<List<Buy>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Buy>> listResource) {
                if(!cancelRequest) {
                    if (listResource != null) {
                        buyList.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    buyList.setValue(new Resource<List<Buy>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            buyList.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            buyList.removeSource(repositorySource);
                        }
                    } else {
                        buyList.removeSource(repositorySource);
                    }
                }
                else{
                    buyList.removeSource(repositorySource);
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



    public void getCategoryListApi(String language_code){

        final LiveData<Resource<List<Category>>> repositorySource = buyRepository.getCategoryList(language_code);
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
                                            RequestBody description, RequestBody price, RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file){

       return buyRepository.uploadData(list, user_id,mobile_status,category_id,title,description,price,latitude,longitude,address,video_file);

    }

    public Call<ResponseBody> editData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                            RequestBody description, RequestBody price, RequestBody latitude, RequestBody longitude, RequestBody address,
                                       MultipartBody.Part video_file, RequestBody product_id){

       return buyRepository.editData(list, user_id,mobile_status,category_id,title,description,price,latitude,longitude,address,video_file,product_id);

    }

    public Call<ResponseBody> interest( RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                        RequestBody listing_id, RequestBody type, RequestBody listing_title,RequestBody listing_type,RequestBody language, String category_id){

//        final LiveData<Resource<List<Buy>>> repositorySource = buyRepository.interest(unique_id, user_id,to_user_id,full_name,listing_id,type,listing_title,listing_type,  pageNumber, category_id );
//        buyList.addSource(repositorySource, new Observer<Resource<List<Buy>>>() {
//            @Override
//            public void onChanged(@Nullable Resource<List<Buy>> listResource) {
//                if(!cancelRequest) {
//                    if (listResource != null) {
//                        buyList.setValue(listResource);
//                        if (listResource.status == Resource.Status.SUCCESS) {
//                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
//                            isPerformingQuery = false;
//                            if (listResource.data != null) {
//                                if (listResource.data.size() == 0) {
//                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
//                                    buyList.setValue(new Resource<List<Buy>>(
//                                            Resource.Status.ERROR,
//                                            listResource.data,
//                                            QUERY_EXHAUSTED
//                                    ));
//                                    isPerformingQuery = true;
//                                }
//                            }
//                            // must remove or it will keep listening to repository
//                            buyList.removeSource(repositorySource);
//                        } else if (listResource.status == Resource.Status.ERROR) {
//                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
//                            isPerformingQuery = false;
//                            buyList.removeSource(repositorySource);
//                        }
//                    } else {
//                        buyList.removeSource(repositorySource);
//                    }
//                }
//                else{
//                    buyList.removeSource(repositorySource);
//                }
//            }
//        });
        return buyRepository.interest(unique_id, user_id,to_user_id,full_name,listing_id,type,listing_title,listing_type, language, pageNumber, category_id );

    }

    public Call<ResponseBody> deleteImage( String image_id, String path, String post_id){

        return buyRepository.deleteImage(image_id, path, post_id);

    }

    public void updateImage(String image_id, String path, String post_id, String dashboard){

        buyRepository.updateImage(image_id, path, post_id,dashboard);

    }


    public Call<ResponseBody> block(String self_user_id, String to_user_id){

        return buyRepository.block( self_user_id,to_user_id);

    }
//    public LiveData<Resource<List<Buy>>> deletePost (String to_user_id, String category_id){
    public void deletePost (String to_user_id, String category_id){

        final LiveData<Resource<List<Buy>>> repositorySource = buyRepository.deletePost(to_user_id,  pageNumber, category_id );
        buyList.addSource(repositorySource, new Observer<Resource<List<Buy>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Buy>> listResource) {
                if(!cancelRequest) {
                    if (listResource != null) {
                        buyList.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    buyList.setValue(new Resource<List<Buy>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            buyList.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            buyList.removeSource(repositorySource);
                        }
                    } else {
                        buyList.removeSource(repositorySource);
                    }
                }
                else{
                    buyList.removeSource(repositorySource);
                }
            }
        });



     //   return buyRepository.deletePost(to_user_id,  pageNumber, category_id );

    }
    public Call<ResponseBody> ReportPost(String user_id, String post_id, String employment_id,String business_id,String report){

        return buyRepository.ReportPost(user_id, post_id, employment_id,business_id, report);

    }

    public Call<ResponseBody> logActivity(String post_id, String post_user_id, String user_id,String type){

        return buyRepository.logActivity(post_id,post_user_id,user_id,type);

    }


    public void blockRemovefromDb(String to_user_id) {

        buyRepository.blockRemovefromDb(to_user_id);


    }
}