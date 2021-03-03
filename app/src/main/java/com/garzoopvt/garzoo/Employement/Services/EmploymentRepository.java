package com.garzoopvt.garzoo.Employement.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;


import com.garzoopvt.garzoo.Employement.Model.Employment;
import com.garzoopvt.garzoo.Employement.Persistence.EmploymentDao;
import com.garzoopvt.garzoo.Employement.Persistence.EmploymentDatabase;
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

public class EmploymentRepository {

    private static final String TAG = "EmploymentRepository";

    private static EmploymentRepository instance;

    //new
    private EmploymentDao employmentDao;

    public static EmploymentRepository getInstance(Context context) {
        if (instance == null) {
            instance = new EmploymentRepository(context);
        }
        return instance;
    }

    private EmploymentRepository(Context context) {
        employmentDao = EmploymentDatabase.getInstance(context).getListDao();
    }


    public LiveData<Resource<List<Employment>>> getEmploymentList(final String user_id, final int pageNumber, final String search_name, final String latitude, final String longitude, final String category_id) {
        return new NetworkBoundResource<List<Employment>, EmploymentResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull EmploymentResponse item) {

                if (item.getEmployment() != null) { //  list will be null if api key is expired
                    Employment[] recipes = new Employment[item.getEmployment().size()];

                    int index = 0;
                    for (long rowId : employmentDao.insertData((Employment[]) (item.getEmployment().toArray(recipes)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            employmentDao.updateList(
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


    public Call<ResponseBody> interest(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title) {

        return ServiceGenerator.getEmploymentApi().interest(
                unique_id,
                user_id,
                to_user_id,
                full_name,
                listing_id,
                type,
                listing_title
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

    public Call<ResponseBody> block(String self_user_id, String to_user_id){

        return  ServiceGenerator.getEmploymentApi().block(
                URLs.unique_id,
                self_user_id,
                to_user_id
        );

    }

    public Call<ResponseBody> deletePost(String post_id){


        return  ServiceGenerator.getEmploymentApi().Employment_delete_record_2_0(
                URLs.unique_id,
                post_id
        );

    }

    public Call<ResponseBody> ReportPost(String user_id, String post_id, String employment_id,String business_id,String report){

        return  ServiceGenerator.getEmploymentApi().ReportPost(
                URLs.unique_id,
                user_id,
                post_id, employment_id,business_id, report
        );





    }
}












