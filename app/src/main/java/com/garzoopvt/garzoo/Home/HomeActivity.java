package com.garzoopvt.garzoo.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.AdSettings;
import com.garzoopvt.garzoo.BaseActivity;
import com.garzoopvt.garzoo.BuildConfig;
import com.garzoopvt.garzoo.Chat.Fragment.ChatMainFragment;
import com.garzoopvt.garzoo.Dashboard.Fragment.DashboardFragment;
import com.garzoopvt.garzoo.Notification.Fragment.NotificationFragment;
import com.garzoopvt.garzoo.Profile.Fragment.ProfileFragment;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

public class HomeActivity extends BaseActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sessionManager=new SessionManager(getApplicationContext());
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();
        if (BuildConfig.DEBUG) {
            AdSettings.setTestMode(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_home:
//                changeHomeIconColor(0);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashBoardFragment").commit();
                break;
            case R.id.action_notification:
//                changeHomeIconColor(1);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new NotificationFragment()).addToBackStack("NotificationFragment").commit();
                break;
            case R.id.action_chat:
//                changeHomeIconColor(2);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new ChatMainFragment()).addToBackStack("ChatMainFragment").commit();
                break;
            case R.id.action_profile:
//                changeHomeIconColor(3);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new ProfileFragment(), "ProfileFragment").commit();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        finish();
        sessionManager.logoutUser(sessionManager.getFromSessionManager(SessionManager.MOBILE));
    }

}