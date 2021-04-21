package com.garzoopvt.garzoo.Profile.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDao;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDatabase;
import com.garzoopvt.garzoo.Dashboard.Services.DashboardApiClient;
import com.garzoopvt.garzoo.Dashboard.Services.DashboardRepository;
import com.garzoopvt.garzoo.Dashboard.Services.DashboardResponse;
import com.garzoopvt.garzoo.Login.data.LoginRepository;
import com.garzoopvt.garzoo.Login.data.LoginResult;
import com.garzoopvt.garzoo.Profile.Model.BlockedPeople;
import com.garzoopvt.garzoo.Profile.Room.BlockedListDao;
import com.garzoopvt.garzoo.Profile.Room.BlockedListDatabase;
import com.garzoopvt.garzoo.Profile.Room.InterestedListDao;
import com.garzoopvt.garzoo.Profile.Room.InterestedListDatabase;
import com.garzoopvt.garzoo.Profile.Room.MyListDao;
import com.garzoopvt.garzoo.Profile.Room.MyListDatabase;
import com.garzoopvt.garzoo.Profile.Room.UserMoreDatabase;
import com.garzoopvt.garzoo.Profile.Room.UserMoreListDao;
import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
import com.garzoopvt.garzoo.RetrofitService.NetworkBoundResource;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.URLs;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ProfileRepository {
    private static final String TAG = "DashboardRepository";
    private static volatile ProfileRepository instance;

    private int mPageNumber;

    private InterestedListDao interestedListDao;
    private BlockedListDao blockedListDao;
    private MyListDao myListDao;
    private UserMoreListDao userMoreListDao;

    public static ProfileRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileRepository(context);
        }
        return instance;
    }

    private ProfileRepository(Context context) {
        interestedListDao = InterestedListDatabase.getInstance(context).getDashboardListDao();
        blockedListDao = BlockedListDatabase.getInstance(context).getListDao();
        myListDao = MyListDatabase.getInstance(context).getDashboardListDao();
        userMoreListDao = UserMoreDatabase.getInstance(context).getDashboardListDao();
    }

    public Call<ResponseBody> contactUs(String mobile, String email, String message, String name) {

        return ServiceGenerator.getProfileApi().contactUs(
                mobile, email, message, name
        );

    }

    public Call<ResponseBody> feedback(String mobile, String email, String message, String name) {

        return ServiceGenerator.getProfileApi().feedback(
                mobile, email, message, name
        );

    }


    public LiveData<Resource<List<DashboardList>>> getInterestedListApi(final String user_id, final int pageNumber, final String search_name, final String latitude, final String longitude) {
        return new NetworkBoundResource<List<DashboardList>, InterestedResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull InterestedResponse item) {

                if (item.getDashboard() != null) { //  list will be null if api key is expired
                    DashboardList[] list = new DashboardList[item.getDashboard().size()];
                    ArrayList<String> ids=new ArrayList<>();
                    int index = 0;
                    for (long rowId : interestedListDao.insertData((DashboardList[]) (item.getDashboard().toArray(list)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            interestedListDao.updateList(
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

                    if(ids.size()>0)interestedListDao.deleteOldData(ids);
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<DashboardList> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<DashboardList>> loadFromDb() {
                return interestedListDao.getLIst();
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<InterestedResponse>> createCall() {
                return ServiceGenerator.getProfileApi().getInterested(
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
                    DashboardList[] recipes = new DashboardList[item.getDashboard().size()];

                    int index = 0;
                    for (long rowId : interestedListDao.insertData((DashboardList[]) (item.getDashboard().toArray(recipes)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            interestedListDao.updateInterestStatusList(
                                    recipes[index].getId(),
                                    recipes[index].getInterest_status()
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
                return interestedListDao.getLIst();

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


    public LiveData<Resource<List<BlockedPeople>>> getBlokedListApi(final String user_id, final int pageNumber, final String query) {
        return new NetworkBoundResource<List<BlockedPeople>, BlockedPeopleResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull BlockedPeopleResponse item) {

                if (item.getBlocked() != null) { //  list will be null if api key is expired
                    BlockedPeople[] recipes = new BlockedPeople[item.getBlocked().size()];

                    int index = 0;
                    for (long rowId : blockedListDao.insertData((BlockedPeople[]) (item.getBlocked().toArray(recipes)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            blockedListDao.updateList(
                                    recipes[index].getId(),
                                    recipes[index].getBlock_record_id(),
                                    recipes[index].getFname(),
                                    recipes[index].getLname(),
                                    recipes[index].getStatus(),
                                    recipes[index].isFlag(),
                                    recipes[index].getLast_modified(),
                                    recipes[index].getDt(),
                                    recipes[index].getImage()
                            );
                        }
                        index++;
                    }
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<BlockedPeople> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<BlockedPeople>> loadFromDb() {
                return blockedListDao.getLIst();
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<BlockedPeopleResponse>> createCall() {
                return ServiceGenerator.getProfileApi().getBlocked(
                        URLs.unique_id,
                        user_id
//                        ,
//                        String.valueOf(pageNumber)
                );
            }

        }.getAsLiveData();
    }


    public LiveData<Resource<List<DashboardList>>> getMyListApi(final String user_id, final int pageNumber, final String search_name, final String latitude, final String longitude, final String type) {
        return new NetworkBoundResource<List<DashboardList>, MyListResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull MyListResponse item) {

                if (item.getDashboard() != null) { //  list will be null if api key is expired
                    DashboardList[] list = new DashboardList[item.getDashboard().size()];
                    ArrayList<String> ids=new ArrayList<>();
                    int index = 0;
                    for (long rowId : myListDao.insertData((DashboardList[]) (item.getDashboard().toArray(list)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            myListDao.updateList(
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
                    if(ids.size()>0) myListDao.deleteOldData(ids);
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<DashboardList> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<DashboardList>> loadFromDb() {
                return myListDao.searchList(search_name, pageNumber);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<MyListResponse>> createCall() {
                return ServiceGenerator.getProfileApi().getMyRecords(
                        URLs.unique_id,
                        user_id,
                        String.valueOf(pageNumber),
                        search_name,
                        latitude,
                        longitude,
                        type
                );
            }

        }.getAsLiveData();
    }

    public Call<ResponseBody> register(String user_id, String fname, String lname,
                                       String gender, String age,
                                       String state, String area,
                                       String taluka, String district,
                                       String latitude, String longitude) {

        return ServiceGenerator.getProfileApi().getRegister(
                user_id,
                fname,
                lname,
                gender,
                age,
                state,
                area,
                taluka,
                district,
                latitude,
                longitude
        );

    }

    public Call<ResponseBody> user_mobile_change(String mobileno, String latitude, String longitude, String user_id) {

        return ServiceGenerator.getProfileApi().user_mobile_change(
                mobileno, latitude, longitude, user_id
        );

    }

    public Call<ResponseBody> user_mobile_update(String mobileno, String user_id) {

        return ServiceGenerator.getProfileApi().user_mobile_update(
                mobileno, user_id
        );

    }


    public LiveData<Resource<List<DashboardList>>> getUserMoreListApi(final String user_id, final int pageNumber, final String search_name, final String latitude, final String longitude, final String type) {
        return new NetworkBoundResource<List<DashboardList>, MyListResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull MyListResponse item) {

                if (item.getDashboard() != null) { //  list will be null if api key is expired
                    DashboardList[] recipes = new DashboardList[item.getDashboard().size()];

                    int index = 0;
                    for (long rowId : userMoreListDao.insertData((DashboardList[]) (item.getDashboard().toArray(recipes)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            userMoreListDao.updateList(
                                    recipes[index].getId(),
                                    recipes[index].getTitle(),
                                    recipes[index].getDescription(),
                                    recipes[index].getPrice(),
                                    recipes[index].getAddress(),
                                    recipes[index].getData_type(),
                                    recipes[index].getUser_id()

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
                return userMoreListDao.getLIst(user_id, pageNumber, type);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<MyListResponse>> createCall() {
                return ServiceGenerator.getProfileApi().getMyRecords(
                        URLs.unique_id,
                        user_id,
                        String.valueOf(pageNumber),
                        search_name,
                        latitude,
                        longitude,
                        type
                );
            }

        }.getAsLiveData();
    }

    public Call<ResponseBody> logActivity(String post_id, String post_user_id, String user_id, String type) {

        return ServiceGenerator.getProfileApi().LogActivity(
                URLs.unique_id,
                post_id,
                post_user_id, user_id, type
        );


    }

    public Call<ResponseBody> RemoveBlock(String record_id) {

        return ServiceGenerator.getProfileApi().RemoveBlock(
                record_id
        );


    }

    public Call<ResponseBody> renew(String type, String record_id) {

        return ServiceGenerator.getProfileApi().renew(
                type, record_id
        );


    }

    public Call<ResponseBody> deletePost(String type, String post_id) {

        if (type.equals("E")) {
            return ServiceGenerator.getProfileApi().Employment_delete_record_2_0(
                    URLs.unique_id,
                    post_id
            );
        } else if (type.equals("B")) {
            return ServiceGenerator.getProfileApi().Business_delete_record_2_0(
                    URLs.unique_id,
                    post_id
            );
        } else if (type.equals("P")) {
            return ServiceGenerator.getProfileApi().Pd_delete_record_2_0(
                    URLs.unique_id,
                    post_id
            );
        } else if (type.equals("R")) {
            return ServiceGenerator.getProfileApi().Listing_rent_delete_record_2_0(
                    URLs.unique_id,
                    post_id
            );
        } else {
            return ServiceGenerator.getProfileApi().Listing_sell_delete_record_2_0(
                    URLs.unique_id,
                    post_id
            );
        }


    }

    public LiveData<Resource<List<DashboardList>>> deletePost1(String type, String post_id, String master_id) {
        return new NetworkBoundResource<List<DashboardList>, DashboardResponse>(AppExecutors.getInstance()) {

            @Override
            public void saveCallResult(@NonNull DashboardResponse item) {

                if (item.getDashboard() != null) { //  list will be null if api key is expired
                    DashboardList[] list = new DashboardList[item.getDashboard().size()];

                    int index = 0;
                    for (long rowId : myListDao.insertData((DashboardList[]) (item.getDashboard().toArray(list)))) {
                        if (rowId == -1) { // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            myListDao.updateList(
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
                return myListDao.searchList(type, mPageNumber);

            }

            @NonNull
            @Override
            public LiveData<ApiResponse<DashboardResponse>> createCall() {
                return ServiceGenerator.getProfileApi().Dashboard_delete_record_2_0(
                        URLs.unique_id,
                        post_id,
                        type,
                        master_id

                );
            }

        }.getAsLiveData();

    }

    public void addRemoveFromFrav(String id, String interest_status) {
        interestedListDao.updateInterestStatusList(id, interest_status);
    }

    public Call<ResponseBody> uploadToken(String user_id, String username, String token, String IMEINumber, String language ) {

        return ServiceGenerator.getProfileApi().uploadToken(
                user_id,
                username,
                token, IMEINumber,
                language
        );

    }

}
