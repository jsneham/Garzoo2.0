package com.garzoopvt.garzoo.RetrofitService;



import com.garzoopvt.garzoo.Business.Services.BusinessApi;
import com.garzoopvt.garzoo.BuySell.Services.BuyApi;
import com.garzoopvt.garzoo.Chat.Services.ChatApi;
import com.garzoopvt.garzoo.Dashboard.Services.DashboardApi;
import com.garzoopvt.garzoo.Employement.Services.EmploymentApi;
import com.garzoopvt.garzoo.Login.Services.LoginApi;
import com.garzoopvt.garzoo.Notification.Services.NotificationApi;
import com.garzoopvt.garzoo.Profile.Services.ProfileApi;
import com.garzoopvt.garzoo.Promotion.Services.PromotionApi;
import com.garzoopvt.garzoo.Rent.Services.RentApi;
import com.garzoopvt.garzoo.Util.URLs;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.garzoopvt.garzoo.Util.Constants.CONNECTION_TIMEOUT;
import static com.garzoopvt.garzoo.Util.Constants.READ_TIMEOUT;
import static com.garzoopvt.garzoo.Util.Constants.WRITE_TIMEOUT;


public class ServiceGenerator {


    private static OkHttpClient client = new OkHttpClient.Builder()
            // establish connection with server
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte read from server
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte sent to server
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

            .retryOnConnectionFailure(false)

            .build();


    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(URLs.BASE_URL)
//                    .client(client)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();


    private static LoginApi loginApi = retrofit.create(LoginApi.class);
    public static LoginApi getLoginApi(){
        return loginApi;
    }

    private static ProfileApi profileApi = retrofit.create(ProfileApi.class);
    public static ProfileApi getProfileApi(){
        return profileApi;
    }

    private static NotificationApi notificationApi = retrofit.create(NotificationApi.class);
    public static NotificationApi getNotificationApi(){
        return notificationApi;
    }

    private static ChatApi chatApi = retrofit.create(ChatApi.class);
    public static ChatApi getChatApi(){
        return chatApi;
    }


    private static DashboardApi  dashboardApi = retrofit.create(DashboardApi.class);
    public static DashboardApi getDashboardApi(){
        return dashboardApi;
    }

    private static BuyApi buyApi = retrofit.create(BuyApi.class);
    public static BuyApi getBuyApi(){
        return buyApi;
    }



    private static RentApi rentApi = retrofit.create(RentApi.class);
    public static RentApi getRentApi(){
        return rentApi;
    }

    private static EmploymentApi employmentApi = retrofit.create(EmploymentApi.class);
    public static EmploymentApi getEmploymentApi(){
        return employmentApi;
    }


    private static BusinessApi businessApi = retrofit.create(BusinessApi.class);
    public static BusinessApi getBusinessApi(){
        return businessApi;
    }

    private static PromotionApi promotionApi = retrofit.create(PromotionApi.class);
    public static PromotionApi getPromotionApi(){
        return promotionApi;
    }


}
