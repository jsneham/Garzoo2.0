package com.garzoopvt.garzoo.Util;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.garzoopvt.garzoo.Login.Services.LoginApi;
import com.garzoopvt.garzoo.Login.Services.TransaleOutput;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;

public class Transaltion {

    private static LoginApi api;
    private SessionManager sessionManager;

    public static void translate(String text, EditText editText, Context context){
        if(text!=null) {
            SessionManager sessionManager = new SessionManager(context);
            String lang= sessionManager.getFromSessionManager(SessionManager.LANGUAGE);
            api = ServiceGenerator.getLoginApi();
            api.translate(text, lang)
                    .enqueue(new Callback<TransaleOutput>() {
                        @Override
                        public void onResponse(Call<TransaleOutput> call,
                                               retrofit2.Response<TransaleOutput> response) {
                            if (response.isSuccessful()) {
                                try {
                                    String result = response.body().getText();
                                    editText.setText(result);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<TransaleOutput> call, Throwable t) {

                        }
                    });
        }
    }

    public static void translate(String text, TextView editText, Context context){
        SessionManager sessionManager = new SessionManager(context);
        String lang= sessionManager.getFromSessionManager(SessionManager.LANGUAGE);
        api = ServiceGenerator.getLoginApi();
        api.translate(text, lang)
                .enqueue(new Callback<TransaleOutput>() {
                    @Override
                    public void onResponse(Call<TransaleOutput> call,
                                           retrofit2.Response<TransaleOutput> response) {
                        if (response.isSuccessful()) {
                            try {
                                String result = response.body().getText();
                                editText.setText(result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<TransaleOutput> call, Throwable t) {

                    }
                });
    }

    public static void translateListing(String text, EditText editText, String type, Context context){
        if(text!=null) {
            SessionManager sessionManager = new SessionManager(context);
            String lang= sessionManager.getFromSessionManager(SessionManager.LANGUAGE);
            api = ServiceGenerator.getLoginApi();
            api.translate(text, lang)
                    .enqueue(new Callback<TransaleOutput>() {
                        @Override
                        public void onResponse(Call<TransaleOutput> call,
                                               retrofit2.Response<TransaleOutput> response) {
                            if (response.isSuccessful()) {
                                try {
                                    String result = response.body().getText();
                                    editText.setText(result);
                                    if(type.equals("Taluka")){
                                        sessionManager.setToSessionManager(SessionManager.TALUKA_List,result);
                                    }
                                    else{
                                        sessionManager.setToSessionManager(SessionManager.CITY_List,result);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<TransaleOutput> call, Throwable t) {
                            Log.d("TransaleOutput", "onFailure: ");
                        }
                    });
        }
    }




}
