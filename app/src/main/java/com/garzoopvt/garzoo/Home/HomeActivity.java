package com.garzoopvt.garzoo.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.Database;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.manager.SupportRequestManagerFragment;
import com.facebook.ads.AdSettings;
import com.garzoopvt.garzoo.BaseActivity;
import com.garzoopvt.garzoo.BuildConfig;
import com.garzoopvt.garzoo.Business.Persistence.BusinessDatabase;
import com.garzoopvt.garzoo.BuySell.Persistence.BuyDatabase;
import com.garzoopvt.garzoo.Chat.Fragment.ChatMainFragment;
import com.garzoopvt.garzoo.Dashboard.Fragment.DashboardFragment;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDatabase;
import com.garzoopvt.garzoo.Employement.Model.Employment;
import com.garzoopvt.garzoo.Employement.Persistence.EmploymentDatabase;
import com.garzoopvt.garzoo.Notification.Fragment.NotificationFragment;
import com.garzoopvt.garzoo.Notification.Room.NotificationDatabase;
import com.garzoopvt.garzoo.Profile.Fragment.ProfileFragment;
import com.garzoopvt.garzoo.Profile.Room.BlockedListDatabase;
import com.garzoopvt.garzoo.Profile.Room.InterestedListDatabase;
import com.garzoopvt.garzoo.Profile.Room.MyListDatabase;
import com.garzoopvt.garzoo.Profile.Room.UserMoreDatabase;
import com.garzoopvt.garzoo.Promotion.Persistence.PromotionDatabase;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Rent.Persistence.RentDatabase;
import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
import com.garzoopvt.garzoo.Util.LocaleHelper;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.services.LocationService;
import com.garzoopvt.garzoo.services.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private SessionManager sessionManager;
    private Context context=this;
    private int mNotificationItemCount = 0;
    private int mChatItemCount = 0;
    private TextView itemChatBadgeTextView;
    private TextView itemNotificationBadgeTextView;
    private ImageView iconChatButtonMessages;
    private ImageView iconNotificationButtonMessages;
    private MenuItem action_home, action_notification, action_chat, action_profile;
    boolean isFirstTime = true;
    private   HomeApi api;
    private   String user_id,name,mobile;
    private String mLanguageCode = "en";
    private final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        checkPermissions();

        sessionManager=new SessionManager(getApplicationContext());
        mLanguageCode=   sessionManager.getFromSessionManager(SessionManager.LANGUAGE);
        LocaleHelper.setLocale(this, mLanguageCode);

        name = sessionManager.getFromSessionManager(SessionManager.USERNAME);
        mobile = sessionManager.getFromSessionManager(SessionManager.MOBILE);

        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        if (user_id.isEmpty()) user_id = "0";

        setToolbarClick();
        createChannelId();

        if (BuildConfig.DEBUG) {
            AdSettings.setTestMode(true);
        }



        if(!user_id.equals("0")){
            api = ServiceGenerator.getHomeApi();
            getNotificationCount();
            getChatNotifyCounts();

            registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
            registerReceiver(ChatReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER_CHAT));


        }


      //  getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();

        handleIntent();


    }

    private void setToolbarClick() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();
                action_home.setIcon(R.drawable.ic_twotone_home_24);
                action_profile.setIcon(R.drawable.ic_outline_dehaze_24);
                action_chat.setIcon(R.drawable.ic_outline_chat_bubble_outline_24);
                iconChatButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_chat_bubble_outline_24));
                action_notification.setIcon(R.drawable.ic_outline_notifications_24);
                iconNotificationButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_notifications_24));

            }
        });
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
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String clickAction = "";
            if (extras != null) {
                clickAction = extras.getString("click_action");
                if (clickAction != null) {
                    switch (clickAction) {
                        case "interest":
                            //changeHomeIconColor(1);
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new NotificationFragment(),"NotificationFragment").commit();
                            break;
                        case "chat":
                            // changeHomeIconColor(2);
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new ChatMainFragment(),"ChatFragment").commit();
                            break;
                        default:
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();

                            break;
                    }
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();
                }

            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();
            }
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();
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

        action_home = menu.findItem(R.id.action_home);
        action_chat = menu.findItem(R.id.action_chat);
        action_notification = menu.findItem(R.id.action_notification);
        action_profile = menu.findItem(R.id.action_profile);

        final View notificaitons = menu.findItem(R.id.action_notification).getActionView();
        itemNotificationBadgeTextView = (TextView) notificaitons.findViewById(R.id.badge_textView);
        iconNotificationButtonMessages = (ImageView) notificaitons.findViewById(R.id.badge_icon_button);
        //iconNotificationButtonMessages.setTextColor(getResources().getColor(R.color.black));
        iconNotificationButtonMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeHomeIconColor(1);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new NotificationFragment(), "NotificationFragment").commit();
            }
        });

        setupNotificationBadge();
        final View chat = menu.findItem(R.id.action_chat).getActionView();
        itemChatBadgeTextView = (TextView) chat.findViewById(R.id.badge_textView);
        iconChatButtonMessages = (ImageView) chat.findViewById(R.id.badge_icon_button);
