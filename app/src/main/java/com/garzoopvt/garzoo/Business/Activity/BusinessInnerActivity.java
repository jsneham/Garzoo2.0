package com.garzoopvt.garzoo.Business.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.garzoopvt.garzoo.Business.Model.Business;

import com.garzoopvt.garzoo.ImageVideoSlider.SliderItemView;
import com.garzoopvt.garzoo.ImageVideoSlider.SliderLayout;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.gms.maps.SupportMapFragment;
import com.squareup.picasso.Picasso;



public class BusinessInnerActivity extends AppCompatActivity implements SliderLayout.SliderInterface {
    SupportMapFragment mapFragment;
    private RelativeLayout rootView;
    private Business business;
    private SessionManager sessionManager;
    private Context context = this;
    private ImageView ivImage, ivCall;
    private Button btnSubmit;
    private TextView tvTitle, tvDescription, tvCategory, tvPrice, type, tvName, tvLocation;
    private String user_id;
    private String[] imageVideo;
    private SliderLayout sliderLayout;

    private double  Lat, Long;
    private double  DestLat, DestLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_buy_inner);
            sessionManager = new SessionManager(context);
            user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
            business = (Business) getIntent().getParcelableExtra("data");
            Lat = Double.parseDouble(sessionManager.getFromSessionManager(SessionManager.LATITUDE));
            Long = Double.parseDouble(sessionManager.getFromSessionManager(SessionManager.LONGITUDE));

            init();
            getToolBar();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.abMain);
       setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setTitle(business.getTitle());


    }

    private void init() {
        rootView = findViewById(R.id.rootView);
        sliderLayout = findViewById(R.id.sliderLayout);
//        rvTabs = (RecyclerView) findViewById(R.id.rvTabs);
        //  ivImage=(ImageView)findViewById(R.id.ivImage);
        ivCall = (ImageView) findViewById(R.id.ivCall);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvName = (TextView) findViewById(R.id.tvName);
        type = (TextView) findViewById(R.id.type);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        btnSubmit =  findViewById(R.id.btnSubmit);

        btnSubmit.setVisibility(View.VISIBLE);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(BusinessInnerActivity.this, PathMapsActivity.class);
                in.putExtra("OrgLat", Lat);
                in.putExtra("OrgLong", Long);
                in.putExtra("DestLat", DestLat);
                in.putExtra("DestLong", DestLong);
               startActivity(in);
            }
        });

        setData();



    }

    private void setData() {
        DestLat = Double.parseDouble(business.getLatitude());
        DestLong = Double.parseDouble(business.getLongitude());
        tvPrice.setVisibility(View.GONE);

//        if (business.getPrice().equals(null)) tvPrice.setVisibility(View.GONE);
//        else tvPrice.setText(String.format("₹ %1$s", business.getPrice()));

        tvName.setText(String.format("%1$s %2$s", business.getFname(), business.getLname()));
        tvTitle.setText(business.getTitle());
        tvDescription.setText(business.getDescription());
        String category = business.getCategory();
       // tvCategory.setText(category);
        type.setText(String.format("%1$s %2$s %3$s", "(", context.getString(R.string.buisness), ")"));
        if (business.getAddress().equals("")) tvLocation.setVisibility(View.GONE);
        else tvLocation.setText(business.getAddress());
        if (business.getMobile_status().equals("1"))
            ivCall.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_call_hide));

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (business.getMobile_status().equals("0")) {
                    String number = business.getMobile();
                    Utils.openCallFunction(number, context);
                }
                Utils.openSnackBar(context.getString(R.string.mobile_not_available), rootView);
            }
        });

        sliderLayout = findViewById(R.id.sliderLayout);
        imageVideo = business.getImages().split(",");
        if (imageVideo.length == 0 || imageVideo[0].equals(""))
            sliderLayout.setVisibility(View.GONE);
        else {
            for (int i = 0; i < imageVideo.length; i++) {
                SliderItemView sliderItemView = new SliderItemView(context);
                sliderItemView.setScaleType(SliderItemView.ScaleType.CenterInside);

                Log.d("imageVideo", URLs.BASE_URL + imageVideo[i]);
                if (!imageVideo[i].contains("video"))
                    sliderItemView.setItem(URLs.BASE_URL + imageVideo[i], 0);
                else
                    sliderItemView.setItem(URLs.BASE_URL + imageVideo[i], 1);


                sliderLayout.addSlider(sliderItemView);
            }
        }

        sliderLayout.setSliderInterface(this::onSliderClicked);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

//            case R.id.action_edit:
//                openEditSheet();
//                break;

            case android.R.id.home:
                onBackPressed();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void openEditSheet() {
//        Intent intent = new Intent(context, EditBuyListingActivity.class);
//        intent.putExtra("data", business);
//        intent.putExtra("filterCategoryArrayList", filterCategoryArrayList);
//        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //   getMenuInflater().inflate(R.menu.edit, menu);
//        final MenuItem action_edit = menu.findItem(R.id.action_edit);
//        if(user_id.equals(business.getUser_id()))
//            action_edit.setVisible(true);
//        else action_edit.setVisible(false);
        return true;
    }

    @Override
    public void onSliderClicked(int idx) {
        openImagePopup(idx);
    }


    public void openImagePopup(int id) {
        if (!imageVideo[id - 1].contains("video")) {
            View view = LayoutInflater.from(context).inflate(R.layout.image_view, null);
            ImageView ivImage = (ImageView) view.findViewById(R.id.ivImage);


            Picasso.with(context).load(URLs.BASE_URL + imageVideo[id - 1]).into(ivImage);


            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setView(view);
            final android.app.AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.setCancelable(true);
            alertDialog.show();
        }


    }



}

