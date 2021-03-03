package com.garzoopvt.garzoo.Promotion.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;


import com.garzoopvt.garzoo.Promotion.Model.Promotion;
import com.garzoopvt.garzoo.Promotion.Persistence.PromotionDao;
import com.garzoopvt.garzoo.Promotion.Persistence.PromotionDatabase;
import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
import com.garzoopvt.garzoo.RetrofitService.NetworkBoundResource;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.URLs;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class PromotionRepository {

    private static final String TAG = "PromotionRepository";

    private static PromotionRepository instance;

    //new
    private PromotionDao promotionDao;
    public static PromotionRepository getInstance(Context context){
        if(instance == null){
            instance = new PromotionRepository(context);
        }
        return instance;
    }

    private PromotionRepository(Context context) {
        promotionDao = PromotionDatabase.getInstance(context).getListDao();
    }


    public LiveData<Resource<List<Promotion>>> getPromotionList(final String user_id , final int pageNumber, final String search_name, final String latitude, final String longitude, final String category_id){
        return new NetworkBoundResource<List<Promotion>, PromotionResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull PromotionResponse item) {

                if(item.getPromotion() != null){ //  list will be null if api key is expired
                    Promotion[] recipes = new Promotion[item.getPromotion().size()];

                    int index = 0;
                    for(long rowId: promotionDao.insertData((Promotion[])(item.getPromotion().toArray(recipes)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                              promotionDao.updateList(
                                    recipes[index].getId(),
                                    recipes[index].getTitle(),
                                    recipes[index].getDescription(),
                                    recipes[index].getAddress()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Promotion> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Promotion>> loadFromDb() {
                return   promotionDao.searchList(search_name, pageNumber);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<PromotionResponse>> createCall() {
                return ServiceGenerator.getPromotionApi().getPromotion(
                        URLs.unique_id,
                        user_id,
                        String.valueOf(pageNumber),
                        search_name,
                        latitude,
                        longitude
                );
            }

        }.getAsLiveData();
    }


    public void cancelRequest(){
//        mDashboardApiClient.cancelRequest();
    }


    public Call<ResponseBody> uploadData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status,  RequestBody title,
                                             RequestBody description, RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file, RequestBody pd_status){

        return  ServiceGenerator.getPromotionApi().upload(
                user_id,
                mobile_status,
                title,
                description,
                latitude,
                longitude,
                address,
                list,
                video_file,
                pd_status
        );

    }

    public Call<ResponseBody> interest(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title){

        return  ServiceGenerator.getPromotionApi().interest(
                unique_id,
                user_id,
                to_user_id,
                full_name,
                listing_id,
                type,
                listing_title
        );

    }

    public Call<ResponseBody> editData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody title,
                                       RequestBody description,  RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file, RequestBody pd_status, RequestBody product_id){

        return  ServiceGenerator.getPromotionApi().edit(
                user_id,
                mobile_status,
                title,
                description,
                latitude,
                longitude,
                address,
                list,
                video_file,
                pd_status,
                product_id
        );

    }

    public Call<ResponseBody> deleteImage(String image_id, String path){

        return  ServiceGenerator.getPromotionApi().deleteImage(
                image_id,
                path
        );

    }
}












