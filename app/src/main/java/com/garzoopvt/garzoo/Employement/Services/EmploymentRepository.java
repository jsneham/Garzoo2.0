package com.garzoopvt.garzoo.Employement.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;


import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDao;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDatabase;
import com.garzoopvt.garzoo.Employement.Model.Employment;
import com.garzoopvt.garzoo.Employement.Persistence.EmploymentDao;
import com.garzoopvt.garzoo.Employement.Persistence.EmploymentDatabase;
import com.garzoopvt.garzoo.Profile.Room.MyListDao;
import com.garzoopvt.garzoo.Profile.Room.MyListDatabase;
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

public class EmploymentRepository {

    private static final String TAG = "EmploymentRepository";

    private static EmploymentRepository instance;

    //new
    private EmploymentDao employmentDao;
    private DashboardListDao dashboardListDao;
    private MyListDao myListDao;
    private static Context context;

    public static EmploymentRepository getInstance(Context context) {
        if (instance == null) {
            instance = new EmploymentRepository(context);
        }
        return instance;
    }

    private EmploymentRepository(Context context) {
        this.context=context;
        employmentDao = EmploymentDatabase.getInstance(context).getListDao();
    }


    public LiveData<Resource<List<Employment>>> getEmploymentList(final String user_id, final int pageNumber, final String search_name, final String latitude, final String longitude, final String category_id) {
        return new NetworkBoundResource<List<Employment>, EmploymentResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull EmploymentResponse item) {

                if (item.getEmployment() != null) { //  list will be null if api key is expired
                    Employment[] list = new Employment[item.getEmployment().size()];
                    ArrayList<String> ids=new ArrayList<>();

                    int index = 0;
                    for (long rowId : employmentDao.insertData((Employment[]) (item.getEmployment().toArray(list)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            employmentDao.updateList(
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

                    if(ids.size()>0) employmentDao.deleteOldData(ids);
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Employment> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Employment>> loadFromDb() {
                return employmentDao.searchList(search_name, pageNumber);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<EmploymentResponse>> createCall() {
                return ServiceGenerator.getEmploymentApi().getEmployment(
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


    public void cancelRequest() {
//        mDashboardApiClient.cancelRequest();
    }


    public Call<ResponseBody> uploadData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                         RequestBody description, RequestBody price, RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file, RequestBody emp_status) {

        return ServiceGenerator.getEmploymentApi().upload(
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
                emp_status
        );

    }


    public LiveData<Resource<List<Employment>>>  interest1(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title, RequestBody listing_type) {

        return new NetworkBoundResource<List<Employment>, EmploymentResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull EmploymentResponse item) {

                if (item.getEmployment() != null) { //  list will be null if api key is expired
                    Employment[] list = new Employment[item.getEmployment().size()];

                    int index = 0;
                    for (long rowId : employmentDao.insertData((Employment[]) (item.getEmployment().toArray(list)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            employmentDao.updateInterestStatusList(
                                    list[index].getId(),
                                    list[index].getInterest_status()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Employment> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Employment>> loadFromDb() {
                return employmentDao.searchList("", 1);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<EmploymentResponse>> createCall() {
                return ServiceGenerator.getEmploymentApi().interest1(
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

    public Call<ResponseBody>  interest(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
             RequestBody listing_id, RequestBody type, RequestBody listing_title, RequestBody listing_type, RequestBody language) {


                return ServiceGenerator.getEmploymentApi().interest(
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


    public Call<ResponseBody> editData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                       RequestBody description, RequestBody price, RequestBody latitude, RequestBody longitude, RequestBody address,
                                       MultipartBody.Part video_file, RequestBody emp_status, RequestBody product_id) {

        return ServiceGenerator.getEmploymentApi().edit(
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
                emp_status,
                product_id
        );

    }


    public Call<ResponseBody> available(String unique_id, String user_id, String listing_id) {

        return ServiceGenerator.getEmploymentApi().available(
                unique_id,
                user_id,
                listing_id
        );

    }

    public Call<ResponseBody> unavailable(String unique_id, String user_id, String listing_id) {

        return ServiceGenerator.getEmploymentApi().unavailable(
                unique_id,
                user_id,
                listing_id
        );

    }

    public Call<ResponseBody> deleteImage(String image_id, String path){

        return  ServiceGenerator.getEmploymentApi().deleteImage(
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
            employmentDao.updateImage(
                    path,
                    image_id,
                    post_id

            );
        }



    }


    public Call<ResponseBody> block(String self_user_id, String to_user_id){

        return  ServiceGenerator.getEmploymentApi().block(
                URLs.unique_id,
                self_user_id,
                to_user_id
        );

    }

    public LiveData<Resource<List<Employment>>>  deletePost(String post_id){
        return new NetworkBoundResource<List<Employment>, EmploymentResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull EmploymentResponse item) {

                if (item.getEmployment() != null) { //  list will be null if api key is expired
                    Employment[] list = new Employment[item.getEmployment().size()];

                    int index = 0;
                    for (long rowId : employmentDao.insertData((Employment[]) (item.getEmployment().toArray(list)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            employmentDao.updateList(
                                    list[index].getId(),
                                    list[index].getStatus()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Employment> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Employment>> loadFromDb() {
                return employmentDao.searchList("", 1);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<EmploymentResponse>> createCall() {
                return ServiceGenerator.getEmploymentApi().Employment_delete_record_2_0(
                        URLs.unique_id,
                        post_id
                );
            }

        }.getAsLiveData();

//        return  ServiceGenerator.getEmploymentApi().Employment_delete_record_2_0(
//                URLs.unique_id,
//                post_id
//        );

    }

    public Call<ResponseBody> ReportPost(String user_id, String post_id, String employment_id,String business_id,String report){

        return  ServiceGenerator.getEmploymentApi().ReportPost(
                URLs.unique_id,
                user_id,
                post_id, employment_id,business_id, report
        );





    }

    public Call<ResponseBody> logActivity(String post_id, String post_user_id, String user_id,String type) {

        return ServiceGenerator.getEmploymentApi().LogActivity(
                URLs.unique_id,
                post_id,
                post_user_id, user_id, type
        );


    }

    public void blockRemovefromDb(String to_user_id) {
        employmentDao.delete(to_user_id);
        DashboardListDao dao = DashboardListDatabase.getInstance(context).getDashboardListDao();
        dao.block(to_user_id);
    }
}