//        iconChatButtonMessages.setTextColor(getResources().getColor(R.color.black));
        iconChatButtonMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeHomeIconColor(2);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new ChatMainFragment(), "ChatFragment").commit();
            }
        });

        setupChatBadge();


        return super.onCreateOptionsMenu(menu);
    }


    private void setupNotificationBadge() {
        itemNotificationBadgeTextView.setText(String.valueOf(mNotificationItemCount));
        if (itemNotificationBadgeTextView != null) {
            if (mNotificationItemCount == 0) {
                if (itemNotificationBadgeTextView.getVisibility() != View.GONE) {
                    itemNotificationBadgeTextView.setVisibility(View.GONE);
                }
            } else {
                itemNotificationBadgeTextView.setText(String.valueOf(Math.min(mNotificationItemCount, 99)));
                if (itemNotificationBadgeTextView.getVisibility() != View.VISIBLE) {
                    itemNotificationBadgeTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setupChatBadge() {
        itemChatBadgeTextView.setText(String.valueOf(mChatItemCount));
        if (itemChatBadgeTextView != null) {
            if (mChatItemCount == 0) {
                if (itemChatBadgeTextView.getVisibility() != View.GONE) {
                    itemChatBadgeTextView.setVisibility(View.GONE);
                }
            } else {
                itemChatBadgeTextView.setText(String.valueOf(Math.min(mChatItemCount, 99)));
                if (itemChatBadgeTextView.getVisibility() != View.VISIBLE) {
                    itemChatBadgeTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_home:
                changeHomeIconColor(0);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();
                break;
//            case R.id.action_notification:
//                changeHomeIconColor(1);
//                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new NotificationFragment()).addToBackStack("NotificationFragment").commit();
//                break;
//            case R.id.action_chat:
//                changeHomeIconColor(2);
//                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new ChatFragment()).addToBackStack("ChatFragment").commit();
//                break;
            case R.id.action_profile:
                changeHomeIconColor(3);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new ProfileFragment(), "ProfileFragment").commit();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void changeHomeIconColor(int icon_name) {
        switch (icon_name) {
            case 0:
                action_home.setIcon(R.drawable.ic_twotone_home_24);
                action_profile.setIcon(R.drawable.ic_outline_dehaze_24);
                action_chat.setIcon(R.drawable.ic_outline_chat_bubble_outline_24);
                iconChatButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_chat_bubble_outline_24));
                action_notification.setIcon(R.drawable.ic_outline_notifications_24);
                iconNotificationButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_notifications_24));
                break;
            case 1:
                action_home.setIcon(R.drawable.ic_outline_home_24);
                action_profile.setIcon(R.drawable.ic_outline_dehaze_24);
                action_chat.setIcon(R.drawable.ic_outline_chat_bubble_outline_24);
                iconChatButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_chat_bubble_outline_24));
                action_notification.setIcon(R.drawable.ic_twotone_notifications_24);
                iconNotificationButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_notifications_24));
                break;
            case 2:
                action_home.setIcon(R.drawable.ic_outline_home_24);
                action_profile.setIcon(R.drawable.ic_outline_dehaze_24);
                action_chat.setIcon(R.drawable.ic_twotone_chat_bubble_24);
                iconChatButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_twotone_chat_bubble_24));
                action_notification.setIcon(R.drawable.ic_outline_notifications_24);
                iconNotificationButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_notifications_24));
                break;
            case 3:
                action_home.setIcon(R.drawable.ic_outline_home_24);
                action_profile.setIcon(R.drawable.ic_twotone_dehaze_24);
                action_chat.setIcon(R.drawable.ic_outline_chat_bubble_outline_24);
                iconChatButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_chat_bubble_outline_24));
                action_notification.setIcon(R.drawable.ic_outline_notifications_24);
                iconNotificationButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_notifications_24));
                break;


        }
    }


    public void logout() {
       clearTables();
        sessionManager.logoutUser(mobile);
        finish();
    }

    private void clearTables() {

        new deleteAllWordsAsyncTask(context).execute();
    }

    private static class deleteAllWordsAsyncTask extends AsyncTask<Void, Void, Void> {

        public Context context;

        deleteAllWordsAsyncTask(Context mcontext) {
            context=mcontext;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            DashboardListDatabase.getInstance(context).getDashboardListDao().deleteAll();
            BuyDatabase.getInstance(context).getListDao().deleteAll();
            RentDatabase.getInstance(context).getListDao().deleteAll();
            EmploymentDatabase.getInstance(context).getListDao().deleteAll();
            BusinessDatabase.getInstance(context).getListDao().deleteAll();
            PromotionDatabase.getInstance(context).getListDao().deleteAll();
            NotificationDatabase.getInstance(context).getListDao().deleteAll();
            BlockedListDatabase.getInstance(context).getListDao().deleteAll();
            InterestedListDatabase.getInstance(context).getDashboardListDao().deleteAll();
            MyListDatabase.getInstance(context).getDashboardListDao().deleteAll();
            UserMoreDatabase.getInstance(context).getDashboardListDao().deleteAll();
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        try {
            FragmentManager fragMan = getSupportFragmentManager();
            DashboardFragment myFragment = (DashboardFragment) fragMan.findFragmentByTag("DashboardFragment");
            if (myFragment != null) {
                boolean myFragXwasVisible = myFragment.isVisible();
                if (myFragXwasVisible) {
                    tellFragments();
                    //  super.onBackPressed();

                } else {
                    changeHomeIconColor(0);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();
                }
            } else {
                changeHomeIconColor(0);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new DashboardFragment(), "DashboardFragment").commit();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


    }


    public void highLightSection() {
        action_home.setIcon(R.drawable.ic_outline_home_24);
        action_profile.setIcon(R.drawable.ic_outline_dehaze_24);
        action_chat.setIcon(R.drawable.ic_outline_chat_bubble_outline_24);
        iconChatButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_chat_bubble_outline_24));
        action_notification.setIcon(R.drawable.ic_outline_notifications_24);
        iconNotificationButtonMessages.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_notifications_24));

    }

    public void UpdateIsFirstTime(boolean value) {
        isFirstTime = value;
    }

    public void callResetNotificationCount() {

        try {
            String count = sessionManager.getFromSessionManager(SessionManager.NOTIFICATION_COUNT);
            mNotificationItemCount = Integer.parseInt(count);
            if (mNotificationItemCount == 0) {
                if (itemNotificationBadgeTextView.getVisibility() != View.GONE) {
                    itemNotificationBadgeTextView.setVisibility(View.GONE);

                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void callResetChatNotificationCount() {
        try {
            String count = sessionManager.getFromSessionManager(SessionManager.CHAT_COUNT);
            mChatItemCount = Integer.parseInt(count);
            if (mChatItemCount == 0) {
                if (itemChatBadgeTextView.getVisibility() != View.GONE) {
                    itemChatBadgeTextView.setVisibility(View.GONE);

                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            String notification_count = b.getString("notification_count");
            updateUi(notification_count);
        }
    };

    private void updateUi(String notification_count) {
        sessionManager.setToSessionManager(SessionManager.NOTIFICATION_COUNT, notification_count);
        itemNotificationBadgeTextView.setText(notification_count);
        if (itemNotificationBadgeTextView != null) {
            itemNotificationBadgeTextView.setText(notification_count);
            if (itemNotificationBadgeTextView.getVisibility() != View.VISIBLE) {
                itemNotificationBadgeTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(!user_id.equals("0")) {
            unregisterReceiver(myReceiver);
            unregisterReceiver(ChatReceiver);
        }
        stopLocationService();

    }


    public void updateChatUi(String notification_count) {
        sessionManager.setToSessionManager(SessionManager.CHAT_COUNT, notification_count);
        if (!notification_count.equals("0")) {
            itemChatBadgeTextView.setText(notification_count);
            if (itemChatBadgeTextView != null) {
                itemChatBadgeTextView.setText(notification_count);
                if (itemChatBadgeTextView.getVisibility() != View.VISIBLE) {
                    itemChatBadgeTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private BroadcastReceiver ChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            String notification_count = b.getString("notification_count");
            updateChatUi(notification_count);
        }
    };






    private void getNotificationCount() {

        api.GetNotificationCount(URLs.unique_id, user_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        Log.d("NotificationCount", result);
                        mNotificationItemCount = Integer.parseInt(result);
                        sessionManager.setToSessionManager(SessionManager.NOTIFICATION_COUNT, String.valueOf(mNotificationItemCount));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private void getChatNotifyCounts() {
        api.getDataList(URLs.unique_id, user_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        mChatItemCount = Integer.parseInt(result);
                        Log.d("result", result);
                        sessionManager.setToSessionManager(SessionManager.CHAT_COUNT, String.valueOf(mChatItemCount));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }


    private void tellFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f != null && f instanceof DashboardFragment) {
                if (isFirstTime) {
                    ((DashboardFragment) f).onBackPressed();
                    isFirstTime = false;
                    break;
                } else
                    super.onBackPressed();
            } else {
                if (f != null && f instanceof SupportRequestManagerFragment) {

                } else
                    super.onBackPressed();
            }
        }
    }





    //-------------------------Location Relate_---------------------------------------------------------------


    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(URLs.ACTION_START_LOCATION_SERVICE);
            startService(intent);
        }
    }


    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(URLs.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(serviceInfo.service.getClassName())) {
                    if (serviceInfo.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }





}