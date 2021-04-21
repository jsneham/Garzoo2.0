package com.garzoopvt.garzoo.Business.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.Business.Model.Business;
import com.garzoopvt.garzoo.Business.Persistence.BusinessDao;
import com.garzoopvt.garzoo.Business.Persistence.BusinessDatabase;
import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.BuySell.Services.BuyResponse;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDao;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDatabase;
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
import retrofit2.http.Part;

public class BusinessRepository {

    private static final String TAG = "BusinessRepository";

    private static BusinessRepository instance;

    //new
    private BusinessDao businessDao;
    private DashboardListDao dashboardListDao;
    private MyListDao myListDao;
    private static Context context;

    public static BusinessRepository getInstance(Context context) {
        if (instance == null) {
            instance = new BusinessRepository(context);
        }
        return instance;
    }

    private BusinessRepository(Context context) {
        this.context=context;
        businessDao = BusinessDatabase.getInstance(context).getListDao();
    }


    public LiveData<Resource<List<Business>>> getBusinessList(final String user_id, final int pageNumber, final String search_name, final String latitude, final String longitude, final String category_id) {
        return new NetworkBoundResource<List<Business>, BusinessResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull BusinessResponse item) {

                if (item.getBusiness() != null) { //  list will be null if api key is expired
                    Business[] business = new Business[item.getBusiness().size()];
                    ArrayList<String> ids=new ArrayList<>();
                    int index = 0;
                    for (long rowId : businessDao.insertData((Business[]) (item.getBusiness().toArray(business)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            businessDao.updateList(
                                    business[index].getId(),
                                    business[index].getTitle(),
                                    business[index].getDescription(),
                                    business[index].getPrice(),
                                    business[index].getAddress(),
                                    business[index].getImages(),
                                    business[index].getImage_id(),
                                    business[index].getBlock_status(),
                                    business[index].getStatus(),
                                    business[index].getInterest_status(),
                                    business[index].getLatitude(),
                                    business[index].getLongitude(),
                                    business[index].getDistance()

                            );
                        }
                        ids.add(business[index].getId());
                        index++;

                    }

                    if(ids.size()>0) businessDao.deleteOldData(ids);
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Business> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Business>> loadFromDb() {
                return businessDao.searchList(search_name, pageNumber);
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


    public void cancelRequest() {
//        mDashboardApiClient.cancelRequest();
    }

    public Call<ResponseBody> uploadData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                         RequestBody description, RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file) {

        return ServiceGenerator.getBusinessApi().upload(
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
                                                       RequestBody listing_id, RequestBody type, RequestBody listing_title, RequestBody listing_type, RequestBody language) {


                return ServiceGenerator.getBusinessApi().interest(
                        unique_id,
                        user_id,
                        to_user_id,
                        full_name,
                        listing_id,
                        type,
                        listing_title,
                        listing_type,
                        language
                );




    }


    public LiveData<Resource<List<Business>>> interest1(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title, RequestBody listing_type) {

        return new NetworkBoundResource<List<Business>, BusinessResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull BusinessResponse item) {

                if (item.getBusiness() != null) { //  list will be null if api key is expired
                    Business[] business = new Business[item.getBusiness().size()];

                    int index = 0;
                    for (long rowId : businessDao.insertData((Business[]) (item.getBusiness().toArray(business)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            businessDao.updateInterestStatusList(
                                    business[index].getId(),
                                    business[index].getInterest_status()
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
                return businessDao.searchList("", 1);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<BusinessResponse>> createCall() {
                return ServiceGenerator.getBusinessApi().interest1(
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

    public Call<ResponseBody> deleteImage(String image_id, String path) {

        return ServiceGenerator.getBusinessApi().deleteImage(
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
            businessDao.updateImage(
                    path,
                    image_id,
                    post_id

            );
        }



    }


    public Call<ResponseBody> editData(MultipartBody.Part list[], RequestBody user_id, RequestBody mobile_status, RequestBody category_id, RequestBody title,
                                       RequestBody description, RequestBody latitude, RequestBody longitude, RequestBody address, MultipartBody.Part video_file, RequestBody product_id) {

        return ServiceGenerator.getBusinessApi().edit(
                user_id,
                category_id,
                mobile_status,
                title,
                description,
                latitude,
                longitude,
                address,
                list,
                video_file,
                product_id
        );

    }

    public Call<ResponseBody> block(String self_user_id, String to_user_id) {

        return ServiceGenerator.getBusinessApi().block(
                URLs.unique_id,
                self_user_id,
                to_user_id
        );

    }

    public LiveData<Resource<List<Business>>> deletePost(String post_id) {

        return new NetworkBoundResource<List<Business>, BusinessResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull BusinessResponse item) {

                if (item.getBusiness() != null) { //  list will be null if api key is expired
                    Business[] business = new Business[item.getBusiness().size()];

                    int index = 0;
                    for (long rowId : businessDao.insertData((Business[]) (item.getBusiness().toArray(business)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            businessDao.updateList(
                                    business[index].getId(),
                                    business[index].getStatus()
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
                return businessDao.searchList("", 1);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<BusinessResponse>> createCall() {
                return ServiceGenerator.getBusinessApi().Business_delete_record_2_0(
                        URLs.unique_id,
                        post_id
                );
            }

        }.getAsLiveData();


//        return  ServiceGenerator.getBusinessApi().Business_delete_record_2_0(
//                    URLs.unique_id,
//                    post_id
//            );

    }

    public Call<ResponseBody> ReportPost(String user_id, String post_id, String employment_id, String business_id, String report) {

        return ServiceGenerator.getBusinessApi().ReportPost(
                URLs.unique_id,
                user_id,
                post_id, employment_id, business_id, report
        );


    }

    public Call<ResponseBody> logActivity(String post_id, String post_user_id, String user_id,String type) {

        return ServiceGenerator.getBusinessApi().LogActivity(
                URLs.unique_id,
                post_id,
                post_user_id, user_id, type
        );


    }

    public void blockRemovefromDb(String to_user_id) {
        businessDao.delete(to_user_id);
        DashboardListDao dao = DashboardListDatabase.getInstance(context).getDashboardListDao();
        dao.block(to_user_id);
    }

}












