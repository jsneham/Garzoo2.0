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
import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
import com.garzoopvt.garzoo.RetrofitService.NetworkBoundResource;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.URLs;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ProfileRepository {
    private static final String TAG = "DashboardRepository";
    private static volatile ProfileRepository instance;

    private int mPageNumber;

    private InterestedListDao dashboardListDao;
    private BlockedListDao blockedListDao;

    public static ProfileRepository getInstance(Context context){
        if(instance == null){
            instance = new ProfileRepository(context);
        }
        return instance;
    }

    private ProfileRepository(Context context) {
        dashboardListDao = InterestedListDatabase.getInstance(context).getDashboardListDao();
        blockedListDao = BlockedListDatabase.getInstance(context).getListDao();
    }

    public Call<ResponseBody> contactUs(String mobile, String email, String message,String name ){

        return  ServiceGenerator.getProfileApi().contactUs(
                mobile,email, message,name
        );

    }

    public Call<ResponseBody> feedback(String mobile, String email, String message,String name ){

        return  ServiceGenerator.getProfileApi().feedback(
                mobile,email, message,name
        );

    }







    public LiveData<Resource<List<DashboardList>>> getInterestedListApi(final String user_id , final int pageNumber, final String search_name, final String latitude, final String longitude){
        return new NetworkBoundResource<List<DashboardList>, DashboardResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull DashboardResponse item) {

                if(item.getDashboard() != null){ //  list will be null if api key is expired
                    DashboardList[] recipes = new DashboardList[item.getDashboard().size()];

                    int index = 0;
                    for(long rowId: dashboardListDao.insertRecipes((DashboardList[])(item.getDashboard().toArray(recipes)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            dashboardListDao.updateList(
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
            public boolean shouldFetch(@Nullable List<DashboardList> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<DashboardList>> loadFromDb() {
                return dashboardListDao.searchList(search_name, pageNumber);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<DashboardResponse>> createCall() {
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
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title){

        return  ServiceGenerator.getDashboardApi().interest(
                unique_id,
                user_id,
                to_user_id,
                full_name,
                listing_id,
                type,
                listing_title
        );

    }


    public LiveData<Resource<List<BlockedPeople>>> getBlokedListApi(final String user_id , final int pageNumber,final String query){
        return new NetworkBoundResource<List<BlockedPeople>, BlockedPeopleResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull BlockedPeopleResponse item) {

                if(item.getBlocked() != null){ //  list will be null if api key is expired
                    BlockedPeople[] recipes = new BlockedPeople[item.getBlocked().size()];

                    int index = 0;
                    for(long rowId: blockedListDao.insertRecipes((BlockedPeople[])(item.getBlocked().toArray(recipes)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            blockedListDao.updateList(
                                    recipes[index].getId(),
                                    recipes[index].getBlock_record_id(),
                                    recipes[index].getName(),
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
                return blockedListDao.searchList(query,pageNumber);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<BlockedPeopleResponse>> createCall() {
                return ServiceGenerator.getProfileApi().getBlocked(
                        URLs.unique_id,
                        user_id,
                        String.valueOf(pageNumber)
                );
            }

        }.getAsLiveData();
    }


}
