package com.garzoopvt.garzoo.Chat.Receiver;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.garzoopvt.garzoo.Home.HomeActivity;
import com.garzoopvt.garzoo.R;

import com.garzoopvt.garzoo.Util.URLs;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;


public class GCMPushReceiverService extends GcmListenerService {

    public static String NOTIFICATION_CHANNEL_ID = "555";
    public static final int NOTIFICATION_ID = 2;


    @Override
    public void onMessageReceived(String from, Bundle data) {

//        String message = data.getString("message");
//        String title = data.getString("title");
//      //  String id = data.getString("id");
//        sendNotification(message, title, "id");

        try {
            JSONObject json = new JSONObject(data.getString("message"));
            // JSONObject main = json.getJSONObject("message");
            JSONObject detail = json.getJSONObject("data");
            String message = detail.getString("message");
            String title = detail.getString("title");
            String notificationCount = detail.getString("notificationCount");
            String click_action = detail.getString("click_action");
            if (click_action.equals("chat_group")) {
                sendNotificationGroup(message, title, notificationCount);
            } else {
                sendNotification(message, title, notificationCount);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String message, String title, String notificationCount) {
        //Creating a broadcast intent
        Intent pushNotification = new Intent(URLs.PUSH_NOTIFICATION);
        //Adding notification data to the intent
        pushNotification.putExtra("message", message);
        pushNotification.putExtra("name", title);
        pushNotification.putExtra("notificationCount", notificationCount);

        //We will create this class to handle notifications
        NotificationHandler notificationHandler = new NotificationHandler(getApplicationContext());

        //If the app is in foreground
        if (!NotificationHandler.isAppIsInBackground(getApplicationContext())) {
            //Sending a broadcast to the chatroom to add the new message
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        } else {
            //If app is in foreground displaying push notification
            //  notificationHandler.showNotificationMessage(title, message);
            //  showNotification(getApplicationContext(),title,message,notificationCount );

        }
    }

    private void sendNotificationGroup(String message, String title, String notificationCount) {
        //Creating a broadcast intent
        Intent pushNotification = new Intent(URLs.PUSH_NOTIFICATION_GROUP);
        //Adding notification data to the intent
        pushNotification.putExtra("message", message);
        pushNotification.putExtra("name", title);
        pushNotification.putExtra("notificationCount", notificationCount);

        //We will create this class to handle notifications
        NotificationHandler notificationHandler = new NotificationHandler(getApplicationContext());

        //If the app is in foreground
        if (!NotificationHandler.isAppIsInBackground(getApplicationContext())) {
            //Sending a broadcast to the chatroom to add the new message
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        } else {
            //If app is in foreground displaying push notification
            //  notificationHandler.showNotificationMessage(title, message);
            //  showNotification(getApplicationContext(),title,message,notificationCount );

        }
    }


    private void showNotification(Context context, String title, String message, String notificationCount) {

        Intent ii;
        ii = new Intent(context, HomeActivity.class);
        ii.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        ii.setAction("actionstring" + System.currentTimeMillis());
        //ii.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Log.e("Notification", "Created in up to orio OS device");
            notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(false)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setWhen(System.currentTimeMillis())
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title).build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, title, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
            notification.number = Integer.parseInt(notificationCount);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setAutoCancel(true)
                    .setContentText(message)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification.number = Integer.parseInt(notificationCount);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.garzoo_tm : R.mipmap.ic_launcher;
    }
}