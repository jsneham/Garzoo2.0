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
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDao;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDatabase;
import com.garzoopvt.garzoo.Profile.Room.MyListDao;
import com.garzoopvt.garzoo.Profile.Room.MyListDatabase;
import com.garzoopvt.garzoo.RetrofitService.AddResponse;
import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
import com.garzoopvt.garzoo.RetrofitService.NetworkBoundResource;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.URLs;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class BuyRepository {

    private static final String TAG = "BuyRepository";

    private static BuyRepository instance;
    private static Context context;

    //new
    private BuyDao buyDao;
    private DashboardListDao dashboardListDao;
    private MyListDao myListDao;
    private CategoryDao categoryDao;
    public static BuyRepository getInstance(Context context){
        if(instance == null){
            instance = new BuyRepository(context);
        }
        return instance;
    }

    private BuyRepository(Context context) {
        this.context=context;
        buyDao = BuyDatabase.getInstance(context).getListDao();
        categoryDao = CategoryDatabase.getInstance(context).getListDao();

    }


    public LiveData<Resource<List<Buy>>> getBuyList(final String user_id , final int pageNumber, final String search_name, final String latitude,
                                                    final String longitude, final String category_id){
        return new NetworkBoundResource<List<Buy>, BuyResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull BuyResponse item) {

                if(item.getBuy() != null){ //  list will be null if api key is expired
                    Buy[] list = new Buy[item.getBuy().size()];
                    ArrayList<String> ids=new ArrayList<>();
                    int index = 0;
                    for(long rowId: buyDao.insertData((Buy[])(item.getBuy().toArray(list)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            buyDao.updateList(
                                    list[index].getId(),
                                    list[index].getTitle(),
                                    list[index].getDescription(),
                                    list[index].getPrice(),
                                    list[index].getAddress(),
                                    list[index].getImages(),
                                    list[index].getImage_id(),
                                    list[index].getBlock_status(),
                                    list[index].getStatus(),
                                    list[index].getInterest_status(),
                                    list[index].getLatitude(),
                                    list[index].getLongitude(),
                                    list[index].getDistance()

                            );
                        }
                        ids.add(list[index].getId());
                        index++;

                    }

                    if(ids.size()>0) buyDao.deleteOldData(ids);
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


    public LiveData<Resource<List<Category>>> getCategoryList(String language_code){
        return new NetworkBoundResource<List<Category>, CategoryResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull CategoryResponse item) {

                if(item.getCategory() != null){ //  list will be null if api key is expired
                    Category[] list = new Category[item.getCategory().size()];

                    int index = 0;
                    for(long rowId: categoryDao.insertData((Category[])(item.getCategory().toArray(list)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            categoryDao.updateList(
                                    list[index].getId(),
                                    list[index].getName(),
                                    list[index].getImage()
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
                return ServiceGenerator.getBuyApi().getCategory(language_code);
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


    public  LiveData<Resource<List<Buy>>> interest1(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title,RequestBody listing_type,final int pageNumber,  final String category_id){


        return new NetworkBoundResource<List<Buy>, BuyResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull BuyResponse item) {

                if(item.getBuy() != null){ //  list will be null if api key is expired
                    Buy[] list = new Buy[item.getBuy().size()];

                    int index = 0;
                    for(long rowId: buyDao.insertData((Buy[])(item.getBuy().toArray(list)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            buyDao.updateInterestStatusList(
                                    list[index].getId(),
                                    list[index].getInterest_status()

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
                return buyDao.searchList("", pageNumber, category_id);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<BuyResponse>> createCall() {
                return  ServiceGenerator.getBuyApi().interest1(
                        unique_id,
                        user_id,
                        to_user_id,
                        full_name,
                        listing_id,
                        type,
                        listing_title,
                        listing_type
                );
            }

        }.getAsLiveData();


    }

    public   Call<ResponseBody> interest(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                                   RequestBody listing_id, RequestBody type, RequestBody listing_title,RequestBody listing_type,RequestBody language,final int pageNumber,  final String category_id){


                return  ServiceGenerator.getBuyApi().interest(
                        unique_id,
                        user_id,
                        to_user_id,
                        full_name,
                        listing_id,
                        type,
                        listing_title,
                        listing_type,language
                );



    }

    public Call<ResponseBody> deleteImage(String image_id, String path, String post_id){

        return  ServiceGenerator.getBuyApi().deleteImage(
                image_id,
                path
        );



    }

    public void updateImage(String image_id, String path, String post_id, String screen){

        if(screen.equalsIgnoreCase("dashboard")){
            dashboardListDao = DashboardListDatabase.getInstance(context).getDashboardListDao();
            dashboardListDao.updateImage(
                    path,
                    image_id,
                    post_id

            );
        }
       else if(screen.equalsIgnoreCase("mylist")){
            myListDao = MyListDatabase.getInstance(context).getDashboardListDao();
            myListDao.updateImage(
                    path,
                    image_id,
                    post_id

            );
        }
        else {
            buyDao.updateImage(
                    path,
                    image_id,
                    post_id

            );
        }



    }


    public Call<ResponseBody> block(String self_user_id, String to_user_id){

        return  ServiceGenerator.getBuyApi().block(
                URLs.unique_id,
                self_user_id,
                to_user_id
        );

    }

    public LiveData<Resource<List<Buy>>> deletePost(String post_id,  final int pageNumber,  final String category_id){

        return new NetworkBoundResource<List<Buy>, BuyResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull BuyResponse item) {

                if(item.getBuy() != null){ //  list will be null if api key is expired
                    Buy[] list = new Buy[item.getBuy().size()];

                    int index = 0;
                    for(long rowId: buyDao.insertData((Buy[])(item.getBuy().toArray(list)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            buyDao.updateList(
                                    list[index].getId(),
                                    list[index].getStatus()

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
                return buyDao.searchList("", pageNumber, category_id);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<BuyResponse>> createCall() {
                return ServiceGenerator.getBuyApi().Listing_sell_delete_record_2_0(
                        URLs.unique_id,
                        post_id
                );
            }

        }.getAsLiveData();


//        return  ServiceGenerator.getBuyApi().Listing_sell_delete_record_2_0(
//                URLs.unique_id,
//                post_id
//        );

    }

    public Call<ResponseBody> ReportPost(String user_id, String post_id, String employment_id,String business_id,String report){

        return  ServiceGenerator.getBuyApi().ReportPost(
                URLs.unique_id,
                user_id,
                post_id, employment_id,business_id, report
        );





    }


    public Call<ResponseBody> logActivity(String post_id, String post_user_id, String user_id,String type) {

        return ServiceGenerator.getBuyApi().LogActivity(
                URLs.unique_id,
                post_id,
                post_user_id, user_id, type
        );


    }

    public void blockRemovefromDb(String to_user_id) {
        buyDao.delete(to_user_id);
        DashboardListDao dao = DashboardListDatabase.getInstance(context).getDashboardListDao();
        dao.block(to_user_id);
    }
}












