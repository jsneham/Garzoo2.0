package com.garzoopvt.garzoo.Splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.garzoopvt.garzoo.BaseActivity;
import com.garzoopvt.garzoo.Home.HomeActivity;
import com.garzoopvt.garzoo.R;

public class SplashActivity extends BaseActivity {

    // SessionManager sessionManager;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
                //check();
                // This method will be executed once the timer is over

            }
        }, 500);
    }

//    private void check() {
//        sessionManager = new SessionManager(getApplicationContext());
//        if (sessionManager.isLoggedIn()) {
//            goToHome();
//        } else {
//            Intent i = new Intent(SplashActivity.this, LoginActivityold2.class);
//            startActivity(i);
//            finish();
//
//        }
//    }
//
//    private void goToHome() {
//        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
//        String user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
//        String username = sessionManager.getFromSessionManager(SessionManager.USERNAME);
//        i.putExtra("user_id", user_id);
//        i.putExtra("username", username);
//        startActivity(i);
//        sessionManager = null;
//        finish();
//    }
}