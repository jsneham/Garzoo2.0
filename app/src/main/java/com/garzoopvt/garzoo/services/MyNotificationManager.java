package com.garzoopvt.garzoo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.garzoopvt.garzoo.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


/**
 * Created by admin on 25-Jan-17.
 */

public class MyNotificationManager {

    private String TAG = "MyNotificationManager";
    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;

    int launcherIcon = R.drawable.garzoo_tm;

    int smallIcon = R.drawable.garzoo_tm;


    private int getSmallNotificationIcon ( ) {
        boolean useWhiteIcon = ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP );
        return useWhiteIcon ? smallIcon : launcherIcon;
    }

    private Context mCtx;

    public MyNotificationManager(Context mCtx ) {
        this.mCtx = mCtx;


    }

    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    public void showBigNotification (String title, String message, String url, Intent intent ) {
        try {
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity (
                            mCtx,
                            ID_BIG_NOTIFICATION,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            Log.e ( "big_picture_Style", "calleed" );

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle ( );
            bigPictureStyle.setBigContentTitle ( title );
            bigPictureStyle.setSummaryText ( Html.fromHtml ( message ).toString ( ) );
//            bigPictureStyle.bigPicture ( getBitmapFromURL ( url ) );
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder ( mCtx );
            Notification notification;
            notification = mBuilder.setSmallIcon ( launcherIcon ).setTicker ( title ).setWhen ( 0 )
                    .setAutoCancel ( true )
                    .setContentIntent ( resultPendingIntent )
                    .setContentTitle ( title )
                    .setStyle ( bigPictureStyle )
                    .setSmallIcon ( getSmallNotificationIcon ( ) )
                    .setDefaults ( Notification.DEFAULT_SOUND )
                    .setDefaults ( Notification.DEFAULT_VIBRATE )
                    .setLargeIcon ( BitmapFactory.decodeResource ( mCtx.getResources ( ), launcherIcon ) )
                    .setContentText ( message )
                    .build ( );

            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService ( Context.NOTIFICATION_SERVICE );
            notificationManager.notify ( ID_BIG_NOTIFICATION, notification );

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                String NOTIFICATION_CHANNEL_ID = mCtx.getString ( R.string.default_notification_channel_id );
                NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance );
                notificationChannel.enableLights ( true );
                notificationChannel.setLightColor ( Color.RED );
                notificationChannel.enableVibration ( true );
                notificationChannel.setVibrationPattern ( new long[] { 100, 200, 300, 400, 500, 400, 300, 200, 400 } );
                assert notificationManager != null;
                mBuilder.setChannelId ( NOTIFICATION_CHANNEL_ID );
                notificationManager.createNotificationChannel ( notificationChannel );
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Big Notification: " + e.getMessage());
        }

    }





    public void showSmallNotification (String title, String message, Intent intent, Bitmap image , boolean showImge) {

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(mCtx);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = mCtx.getString(R.string.default_notification_channel_id);
//            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(mCtx.NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(channel);
//            builder.setChannelId(channelId);
//        }

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity (
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        Log.e ( "showSmallNotification", "calleed" );
        int m = ( int ) ( ( new Date( ).getTime ( ) / 1000L ) % Integer.MAX_VALUE );
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder ( mCtx );
        Notification notification;
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();

        if(!showImge){
            notification = mBuilder.setSmallIcon ( R.mipmap.ic_launcher ).setTicker ( title )
                    .setWhen ( 0 )
                    .setSmallIcon ( R.mipmap.ic_launcher )
                    .setLargeIcon ( BitmapFactory.decodeResource ( mCtx.getResources ( ), launcherIcon ) )
                    .setAutoCancel ( true )
                    .setContentIntent ( resultPendingIntent )
                    .setDefaults ( Notification.DEFAULT_SOUND )
                    .setContentTitle ( title )
                    .setStyle(bigTextStyle.bigText(message)).setNumber(2)
                    .build ( );
        }
        else {

            notification = mBuilder.setSmallIcon ( R.mipmap.ic_launcher  ).setTicker ( title )
                    .setWhen ( 0 )
                    .setSmallIcon ( R.mipmap.ic_launcher  )
                    .setLargeIcon ( BitmapFactory.decodeResource ( mCtx.getResources ( ), launcherIcon ) )
                    .setAutoCancel ( true )
                    .setContentIntent ( resultPendingIntent )
                    .setDefaults ( Notification.DEFAULT_SOUND )
                    .setContentTitle ( title )
//                    .setStyle ( new NotificationCompat.BigPictureStyle ( ).bigPicture ( image ) )
                    .setContentText ( message ).setNumber(2)
                    .setStyle ( bigPictureStyle.bigPicture ( image ))
                    .build ( );
        }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Log.e ( TAG, "pending intent:" + resultPendingIntent );
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService ( Context.NOTIFICATION_SERVICE );
        notificationManager.notify ( m, notification );


        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String NOTIFICATION_CHANNEL_ID = mCtx.getString ( R.string.default_notification_channel_id );
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance );
            notificationChannel.enableLights ( true );
            notificationChannel.setLightColor ( Color.RED );
            notificationChannel.enableVibration ( true );
            notificationChannel.setVibrationPattern ( new long[] { 100, 200, 300, 400, 500, 400, 300, 200, 400 } );
            assert notificationManager != null;
            mBuilder.setChannelId ( NOTIFICATION_CHANNEL_ID );
            notificationManager.createNotificationChannel ( notificationChannel );
        }
        assert notificationManager != null;
        notificationManager.notify ( m, mBuilder.build ( ) );

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showBigTextStyleNotification (String title, String message, Intent intent ) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity (
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        int m = ( int ) ( ( new Date( ).getTime ( ) / 1000L ) % Integer.MAX_VALUE );
        Notification.Builder mBuilder = new Notification.Builder ( mCtx );

        Log.e ( "showBigTexotification", "calleed" );
        Notification notification;
        notification = mBuilder.setSmallIcon ( launcherIcon ).setTicker ( title ).setWhen ( 0 )
                .setAutoCancel ( true )
                .setContentIntent ( resultPendingIntent )
                .setContentTitle ( title )
                .setContentText ( message )
                .setSmallIcon ( getSmallNotificationIcon ( ) )
                .setLargeIcon ( BitmapFactory.decodeResource ( mCtx.getResources ( ), launcherIcon ) )

                .setStyle ( new Notification.BigTextStyle ( )
                        .bigText ( message ) )
                .setDefaults ( Notification.DEFAULT_SOUND )
                //.notify(ID_SMALL_NOTIFICATION,mBuilder.build());
                .build ( );

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Log.e ( TAG, "pending intent:" + resultPendingIntent );
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService ( Context.NOTIFICATION_SERVICE );
        notificationManager.notify ( m, notification );
//        https://stackoverflow.com/questions/28387602/notification-bar-icon-turns-white-in-android-5-lollipop

    }


    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL (String strURL ) {
        try {
            URL url = new URL( strURL );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection ( );
            connection.setDoInput ( true );
            connection.connect ( );
            InputStream input = connection.getInputStream ( );
            Bitmap myBitmap = BitmapFactory.decodeStream ( input );
            return myBitmap;
        } catch ( IOException e ) {
            e.printStackTrace ( );
            return null;
        }
    }
}
