package com.garzoopvt.garzoo.Rent.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;


import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.garzoopvt.garzoo.BuySell.Persistence.CategoryDao;
import com.garzoopvt.garzoo.BuySell.Persistence.CategoryDatabase;
import com.garzoopvt.garzoo.BuySell.Services.CategoryResponse;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDao;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDatabase;
import com.garzoopvt.garzoo.Profile.Room.MyListDao;
import com.garzoopvt.garzoo.Profile.Room.MyListDatabase;
import com.garzoopvt.garzoo.Rent.Model.Rent;
import com.garzoopvt.garzoo.Rent.Persistence.RentCategoryDao;
import com.garzoopvt.garzoo.Rent.Persistence.RentCategoryDatabase;
import com.garzoopvt.garzoo.Rent.Persistence.RentDao;
import com.garzoopvt.garzoo.Rent.Persistence.RentDatabase;
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


public class RentRepository {

    private static final String TAG = "RentRepository";

    private static RentRepository instance;

    //new
    private RentCategoryDao categoryDao;
    private RentDao rentDao;
    private DashboardListDao dashboardListDao;
    private MyListDao myListDao;
    private static Context context;

    public static RentRepository getInstance(Context context){
        if(instance == null){
            instance = new RentRepository(context);
        }
        return instance;
    }

    private RentRepository(Context context) {
        this.context=context;
        rentDao = RentDatabase.getInstance(context).getListDao();
        categoryDao = RentCategoryDatabase.getInstance(context).getListDao();
    }


    public LiveData<Resource<List<Rent>>> getRentList(final String user_id , final int pageNumber, final String search_name, final String latitude, final String longitude, final String category_id){
        return new NetworkBoundResource<List<Rent>, RentResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull RentResponse item) {

                if(item.getRent() != null){ //  list will be null if api key is expired
                    Rent[] list = new Rent[item.getRent().size()];
                    ArrayList<String> ids=new ArrayList<>();

                    int index = 0;
                    for(long rowId: rentDao.insertData((Rent[])(item.getRent().toArray(list)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            rentDao.updateList(
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

                    if(ids.size()>0) rentDao.deleteOldData(ids);
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Rent> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Rent>> loadFromDb() {
                return rentDao.searchList(search_name, pageNumber,category_id);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<RentResponse>> createCall() {
                return ServiceGenerator.getRentApi().getRent(
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
                return ServiceGenerator.getRentApi().getCategory(
                        URLs.unique_id,
                        language_code
                );
            }

        }.getAsLiveData();
    }

    public Call<ResponseBody> uploadData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                             RequestBody description, RequestBody price, RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file, RequestBody listing_status, RequestBody available_status){

        return  ServiceGenerator.getRentApi().upload(
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
                listing_status,
                available_status
        );

    }

    public  Call<ResponseBody> interest(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
       RequestBody listing_id, RequestBody type, RequestBody listing_title, RequestBody listing_type, RequestBody language){


                return  ServiceGenerator.getRentApi().interest(
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

    public LiveData<Resource<List<Rent>>> interest1(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title, RequestBody listing_type){

        return new NetworkBoundResource<List<Rent>, RentResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull RentResponse item) {

                if(item.getRent() != null){ //  list will be null if api key is expired
                    Rent[] list = new Rent[item.getRent().size()];

                    int index = 0;
                    for(long rowId: rentDao.insertData((Rent[])(item.getRent().toArray(list)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            rentDao.updateInterestStatusList(
                                    list[index].getId(),
                                    list[index].getInterest_status()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Rent> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Rent>> loadFromDb() {
                return rentDao.searchList("", 1);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<RentResponse>> createCall() {
                return  ServiceGenerator.getRentApi().interest1(
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


    public Call<ResponseBody> editData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                       RequestBody description, RequestBody price, RequestBody latitude, RequestBody longitude,
                                       RequestBody address, MultipartBody.Part video_file,RequestBody product_id,RequestBody listing_status){

        return  ServiceGenerator.getRentApi().editData(
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
                product_id,listing_status
        );

    }

    public Call<ResponseBody> deleteImage(String image_id, String path){

        return  ServiceGenerator.getRentApi().deleteImage(
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
            rentDao.updateImage(
                    path,
                    image_id,
                    post_id

            );
        }



    }


    public Call<ResponseBody> block(String self_user_id, String to_user_id){

        return  ServiceGenerator.getRentApi().block(
                URLs.unique_id,
                self_user_id,
                to_user_id
        );

    }

    public LiveData<Resource<List<Rent>>> deletePost(String post_id){

        return new NetworkBoundResource<List<Rent>, RentResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull RentResponse item) {

                if(item.getRent() != null){ //  list will be null if api key is expired
                    Rent[] list = new Rent[item.getRent().size()];

                    int index = 0;
                    for(long rowId: rentDao.insertData((Rent[])(item.getRent().toArray(list)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            rentDao.updateList(
                                    list[index].getId(),
                                    list[index].getStatus()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Rent> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Rent>> loadFromDb() {
                return rentDao.searchList("", 1);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<RentResponse>> createCall() {
                return ServiceGenerator.getRentApi().Listing_rent_delete_record_2_0(
                        URLs.unique_id,
                        post_id
                );
            }

        }.getAsLiveData();
//        return  ServiceGenerator.getRentApi().Listing_rent_delete_record_2_0(
//                URLs.unique_id,
//                post_id
//        );

    }

    public Call<ResponseBody> ReportPost(String user_id, String post_id, String employment_id,String business_id,String report){

        return  ServiceGenerator.getRentApi().ReportPost(
                URLs.unique_id,
                user_id,
                post_id, employment_id,business_id, report
        );





    }

    public Call<ResponseBody> available(String unique_id, String user_id, String listing_id) {

        return ServiceGenerator.getRentApi().available(
                unique_id,
                user_id,
                listing_id
        );

    }

    public Call<ResponseBody> unavailable(String unique_id, String user_id, String listing_id) {

        return ServiceGenerator.getRentApi().unavailable(
                unique_id,
                user_id,
                listing_id
        );

    }

    public Call<ResponseBody> logActivity(String post_id, String post_user_id, String user_id,String type) {

        return ServiceGenerator.getRentApi().LogActivity(
                URLs.unique_id,
                post_id,
                post_user_id, user_id, type
        );


    }
    public void blockRemovefromDb(String to_user_id) {
        rentDao.delete(to_user_id);
        DashboardListDao dao = DashboardListDatabase.getInstance(context).getDashboardListDao();
        dao.block(to_user_id);
    }
}












