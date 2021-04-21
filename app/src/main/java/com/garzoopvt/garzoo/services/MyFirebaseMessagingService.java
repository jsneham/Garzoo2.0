package com.garzoopvt.garzoo.services;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.garzoopvt.garzoo.Home.HomeActivity;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FireBaseMessaging";
    public static String NOTIFICATION_CHANNEL_ID = "com.itw.firebasepushnotificationdemo";
    public static final int NOTIFICATION_ID = 1;
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public static final String INTENT_FILTER_CHAT = "INTENT_FILTER_CHAT";
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);

        SessionManager sessionManager= new SessionManager(getBaseContext());
        String name = sessionManager.getFromSessionManager(SessionManager.USERNAME);
      //  PushResult.sendTokenToServer(getBaseContext(), sessionManager.getUid(), name, token, "imei");

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        if (remoteMessage.getData() != null) {
            try {
                Log.e(TAG, "remoteMessage: " + remoteMessage.getData());
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                JSONObject main = json.getJSONObject("message");
                JSONObject data = main.getJSONObject("data");
                //parsing json data
                String title = data.getString("title");
                String message = data.getString("message");
                String notificationCount = data.getString("notificationCount");
                String imageUrl = data.getString("image_url");
                String click_action = data.getString("click_action");

                showNotification(getBaseContext(), title, message, notificationCount,click_action);
                if(click_action.equals("chat")) {
                    Intent intent = new Intent(INTENT_FILTER_CHAT);
                    intent.putExtra("notification_count", notificationCount);
                    sendBroadcast(intent);
                }
                else if(click_action.equals("chat_group")) {
                    Intent intent = new Intent(INTENT_FILTER_CHAT);
                    intent.putExtra("notification_count", notificationCount);
                    sendBroadcast(intent);
                }
                else{
                    Intent intent = new Intent(INTENT_FILTER);
                    intent.putExtra("notification_count", notificationCount);
                    sendBroadcast(intent);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "remoteMessage: " + remoteMessage.getData());
            }


        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    private void showNotification(Context context, String title, String message, String notificationCount, String click_action) {
        Intent ii;
        ii = new Intent(context, HomeActivity.class);
        ii.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        ii.putExtra("click_action",click_action);
        ii.setAction("actionstring" + System.currentTimeMillis());
        //ii.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(context, 0, ii,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Log.e("Notification", "Created in up to orio OS device");
            notification = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                    .setOngoing(false)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setWhen(System.currentTimeMillis())
                    .setVibrate( new long[] { 100, 200, 300, 400, 500, 400, 300, 200, 400 })
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title).build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,title, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
            notification.number= Integer.parseInt(notificationCount);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }else{
            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setAutoCancel(true)
                    .setContentText(message)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVibrate( new long[] { 100, 200, 300, 400, 500, 400, 300, 200, 400 })
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification.number= Integer.parseInt(notificationCount);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.garzoo_tm : R.mipmap.ic_launcher;
    }
}