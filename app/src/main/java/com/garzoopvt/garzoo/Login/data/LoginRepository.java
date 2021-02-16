package com.garzoopvt.garzoo.Login.data;

import android.content.Context;


import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.URLs;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    public static LoginRepository getInstance(Context context){
        if(instance == null){
            instance = new LoginRepository(context);
        }
        return instance;
    }

    private LoginRepository(Context context) {

    }

    public Call<LoginResult> login(String mobileno,String latitude, String longitude ){

        return  ServiceGenerator.getLoginApi().login(
                URLs.unique_id,
                mobileno,latitude, longitude
        );

    }


    public Call<ResponseBody> register(String user_id, String fname, String lname,
                                       String gender, String age,
                                       String state, String area,
                                       String taluka, String district,
                                       String latitude, String longitude){

        return  ServiceGenerator.getLoginApi().getRegister(
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
}