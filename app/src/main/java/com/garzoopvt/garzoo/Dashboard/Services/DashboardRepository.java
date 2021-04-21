package com.garzoopvt.garzoo.Dashboard.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDao;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDatabase;
import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
import com.garzoopvt.garzoo.RetrofitService.NetworkBoundResource;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.Constants;
import com.garzoopvt.garzoo.Util.URLs;


import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Query;

public class DashboardRepository {

    private static final String TAG = "DashboardRepository";

    private static DashboardRepository instance;
    private DashboardApiClient mDashboardApiClient;
    private int mPageNumber;


    public static DashboardRepository getInstance() {
        if (instance == null) {
            instance = new DashboardRepository();
        }
        return instance;
    }

    private DashboardRepository() {
        mDashboardApiClient = DashboardApiClient.getInstance();

    }

    public LiveData<List<DashboardList>> getDashboard() {
        return mDashboardApiClient.getDashboardList();
    }


    public void getDashboardList1(String user_id, int page_no, String search_name, String latitude, String longitude) {

        if (page_no == 0) {
            page_no = 1;
        }
        mPageNumber = page_no;
        mDashboardApiClient.getDashboardList(user_id, page_no, search_name, latitude, longitude);
    }


    public void cancelRequest() {
        mDashboardApiClient.cancelRequest();
    }


    public void searchNextPage(String user_id, String search_name, String latitude, String longitude) {
        getDashboardList1(user_id, mPageNumber + 1, search_name, latitude, longitude);
    }


    //new
    private DashboardListDao dashboardListDao;

    public static DashboardRepository getInstance(Context context) {
        if (instance == null) {
            instance = new DashboardRepository(context);
        }
        return instance;
    }

    private DashboardRepository(Context context) {
        dashboardListDao = DashboardListDatabase.getInstance(context).getDashboardListDao();
    }


