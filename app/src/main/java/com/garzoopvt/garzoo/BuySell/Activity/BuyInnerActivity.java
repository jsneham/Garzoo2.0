package com.garzoopvt.garzoo.BuySell.Activity;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.Chat.Activity.ChatRoomListingActivity;
import com.garzoopvt.garzoo.Dashboard.Activity.DashboardInnerActivity;
import com.garzoopvt.garzoo.Dashboard.Adapter.OnDashboardListener;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.ImageVideoSlider.SliderItemView;
import com.garzoopvt.garzoo.ImageVideoSlider.SliderLayout;
import com.garzoopvt.garzoo.Profile.Adapter.ListingAdapter;
import com.garzoopvt.garzoo.Profile.ViewModel.ProfileViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.ExoPlayerActivity;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;

public class BuyInnerActivity extends AppCompatActivity implements SliderLayout.SliderInterface, OnDashboardListener {
    private RelativeLayout rootView;
    private Buy productArrayList;
    private SessionManager sessionManager;
    private Context context=this;
    private ImageView ivImage,ivCall;
    private TextView tvTitle,tvDescription,tvCategory,tvPrice,type,tvName ,tvLocation, tvMore;
    private String user_id,username;
    private RecyclerView rvTabs;
    private ProfileViewModel mViewModel;
    private RecyclerView rvList;
    private String[] imageVideo;
    private String  image_path;
    private String category_id;
    private SliderLayout sliderLayout;
    private double  Lat, Long;
    private String search_name="";
    private String latitude;
    private String longitude;
    private static final String TAG = "Innerpage";
    private ListingAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_buy_inner);
            sessionManager=new SessionManager(context);
            mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
            user_id=sessionManager.getFromSessionManager(SessionManager.USER_ID);
            username=sessionManager.getFromSessionManager(SessionManager.USERNAME);
            productArrayList= (Buy) getIntent().getParcelableExtra("data");
            Lat = Double.parseDouble(sessionManager.getFromSessionManager(SessionManager.LATITUDE));
            Long = Double.parseDouble(sessionManager.getFromSessionManager(SessionManager.LONGITUDE));
            latitude= String.valueOf(Lat);
            longitude= String.valueOf(Long);
            init();
            getToolBar();
            initRecyclerView();
            getDashboardList(productArrayList.getUser_id(), "S");
            subscribeObservers();
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
        actionBar.setTitle(productArrayList.getTitle());


    }
    private void init() {
        tvMore = findViewById(R.id.tvMore);
        rvList = findViewById(R.id.rvList);
        rootView=findViewById(R.id.rootView);
        sliderLayout = findViewById(R.id.sliderLayout);
//        rvTabs = (RecyclerView) findViewById(R.id.rvTabs);
      //  ivImage=(ImageView)findViewById(R.id.ivImage);
        ivCall=(ImageView)findViewById(R.id.ivCall);
        tvTitle=(TextView)findViewById(R.id.tvTitle);
        tvDescription=(TextView)findViewById(R.id.tvDescription);
        tvCategory=(TextView)findViewById(R.id.tvCategory);
        tvPrice=(TextView)findViewById(R.id.tvPrice);
        tvName = (TextView) findViewById(R.id.tvName);
        type = (TextView) findViewById(R.id.type);
        tvLocation = (TextView) findViewById(R.id.tvLocation);

        setData();





    }

    private void setData() {

        category_id=productArrayList.getCategory_id();
        if (productArrayList.getPrice().equals("0")) tvPrice.setVisibility(View.GONE);
        else tvPrice.setText(String.format("₹ %1$s", productArrayList.getPrice()));
        if (productArrayList.getAddress().equals("")) tvLocation.setVisibility(View.GONE);
        else tvLocation.setText(productArrayList.getAddress());
        tvName.setText(String.format("%1$s %2$s" ,productArrayList.getFname(), productArrayList.getLname()));
        tvTitle.setText(productArrayList.getTitle());
        tvDescription.setText(productArrayList.getDescription());
        String category = productArrayList.getCategory().equals("") ? "other" : productArrayList.getCategory();
        tvCategory.setText(category);
        type.setText(String.format("%1$s", context.getString(R.string.sell_)));

        if(productArrayList.getMobile_status().equals("1"))
            ivCall.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_call_hide));

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productArrayList.getMobile_status().equals("0")) {
                    String number = productArrayList.getMobile();
                    Utils.openCallFunction(number, context);
                }
                Utils.openSnackBar(context.getString(R.string.mobile_not_available),  rootView);
            }
        });

        sliderLayout = findViewById(R.id.sliderLayout);
        imageVideo = productArrayList.getImages().split(",");

        if(imageVideo.length==0 || imageVideo[0].equals("")) sliderLayout.setVisibility(View.GONE);
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
//              openEditSheet();
//                break;

            case android.R.id.home:
                onBackPressed();
                return true;



        }
        return super.onOptionsItemSelected(item);
    }

    private void openEditSheet() {
//        Intent intent= new Intent(context, EditBuyListingActivity.class);
//        intent.putExtra("data" , productArrayList);
//        intent.putExtra("filterCategoryArrayList" , filterCategoryArrayList);
//        startActivity(intent);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.edit, menu);
//        final MenuItem action_edit = menu.findItem(R.id.action_edit);
//        if(user_id.equals(productArrayList.getUser_id()))
//            action_edit.setVisible(true);
//        else action_edit.setVisible(false);
        return true;
    }

    @Override
    public void onSliderClicked(int idx) {
        image_path = imageVideo[idx - 1];
        openImagePopup(image_path);
    }


    public void openImagePopup(String image_path) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_view, null);
        //if (!imageVideo[id-1].contains("video")) {
        if (!image_path.contains("video")) {
            ImageView ivImage = (ImageView) view.findViewById(R.id.ivImage);
            Button ivDownload = (Button) view.findViewById(R.id.ivDownload);
            Button ivShare = (Button) view.findViewById(R.id.ivShare);

            // Picasso.get().load(URLs.BASE_URL + image_path).into(ivImage);
            Glide.with(context).load(URLs.BASE_URL + image_path).into(ivImage);

            ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isStoragePermissionGranted()) {
                        try {
                            new SaveFile().execute(URLs.IMAGE_URL + image_path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.shareIntent(context, URLs.IMAGE_URL + image_path);
                }
            });

            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setView(view);
            final android.app.AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.setCancelable(true);
            alertDialog.show();
        }


    }

    @Override
    public void onCallClick(int position) {

        if(!(user_id.equals("0")|| user_id.isEmpty())) {
            DashboardList dl = mAdapter.getSelected(position);
            if (!dl.getUser_id().equals(user_id)) {
                if (dl.getMobile_status().equals("0")) {
                    String number = dl.getMobile();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + number));
                    startActivity(intent);
                } else Utils.openSnackBar(context.getString(R.string.mobile_not_available), rootView);
            }
        }
        else{
            Utils.openLogin(context);
        }
    }

    @Override
    public void onChatClick(int position) {
        if (!(user_id.equals("0") || user_id.isEmpty())) {

            DashboardList dl = mAdapter.getSelected(position);
            if (!dl.getUser_id().equals(user_id)) {

                Intent intent = new Intent(context, ChatRoomListingActivity.class);
                intent.putExtra("record_id", dl.getListing_id());
                intent.putExtra("tuid", dl.getUser_id());
                intent.putExtra("phone_no", dl.getMobile());
                intent.putExtra("to_name", dl.getFname() + " " + dl.getLname());
                startActivity(intent);
            }

        } else {
            Utils.openLogin(context);
        }
    }

    @Override
    public void onShareClick(int position) {
        Utils.shareIntent(context);
    }

    @Override
    public void onLikeClick(int position, Button ivInterested) {
        if(!(user_id.equals("0")|| user_id.isEmpty())) {

            DashboardList dl = mAdapter.getSelected(position);
            if (!dl.getUser_id().equals(user_id)) {
                if (dl.getInterest_status().equalsIgnoreCase("yes")) {
                    ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_thumb_up_24, 0, 0, 0);
                   // dl.setInterest_status("no");
                } else {
                    ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_24, 0, 0, 0);
                   // dl.setInterest_status("yes");
                }

                interest(dl.getListing_id(), dl.getUser_id(), dl.getData_type(), dl.getTitle());
            }
        }
        else{
            Utils.openLogin(context);
        }
    }

    private void interest(String id, String to_user_id, String data_type, String title) {


        RequestBody unique_id = RequestBody.create(MultipartBody.FORM, URLs.unique_id);
        RequestBody rb_user_id = RequestBody.create(MultipartBody.FORM, user_id);
        RequestBody rb_to_user_id = RequestBody.create(MultipartBody.FORM, to_user_id);
        RequestBody rb_full_name = RequestBody.create(MultipartBody.FORM, username);
        RequestBody rb_listing_id = RequestBody.create(MultipartBody.FORM, id);
        RequestBody rb_type = RequestBody.create(MultipartBody.FORM, data_type);
        RequestBody rb_listing_title = RequestBody.create(MultipartBody.FORM, title);
        RequestBody rb_type_listing = RequestBody.create(MultipartBody.FORM, "S");
        RequestBody language = RequestBody.create(MultipartBody.FORM, sessionManager.getFromSessionManager(SessionManager.LANGUAGE));
        //        mViewModel.interest(unique_id, rb_user_id, rb_to_user_id,
//                rb_full_name, rb_listing_id, rb_type, rb_listing_title,rb_type);
//        logActivity(id, to_user_id, user_id, "2");

        Call<ResponseBody> call = mViewModel.interest(unique_id,rb_user_id , rb_to_user_id,
                rb_full_name,rb_listing_id,rb_type,rb_listing_title,rb_type_listing,language);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().string();
                            Utils.openSnackBar(result, rootView);
                            logActivity(id, to_user_id, user_id, "2");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("main", "on error is called and the error is  ----> " + throwable.getMessage());

            }
        });
    }


    private void logActivity(String post_id, String post_user_id, String userId, String type) {
        mViewModel.logActivity(post_id,post_user_id,userId,type);
    }

    @Override
    public void onEditClick(int position, View view) {

    }

    @Override
    public void onItemClick(int position) {
        DashboardList dl = mAdapter.getSelected(position);
        logActivity(dl.getListing_id(), dl.getUser_id(), user_id, "4");
        Intent intent = new Intent(context, DashboardInnerActivity.class);
        intent.putExtra("data", dl);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        mViewModel.cancelSearchRequest(true);
        super.onDestroy();
    }

    private void initRecyclerView() {
        ViewPreloadSizeProvider<String> viewPreloader=new ViewPreloadSizeProvider<>();
        mAdapter = new ListingAdapter(this, context, initGlide(), viewPreloader,user_id, productArrayList.getTitle());
        rvList.setNestedScrollingEnabled(false);
        rvList.setLayoutManager(new LinearLayoutManager(context));



        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!rvList.canScrollVertically(1)){
                    // search for the next page
                    mViewModel.searchMyListNextPage(productArrayList.getUser_id(), search_name, latitude,longitude,"S");

                }
            }
        });

        // autoplayVideoRVWork();
        rvList.setAdapter(mAdapter);
    }



    private void subscribeObservers(){

        mViewModel.getUserMoreList().observe(this, new Observer<Resource<List<DashboardList>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<DashboardList>> listResource) {
                if(listResource != null){
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    if(listResource.data != null){
                        // Testing.printRecipess("data: ", listResource.data);

                        switch (listResource.status) {
                            case LOADING: {
                                if(mViewModel.getPageNumber() > 1){
                                    mAdapter.displayLoading();
                                }
                                else{
                                    mAdapter.displayOnlyLoading();
                                }
                                break;
                            }
                            case SUCCESS: {
                                Log.d(TAG, "onChanged: cache has been refreshed.");
                                Log.d(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                if(listResource.data.size()==1) tvMore.setVisibility(View.GONE);
                                else mAdapter.setList(listResource.data);
                                break;
                            }
                            case ERROR: {
                                Log.e(TAG, "onChanged: cannot refresh cache.");
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message );
                                Log.e(TAG, "onChanged: status: ERROR, #Recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setList(listResource.data);
                                //    Toast.makeText(context, listResource.message, Toast.LENGTH_SHORT).show();

                                if(listResource.message.equals(QUERY_EXHAUSTED)){
                                    mAdapter.setQueryExhausted();
                                }
                                break;
                            }
                        }
                    }


                }
            }

        });




