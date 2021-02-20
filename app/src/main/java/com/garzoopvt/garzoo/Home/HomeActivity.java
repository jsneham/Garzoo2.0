package com.garzoopvt.garzoo.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSettings;
import com.garzoopvt.garzoo.BaseActivity;
import com.garzoopvt.garzoo.BuildConfig;
import com.garzoopvt.garzoo.Chat.Fragment.ChatMainFragment;
import com.garzoopvt.garzoo.Dashboard.Fragment.DashboardFragment;
import com.garzoopvt.garzoo.Notification.Fragment.NotificationFragment;
import com.garzoopvt.garzoo.Profile.Fragment.ProfileFragment;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private SessionManager sessionManager;
    private Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sessionManager=new SessionManager(getApplicationContext());
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // registerDeviceToken();
        createChannelId();
        handleIntent();
        if (BuildConfig.DEBUG) {
            AdSettings.setTestMode(true);
        }


        getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();
    }



    private void handleIntent() {
        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]
    }

    private void createChannelId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
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