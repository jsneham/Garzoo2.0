package com.garzoopvt.garzoo.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationService extends Service {

    private LocationCallback locationCallback= new LocationCallback(){

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if(locationResult!=null && locationResult.getLastLocation()!=null){
                double latitude= locationResult.getLastLocation().getLatitude();
                double longitude= locationResult.getLastLocation().getLongitude();

                getAddress(latitude, longitude);
            }
        }
    };



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @SuppressLint("MissingPermission")
    private void startLocationService(){
        String channelId= "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent= new Intent();
        PendingIntent pendingIntent =  PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder= new NotificationCompat.Builder(
                getApplicationContext(),
                channelId

        );
//        builder.setSmallIcon(R.drawable.ic_twotone_home_24);
//        builder.setContentTitle("Start Service");
//        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
//        builder.setContentText("running");
//        builder.setContentIntent(pendingIntent);
//        builder.setAutoCancel(false);
//        builder.setPriority(NotificationCompat.PRIORITY_LOW);


        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            if(notificationManager!=null && notificationManager.getNotificationChannel(channelId)==null){
                NotificationChannel notificationChannel= new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_NONE
                );
                notificationChannel.setDescription("This channel used by Location");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest= new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
        startForeground(URLs.LOCATION_SERVICE_IO, builder.build()) ;
    }


    private void stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
       // stopForeground(true);
        stopForeground(false);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            String action= intent.getAction();
            if(action!=null){
                if(action.equals(URLs.ACTION_START_LOCATION_SERVICE)){
                    startLocationService();
                }
                else if(action.equals(URLs.ACTION_STOP_LOCATION_SERVICE)){
                    stopLocationService();

                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        try {
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if(addresses.size()>0) {

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String taluka = addresses.get(0).getLocality();//taluka
                String state = addresses.get(0).getAdminArea();  //state
                String country = addresses.get(0).getCountryName(); //country
                String postalCode = addresses.get(0).getPostalCode(); //pincode
                String knownName = addresses.get(0).getFeatureName();
                String city = addresses.get(0).getSubLocality(); //village, city, area
                String district = addresses.get(0).getSubAdminArea(); //district


                SessionManager sessionManager = new SessionManager(this);
                sessionManager.setToSessionManager(SessionManager.LOCATION, address);
                sessionManager.setToSessionManager(SessionManager.CITY, city);
                sessionManager.setToSessionManager(SessionManager.STATE, state);
                sessionManager.setToSessionManager(SessionManager.POSTALCODE, postalCode);
                sessionManager.setToSessionManager(SessionManager.DISTRICT, district);
                sessionManager.setToSessionManager(SessionManager.TALUKA, taluka);
                sessionManager.setToSessionManager(SessionManager.LATITUDE, String.valueOf(latitude));
                sessionManager.setToSessionManager(SessionManager.LONGITUDE, String.valueOf(longitude));


                Log.d("lat,long", String.valueOf(latitude) + "," +String.valueOf(longitude));

                stopLocationService();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }





}
