package com.garzoopvt.garzoo.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.garzoopvt.garzoo.Home.HomeActivity;
import com.garzoopvt.garzoo.Login.Activity.LoginActivity;


public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    private String TAG = "SessionManager";

    public static final String APKVERSIONNUMBER = "ApkVersionNumber";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String IS_UPDATE = "IS_UPDATE";
    private static final String IS_LOGIN = "IS_LOGIN";
    public static final String USER_ID = "USER_ID";
    public static final String USERNAME = "USERNAME";
    public static final String GENDER = "GENDER";
    public static final String AGE = "AGE";
    public static final String LOCATION = "LOCATION";
    public static final String LOCATION_final = "LOCATION_final";
    public static final String MOBILE = "MOBILE";
    public static final String OTP_ID = "OTP_ID";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String STATE = "STATE";
    public static final String POSTALCODE = "POSTALCODE";
    public static final String DISTRICT = "DISTRICT";
    public static final String TALUKA = "TALUKA";
    public static final String TALUKA_List = "TALUKA_List";
    public static final String CITY = "CITY";
    public static final String CITY_List = "CITY_List";
    public static final String NOTIFICATION_COUNT = "NOTIFICATION_COUNT";
    public static final String CHAT_COUNT = "CHAT_COUNT";

    public static final String Login_TALUKA = "Login_TALUKA";
    public static final String Login_CITY = "Login_CITY";

    public static final String LATITUDE_FIXED = "LATITUDE";
    public static final String LONGITUDE_FIXED = "LONGITUDE";


    public static final String OTP = "OTP";
    public static final String OTP_STATE = "OTP_STATE"; //0 -visible, 1-hide





    public static SessionManager getInstance(Context cx) {
        return new SessionManager(cx);
    }

    public SessionManager(Context cntx) {
        this.context = cntx;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }


    public void setToSessionManager(String key, String value) {
        editor.putString(key, value);
        editor.commit();

    }

    public String getFromSessionManager(String key) {
        return pref.getString(key, "");
    }


    /**
     * Create login session
     */
    public void setUid(String uid, String loginType) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(USER_ID, uid);
        editor.commit();
        Log.e("session_uid", uid);
    }

    public String getUid() {
        String uid = pref.getString(USER_ID, "N/A");
        Log.e(TAG, "session_uid: " + uid);
        return uid;
    }


    public boolean checkLogin(String mobile ) {
        boolean isLoggedIn = false;
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
          //  Intent i = new Intent(context, LoginActivity.class);
            Intent i = new Intent(context, HomeActivity.class);
            i.putExtra("mobile", mobile);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            isLoggedIn = true;
        }
        return isLoggedIn;
    }


    public void logoutUser(String mobile) {

      //  PushResult.deleteToken(context, getUid());
        editor.clear();
        editor.commit();
        checkLogin(mobile);
        getUid();
    }

    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

}
