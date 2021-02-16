package com.garzoopvt.garzoo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    double latitude = 0, longitude = 0;
    LatLng latLngdata = null;
    String area, taluka, lat, lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        longitude = Double.parseDouble(getIntent().getStringExtra("longitude"));

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("area", area);
                i.putExtra("taluka", taluka);
                i.putExtra("lat", lat);
                i.putExtra("lang", lang);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        Location latLng = googleMap.getMyLocation();
        if (latLng != null) {
            latitude = latLng.getLatitude();
            longitude = latLng.getLongitude();
            latLngdata = new LatLng(latitude, longitude);
        } else {
            latLngdata = new LatLng(latitude, longitude);
        }

        mMap.addMarker(new MarkerOptions().position(latLngdata).title("ME"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngdata, 12.0f));
        getAddress(latitude, longitude);
        mMap.setOnMapClickListener(this);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (latLng != null) {
            mMap.clear();
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            latLngdata = new LatLng(latitude, longitude);
        } else {
            latLngdata = new LatLng(latitude, longitude);
        }

        mMap.addMarker(new MarkerOptions().position(latLngdata).title("ME"));
        getAddress(latitude, longitude);


    }


    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        try {
            geocoder = new Geocoder(this, Locale.getDefault());


            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() > 0) {

                taluka = addresses.get(0).getLocality();//taluka
                area = addresses.get(0).getSubLocality(); //village, city, area
                lat = String.valueOf(latitude);
                lang = String.valueOf(longitude);


            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }


    @Override
    public void onBackPressed() {
//        Intent i = new Intent();
//        i.putExtra("area", area);
//        i.putExtra("taluka", taluka);
//        i.putExtra("lat", lat);
//        i.putExtra("lang", lang);
//        setResult(RESULT_OK, i);
        super.onBackPressed();
    }
}