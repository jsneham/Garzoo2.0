package com.garzoopvt.garzoo.Business.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.Business.Model.Business;
import com.garzoopvt.garzoo.Business.Persistence.BusinessDao;
import com.garzoopvt.garzoo.Business.Persistence.BusinessDatabase;
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

public class BusinessRepository {

    private static final String TAG = "BusinessRepository";

    private static BusinessRepository instance;

    //new
    private BusinessDao businessDao;
    public static BusinessRepository getInstance(Context context){
        if(instance == null){
            instance = new BusinessRepository(context);
        }
        return instance;
    }

    private BusinessRepository(Context context) {
         businessDao = BusinessDatabase.getInstance(context).getListDao();
    }


    public LiveData<Resource<List<Business>>> getBusinessList(final String user_id , final int pageNumber, final String search_name, final String latitude, final String longitude, final String category_id){
        return new NetworkBoundResource<List<Business>, BusinessResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull BusinessResponse item) {

                if(item.getBusiness() != null){ //  list will be null if api key is expired
                    Business[] recipes = new Business[item.getBusiness().size()];

                    int index = 0;
                    for(long rowId: businessDao.insertData((Business[])(item.getBusiness().toArray(recipes)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                              businessDao.updateList(
                                    recipes[index].getId(),
                                    recipes[index].getTitle(),
                                    recipes[index].getDescription(),
                                    recipes[index].getPrice(),
                                    recipes[index].getAddress()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Business> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Business>> loadFromDb() {
                return   businessDao.searchList(search_name, pageNumber);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<BusinessResponse>> createCall() {
                return ServiceGenerator.getBusinessApi().getBusiness(
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

    public Call<ResponseBody> uploadData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                             RequestBody description,  RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file){

        return  ServiceGenerator.getBusinessApi().upload(
                user_id,
                category_id,
                mobile_status,
                title,
                description,
                latitude,
                longitude,
                address,
                list,
                video_file
        );

    }


    public Call<ResponseBody> interest(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title){

        return  ServiceGenerator.getBusinessApi().interest(
                unique_id,
                user_id,
                to_user_id,
                full_name,
                listing_id,
                type,
                listing_title
        );

    }
}












