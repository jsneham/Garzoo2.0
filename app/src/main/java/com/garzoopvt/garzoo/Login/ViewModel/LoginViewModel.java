package com.garzoopvt.garzoo.Login.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.garzoopvt.garzoo.Login.data.LoginRepository;
import com.garzoopvt.garzoo.Login.data.LoginResult;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class LoginViewModel extends AndroidViewModel {


    private LoginRepository loginRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        loginRepository = LoginRepository.getInstance(application);
    }


    public Call<LoginResult> login(String mobileNo,String latitude, String longitude) {
      return  loginRepository.login(mobileNo,latitude, longitude);
    }


    public Call<ResponseBody> register(String user_id, String fname, String lname,
                                       String gender, String age,
                                       String state, String area,
                                       String taluka, String district,
                                       String latitude, String longitude) {
        return  loginRepository.register(user_id, fname,lname,gender,age,state,area, taluka,district,
                latitude, longitude);
    }

}