//        mViewModel.getDashboard().observe(this, new Observer<List<DashboardList>>() {
//            @Override
//            public void onChanged(@Nullable List<DashboardList> list) {
//                if(list != null){
//                    //mViewModel.setIsPerformingQuery(true);
//                    mAdapter.setList(list);
//                }
//
//            }
//        });
    }

    private void getDashboardList(String user_id, String type){
        mViewModel.getUserMoreListApi(user_id, 1, search_name, latitude,longitude,type );
    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background);

        return Glide.with(this).setDefaultRequestOptions(options);
    }

    @Override
    public void onVideoClick(int position) {
        DashboardList dl = mAdapter.getSelected(position);
        openVideoActivity(context, dl);
    }

    private void openVideoActivity(Context context, DashboardList productArrayList) {
        Intent mIntent = ExoPlayerActivity.getStartIntent(context, productArrayList.getVideo());
        mIntent.putExtra("data", productArrayList.getVideo());
        mIntent.putExtra("listing_status", productArrayList.getListing_status());
        mIntent.putExtra("data_type", productArrayList.getData_type());
        mIntent.putExtra("emp_status", productArrayList.getEmp_status());
        mIntent.putExtra("pd_status", productArrayList.getPd_status());
        mIntent.putExtra("getCategory_id", productArrayList.getCategory_id());
        mIntent.putExtra("username", productArrayList.getFname() + " " + productArrayList.getLname());
        mIntent.putExtra("location", productArrayList.getAddress());
        mIntent.putExtra("description", productArrayList.getDescription());
        mIntent.putExtra("title", productArrayList.getTitle());
        mIntent.putExtra("price", productArrayList.getPrice());
        mIntent.putExtra("timestamp", Utils.formateDate(productArrayList.getDt()));


//        VideoViewPlay.openVideo(context, list.getVideo(),list.getListing_status(),list.getData_type(),list.getPd_status(),list.getCategory_id(),
//                list.getFname() + " " + list.getLname(),list.getAddress(),list.getDescription(),
//                list.getTitle(),list.getPrice(),Utils.formateDate(list.getDt()), list.getEmp_status());
        context.startActivity(mIntent);
    }


    class SaveFile extends AsyncTask<String, Integer, String> {

        ProgressDialog progressDialog;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
//        progressDialog = new ProgressDialog(getC);
//        progressDialog.setMessage("Please wait");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            try {
                File mydir = new File(context.getExternalFilesDir("") + "/Garzoo");
                if (!mydir.exists()) {
                    mydir.mkdirs();
                }

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri downloadUri = Uri.parse(url[0]);
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                SimpleDateFormat dateFormat = new SimpleDateFormat("mmddyyyyhhmmss");
                String date = dateFormat.format(new Date());

                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle("Downloading")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, date + ".jpg");

                manager.enqueue(request);
                return mydir.getAbsolutePath() + File.separator + date + ".jpg";
            } catch (Exception e) {
                e.printStackTrace();
                return "failed";
            }
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            //  progressDialog.dismiss();
            Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show();
        }


    }

    //    ---------------------------------Permission--------------------

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                    new SaveFile().execute(URLs.IMAGE_URL + image_path);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    //--------------------------------Permission End--------------------------


}