    public LiveData<Resource<List<DashboardList>>> getDashboardList(final String user_id, final int pageNumber,
                                                                    final String search_name, final String latitude, final String longitude,
                                                                    final String type, final String district, final String taluka, final String city,
                                                                    final String listing_status) {
        return new NetworkBoundResource<List<DashboardList>, DashboardResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull DashboardResponse item) {

                try {
                    ArrayList<String> ids=new ArrayList<>();

                    if (item.getDashboard() != null) { //  list will be null if api key is expired

                        DashboardList[] list = new DashboardList[item.getDashboard().size()];

                        int index = 0;
                        for (long rowId : dashboardListDao.insertData((DashboardList[]) (item.getDashboard().toArray(list)))) {
                            if (rowId == -1) { // conflict detected
                                Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                                // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                                dashboardListDao.updateList(
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
                                        list[index].getDistance(),
                                        list[index].getListing_id()

                                );
                            }
                            ids.add(list[index].getId());
                            index++;

                        }

                        if(ids.size()>0) dashboardListDao.deleteOldData(ids);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public boolean shouldFetch(@Nullable List<DashboardList> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<DashboardList>> loadFromDb() {
                if (type.equals("0") || type.equals("")) {
                    return dashboardListDao.searchList(search_name, pageNumber);
                } else {
                    String address = district + " " + taluka + " " + city;
                    return dashboardListDao.searchList(search_name, pageNumber, type, address);
                }

            }

            @NonNull
            @Override
            public LiveData<ApiResponse<DashboardResponse>> createCall() {
                return ServiceGenerator.getDashboardApi().getDashboard(
                        URLs.unique_id,
                        user_id,
                        String.valueOf(pageNumber),
                        search_name,
                        latitude,
                        longitude,
                        type,
                        district,
                        taluka,
                        city,
                        listing_status
                );
            }

        }.getAsLiveData();
    }


    public Call<ResponseBody> interest(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title, RequestBody listing_type, RequestBody language) {

        return ServiceGenerator.getDashboardApi().interest(
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

    public LiveData<Resource<List<DashboardList>>> interest1(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                                             RequestBody listing_id, RequestBody type, RequestBody listing_title, RequestBody listing_type) {

        return new NetworkBoundResource<List<DashboardList>, DashboardResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull DashboardResponse item) {

                if (item.getDashboard() != null) { //  list will be null if api key is expired
                    DashboardList[] list = new DashboardList[item.getDashboard().size()];

                    int index = 0;
                    for (long rowId : dashboardListDao.insertData((DashboardList[]) (item.getDashboard().toArray(list)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            dashboardListDao.updateInterestStatusList(
                                    list[index].getId(),
                                    list[index].getInterest_status()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<DashboardList> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<DashboardList>> loadFromDb() {
                return dashboardListDao.searchList("", 1);

            }

            @NonNull
            @Override
            public LiveData<ApiResponse<DashboardResponse>> createCall() {
                return ServiceGenerator.getDashboardApi().interest1(
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

    public Call<ResponseBody> uploadToken(String user_id, String username, String token, String IMEINumber, String language ) {

        return ServiceGenerator.getDashboardApi().uploadToken(
                user_id,
                username,
                token, IMEINumber,
                language
        );

    }

    public Call<ResponseBody> block(String self_user_id, String to_user_id) {

        return ServiceGenerator.getDashboardApi().block(
                URLs.unique_id,
                self_user_id,
                to_user_id
        );

    }

    public LiveData<Resource<List<DashboardList>>> deletePost1(String type, String post_id, String master_id) {
        return new NetworkBoundResource<List<DashboardList>, DashboardResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull DashboardResponse item) {

                if (item.getDashboard() != null) { //  list will be null if api key is expired
                    DashboardList[] list = new DashboardList[item.getDashboard().size()];

                    int index = 0;
                    for (long rowId : dashboardListDao.insertData((DashboardList[]) (item.getDashboard().toArray(list)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            dashboardListDao.updateList(
                                    list[index].getId(),
                                    list[index].getStatus()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<DashboardList> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<DashboardList>> loadFromDb() {
                return dashboardListDao.searchList("", mPageNumber);

            }

            @NonNull
            @Override
            public LiveData<ApiResponse<DashboardResponse>> createCall() {
                return ServiceGenerator.getDashboardApi().Dashboard_delete_record_2_0(
                        URLs.unique_id,
                        post_id,
                        type,
                        master_id

                );
            }

        }.getAsLiveData();

    }


    public Call<ResponseBody> deletePost(String type, String post_id) {

        if (type.equals("E")) {
            return ServiceGenerator.getDashboardApi().Employment_delete_record_2_0(
                    URLs.unique_id,
                    post_id
            );
        } else if (type.equals("B")) {
            return ServiceGenerator.getDashboardApi().Business_delete_record_2_0(
                    URLs.unique_id,
                    post_id
            );
        } else if (type.equals("P")) {
            return ServiceGenerator.getDashboardApi().Pd_delete_record_2_0(
                    URLs.unique_id,
                    post_id
            );
        } else if (type.equals("R")) {
            return ServiceGenerator.getDashboardApi().Listing_rent_delete_record_2_0(
                    URLs.unique_id,
                    post_id
            );
        } else {
            return ServiceGenerator.getDashboardApi().Listing_sell_delete_record_2_0(
                    URLs.unique_id,
                    post_id
            );
        }


    }

    public Call<ResponseBody> ReportPost(String user_id, String post_id, String employment_id, String business_id, String report) {

        return ServiceGenerator.getDashboardApi().ReportPost(
                URLs.unique_id,
                user_id,
                post_id, employment_id, business_id, report
        );


    }


    public Call<ResponseBody> logActivity(String post_id, String post_user_id, String user_id, String type) {

        return ServiceGenerator.getDashboardApi().LogActivity(
                URLs.unique_id,
                post_id,
                post_user_id, user_id, type
        );


    }

    public Call<ResponseBody> deleteImage(String image_id, String path, String post_id) {

        return ServiceGenerator.getDashboardApi().deleteImage(
                image_id,
                path
        );


    }

    public void updateImage(String image_id, String path, String post_id) {

        dashboardListDao.updateImage(
                path,
                image_id,
                post_id

        );

    }

    public void blockRemovefromDb(String to_user_id) {
        dashboardListDao.block(to_user_id);
    }
}












