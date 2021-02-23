package com.garzoopvt.garzoo.BuySell.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.garzoopvt.garzoo.BuySell.Persistence.BuyDao;
import com.garzoopvt.garzoo.BuySell.Persistence.BuyDatabase;
import com.garzoopvt.garzoo.BuySell.Persistence.CategoryDao;
import com.garzoopvt.garzoo.BuySell.Persistence.CategoryDatabase;
import com.garzoopvt.garzoo.RetrofitService.AddResponse;
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
import retrofit2.Response;

public class BuyRepository {

    private static final String TAG = "BuyRepository";

    private static BuyRepository instance;

    //new
    private BuyDao buyDao;
    private CategoryDao categoryDao;
    public static BuyRepository getInstance(Context context){
        if(instance == null){
            instance = new BuyRepository(context);
        }
        return instance;
    }

    private BuyRepository(Context context) {
        buyDao = BuyDatabase.getInstance(context).getListDao();
        categoryDao = CategoryDatabase.getInstance(context).getListDao();
    }


    public LiveData<Resource<List<Buy>>> getBuyList(final String user_id , final int pageNumber, final String search_name, final String latitude,
                                                    final String longitude, final String category_id){
        return new NetworkBoundResource<List<Buy>, BuyResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull BuyResponse item) {

                if(item.getBuy() != null){ //  list will be null if api key is expired
                    Buy[] recipes = new Buy[item.getBuy().size()];

                    int index = 0;
                    for(long rowId: buyDao.insertData((Buy[])(item.getBuy().toArray(recipes)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            buyDao.updateList(
                                    recipes[index].getId(),
                                    recipes[index].getTitle(),
                                    recipes[index].getDescription(),
                                    recipes[index].getPrice(),
                                    recipes[index].getAddress(),
                                    recipes[index].getImages(),
                                    recipes[index].getImage_id()

                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Buy> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Buy>> loadFromDb() {
                return buyDao.searchList(search_name, pageNumber, category_id);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<BuyResponse>> createCall() {
                return ServiceGenerator.getBuyApi().getBuy(
                        URLs.unique_id,
                        user_id,
                        String.valueOf(pageNumber),
                        search_name,
                        latitude,
                        longitude,
                        category_id
                );
            }

        }.getAsLiveData();
    }


    public void cancelRequest(){
//        mDashboardApiClient.cancelRequest();
    }


    public LiveData<Resource<List<Category>>> getCategoryList(){
        return new NetworkBoundResource<List<Category>, CategoryResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull CategoryResponse item) {

                if(item.getCategory() != null){ //  list will be null if api key is expired
                    Category[] recipes = new Category[item.getCategory().size()];

                    int index = 0;
                    for(long rowId: categoryDao.insertData((Category[])(item.getCategory().toArray(recipes)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            categoryDao.updateList(
                                    recipes[index].getId(),
                                    recipes[index].getName(),
                                    recipes[index].getImage()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Category> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Category>> loadFromDb() {
                return categoryDao.searchList("");
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<CategoryResponse>> createCall() {
                return ServiceGenerator.getBuyApi().getCategory();
            }

        }.getAsLiveData();
    }



    public Call<ResponseBody> uploadData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                            RequestBody description, RequestBody price, RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file){

      return  ServiceGenerator.getBuyApi().upload(
                user_id,
                category_id,
                mobile_status,
                title,
                description,
                price,
                latitude,
                longitude,
                address,
               list,
              video_file
        );

    }

    public Call<ResponseBody> editData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                            RequestBody description, RequestBody price, RequestBody latitude, RequestBody longitude,
                                       RequestBody address, MultipartBody.Part video_file,RequestBody product_id){

      return  ServiceGenerator.getBuyApi().editData(
                user_id,
                category_id,
                mobile_status,
                title,
                description,
                price,
                latitude,
                longitude,
                address,
               list,
              video_file,
              product_id
        );

    }


    public Call<ResponseBody> interest(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title){

        return  ServiceGenerator.getBuyApi().interest(
                unique_id,
                user_id,
                to_user_id,
                full_name,
                listing_id,
                type,
                listing_title
        );

    }

    public Call<ResponseBody> deleteImage(String image_id, String path){

        return  ServiceGenerator.getBuyApi().deleteImage(
                image_id,
                path
        );

    }
}












