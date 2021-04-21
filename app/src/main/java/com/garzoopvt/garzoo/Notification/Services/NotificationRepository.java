package com.garzoopvt.garzoo.Notification.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.garzoopvt.garzoo.Chat.Persistence.ChatGroupDatabase;
import com.garzoopvt.garzoo.Chat.Persistence.ChatUserDao;
import com.garzoopvt.garzoo.Chat.Persistence.ChatUserDatabase;
import com.garzoopvt.garzoo.Chat.Services.ChatRepository;
import com.garzoopvt.garzoo.Chat.Services.ChatUserResponse;
import com.garzoopvt.garzoo.Notification.Model.Notification;
import com.garzoopvt.garzoo.Notification.Room.NotificationDao;
import com.garzoopvt.garzoo.Notification.Room.NotificationDatabase;
import com.garzoopvt.garzoo.RetrofitService.ApiResponse;
import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
import com.garzoopvt.garzoo.RetrofitService.NetworkBoundResource;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.URLs;


import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.garzoopvt.garzoo.Util.URLs.language;

public class NotificationRepository {

    private static final String TAG = "NotificationRepository";

    private static NotificationRepository instance;

    private NotificationDao notificationDao;

    public static NotificationRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationRepository(context);
        }
        return instance;
    }

    private NotificationRepository(Context context) {
        notificationDao = NotificationDatabase.getInstance(context).getListDao();

    }

    public void cancelRequest(){
//        mDashboardApiClient.cancelRequest();
    }


    public LiveData<Resource<List<Notification>>> getList(final String user_id , final int pageNumber, final String search_name, final String language){
        return new NetworkBoundResource<List<Notification>, NotificationResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull NotificationResponse item) {
                ArrayList<String> ids=new ArrayList<>();
                if(item.getNotification() != null){ //  list will be null if api key is expired
                    Notification[] list = new Notification[item.getNotification().size()];

                    int index = 0;
                    for(long rowId: notificationDao.insertNotification((Notification[])(item.getNotification().toArray(list)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This list is already in cache.");
                            // if already exists, I don't want to set the values  or timestamp b/c they will be erased
                            notificationDao.updateNotification(
                                    list[index].getNotification_id(),
                                    list[index].getTitle(),
                                    list[index].getPhone(),
                                    list[index].getImage(),
                                    list[index].getFrom_name(),
                                    list[index].getDate()

                            );
                        }
                        ids.add(list[index].getNotification_id());
                        index++;
                    }

                    if(ids.size()>0) notificationDao.deleteOldData(ids);
                }

            }

            @Override
            public boolean shouldFetch(@Nullable List<Notification> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Notification>> loadFromDb() {
                return notificationDao.getNotification();
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<NotificationResponse>> createCall() {
                return ServiceGenerator.getNotificationApi().getNotification(
                        URLs.unique_id,
                        user_id,
                        language

                );
            }

        }.getAsLiveData();
    }


    public Call<ResponseBody> resetNotificationCount(final String user_id ){

        return  ServiceGenerator.getNotificationApi().ResetNotificationCount(
                URLs.unique_id,
                user_id
        );

    }

}












