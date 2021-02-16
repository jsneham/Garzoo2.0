package com.garzoopvt.garzoo.Dashboard.Fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.garzoopvt.garzoo.Business.Fragment.BusinessFragment;
import com.garzoopvt.garzoo.BuySell.Activity.BuyInnerActivity;
import com.garzoopvt.garzoo.BuySell.Fragment.BuyFragment;
import com.garzoopvt.garzoo.BuySell.Fragment.SellSubCategoryFragment;
import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.Dashboard.Activity.DashboardInnerActivity;
import com.garzoopvt.garzoo.Dashboard.Adapter.DashboardAdapter;
import com.garzoopvt.garzoo.Dashboard.Adapter.OnDashboardListener;
import com.garzoopvt.garzoo.Dashboard.Adapter.RecycleAdapter_GridHome;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Dashboard.Model.HomeGridModelClass;
import com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel;
import com.garzoopvt.garzoo.Employement.Fragment.EmploymentFragment;
import com.garzoopvt.garzoo.Promotion.Fragment.PromotionFragment;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Rent.Fragment.RentFragment;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.LocaleHelper;
import com.garzoopvt.garzoo.Util.RecyclerTouchListener;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.garzoopvt.garzoo.services.GpsUtils;
import com.garzoopvt.garzoo.services.LocationService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;

public class DashboardFragment extends Fragment implements OnDashboardListener,NativeAdsManager.Listener,com.facebook.ads.AdListener {

    //view
    private View view;
    private Context context;
    private AAH_CustomRecyclerView rvList;
    private RecyclerView rvTabs;
    private NestedScrollView rvNestedScroll;
    private EditText searchView;
    private static final String TAG = "DashboardFragment";
    private SessionManager sessionManager;

    //Location
    private final int PERMISSION_REQUEST_CODE = 200;

    //instances
    private DashboardViewModel mViewModel;
    private DashboardAdapter mAdapter;
    private RecycleAdapter_GridHome mRecycleAdapter_GridHome;
    private List<DashboardList> dashboardLists = new ArrayList<>();
    private NativeAdsManager mNativeAdsManager;
    private @Nullable
    com.facebook.ads.AdView bannerAdView;
    private ArrayList<HomeGridModelClass> homeGridModelClasses= new ArrayList<>();

    //Data
    private String mLanguageCode = "en";
    private String user_id="0";
    private String username="Sneha";
    private String search_name="";
    private String latitude="19.108589";
    private String longitude="72.827072";
    private int page_no=1;
    public final int ITEM_PER_ADV = 8;
    private Integer image[] = {R.drawable.kharedi, R.drawable.vikri, R.drawable.bhade, R.drawable.rojgar, R.drawable.businessv, R.drawable.charcha};
    private Integer name[] = {R.string.buy, R.string.sell, R.string.rent, R.string.employement, R.string.buisness, R.string.promotion};

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.dashboard_fragment, container, false);
        context=getContext();
        mViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        sessionManager =new SessionManager(context);
        getSessionData();
        fbNativeAds();

        checkPermissions();
        CheckGPSIsON();
        initView();
        initRecyclerView();
        subscribeObservers();
        getDashboardList();
        initSearchView();
        getFbAd();


        return view;
    }

    private void getSessionData() {
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        if(user_id.isEmpty()) user_id="0";
        mLanguageCode = sessionManager.getFromSessionManager(SessionManager.LANGUAGE);
        if(mLanguageCode.isEmpty())  updateLanguage();
    }

    private void fbNativeAds() {
        String placement_id = context.getString(R.string.fb_placement_id);
        mNativeAdsManager = new NativeAdsManager(getActivity(), placement_id, ITEM_PER_ADV);
        mNativeAdsManager.loadAds();
        mNativeAdsManager.setListener(this);
    }
    private void initView() {
        rvNestedScroll = view.findViewById(R.id.rvNestedScroll);
        rvList = view.findViewById(R.id.rvList);
        rvTabs = view.findViewById(R.id.rvTabs);
        searchView = view.findViewById(R.id.etSearchBox);
        setData();
        initRecyclerClick();
    }

    private void setData() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvTabs.setLayoutManager(mLayoutManager);
        rvTabs.setItemAnimator(new DefaultItemAnimator());

        homeGridModelClasses.clear();
        homeGridModelClasses = new ArrayList<>();
        for (int i = 0; i < image.length; i++) {
            HomeGridModelClass beanClassForRecyclerView_contacts = new HomeGridModelClass(image[i], name[i]);
            homeGridModelClasses.add(beanClassForRecyclerView_contacts);
        }
        mRecycleAdapter_GridHome = new RecycleAdapter_GridHome(getActivity(), homeGridModelClasses);
        rvTabs.setAdapter(mRecycleAdapter_GridHome);


    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background);

        return Glide.with(this).setDefaultRequestOptions(options);
    }

    private void initRecyclerView() {
        ViewPreloadSizeProvider<String> viewPreloader=new ViewPreloadSizeProvider<>();
        mAdapter = new DashboardAdapter(this, context,mNativeAdsManager, initGlide(), viewPreloader);
        rvList.setNestedScrollingEnabled(false);
        rvList.setLayoutManager(new LinearLayoutManager(context));

//        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>(Glide.with(context), mAdapter, viewPreloader, ITEM_PER_ADV);
//        rvList.addOnScrollListener(preloader);


        rvNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(!v.canScrollVertically(1)){
                    // search for the next page
                    mViewModel.searchNextPage(user_id, search_name, latitude,longitude);

                }
            }
        });

//        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if(!rvList.canScrollVertically(1)){
//                    // search for the next page
//                    mViewModel.searchNextPage(user_id, search_name, latitude,longitude);
//
//                }
//            }
//        });

        autoplayVideoRVWork();
        rvList.setAdapter(mAdapter);
    }

    private void autoplayVideoRVWork() {
        //todo before setAdapter
        rvList.setActivity(getActivity());
        //optional - to play only first visible video
        rvList.setPlayOnlyFirstVideo(true); // false by default
        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        rvList.setCheckForMp4(false); //true by default
        rvList.setDownloadVideos(false); // false by default

    }

    private void initRecyclerClick() {
        rvTabs.addOnItemTouchListener(new RecyclerTouchListener(context,
                rvTabs, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

               // ((HomeActivity) getActivity()).highLightSection();

                switch (position) {
                    case 0:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new BuyFragment()).commit();
                        break;
                    case 1:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new SellSubCategoryFragment()).commit();
                        break;
                    case 2:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new RentFragment()).commit();
                        break;
                    case 3:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new EmploymentFragment()).commit();
                        break;
                    case 4:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new BusinessFragment()).commit();
                        break;
                    case 5:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new PromotionFragment()).commit();
                        break;
                    default:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new BuyFragment()).commit();
                }


            }


            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    private void subscribeObservers(){

        mViewModel.getDashboard().observe(this, new Observer<Resource<List<DashboardList>>>() {
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
                                mAdapter.setList(listResource.data);
                                break;
                            }
                            case ERROR: {
                                Log.e(TAG, "onChanged: cannot refresh cache.");
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message );
                                Log.e(TAG, "onChanged: status: ERROR, #Recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setList(listResource.data);
                                Toast.makeText(context, listResource.message, Toast.LENGTH_SHORT).show();

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

    private void getDashboardList(){
        mViewModel.getDashboardListApi(user_id, 1, search_name, latitude,longitude );
    }

    private void initSearchView(){
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                search_name= editable.toString();
                mViewModel.getDashboardListApi(user_id, 1, search_name, latitude,longitude );
            }
        });
    }



    @Override
    public void onDestroy() {
        mViewModel.cancelSearchRequest(true);
        stopLocationService();
        super.onDestroy();
       // unregisterReceiver(myReceiver);
       // unregisterReceiver(ChatReceiver);


    }

    private void CheckGPSIsON() {

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        final boolean[] gps_enabled = {false};
        boolean network_enabled = false;
        try {
            gps_enabled[0] = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!gps_enabled[0] && !network_enabled) {
                new GpsUtils(context).turnGPSOn(new GpsUtils.onGpsListener() {
                    @Override
                    public void gpsStatus(boolean isGPSEnable) {
                        // turn on GPS
                        gps_enabled[0] = isGPSEnable;
                    }
                });
                startLocationService();
            } else {
                startLocationService();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(context, LocationService.class);
            intent.setAction(URLs.ACTION_START_LOCATION_SERVICE);
            context.startService(intent);
        }
    }


    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(context, LocationService.class);
            intent.setAction(URLs.ACTION_STOP_LOCATION_SERVICE);
            context.startService(intent);
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
    private void checkPermissions() {
        if (!checkPermission()) {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        int result6 = ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(context, CAMERA);
        int result2 = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(context, READ_PHONE_STATE);
        int result5 = ContextCompat.checkSelfPermission(context, ACCESS_NETWORK_STATE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED
                && result6 == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
//
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION,
                CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE, ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean locationAccepted1 = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean phoneAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean stateAccepted = grantResults[5] == PackageManager.PERMISSION_GRANTED;

//                    &&locationAccepted1

                    if (locationAccepted && cameraAccepted && readAccepted && writeAccepted && phoneAccepted && stateAccepted) {
                        // Toast.makeText(this, "Permission Granted, Now you can access location data and camera.", Toast.LENGTH_LONG).show();
//                        getMyLocation();

                    } else {
//                        shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) ||
                        // DenyAndDontASk = false;
                        // Toast.makeText(this, "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) ||
                                    shouldShowRequestPermissionRationale(CAMERA) ||
                                    shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) ||
                                    shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE) ||
                                    shouldShowRequestPermissionRationale(READ_PHONE_STATE) ||
                                    shouldShowRequestPermissionRationale(ACCESS_NETWORK_STATE)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE, ACCESS_NETWORK_STATE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", okListener)
                // .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onAdsLoaded() {
        Log.d(TAG, "onAdsLoaded: ");
    }

    @Override
    public void onAdError(AdError adError) {
        Log.d(TAG, "onError:- " +  adError.getErrorCode());
    }

    private void getFbAd() {
        RelativeLayout bannerAdContainer = (RelativeLayout) view.findViewById(R.id.bannerAdContainer);
        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }


        // Create a banner's ad view with a unique placement ID (generate your own on the Facebook
        // app settings). Use different ID for each ad placement in your app.
        bannerAdView =
                new com.facebook.ads.AdView(
                        this.getActivity(),
                        "390694838957593_416544323039311",
                        com.facebook.ads.AdSize.BANNER_HEIGHT_50);

        // Reposition the ad and add it to the view hierarchy.
        bannerAdContainer.addView(bannerAdView);

        // Initiate a request to load an ad.
        bannerAdView.loadAd(bannerAdView.buildLoadAdConfig().withAdListener(this).build());
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Log.d(TAG, "onError:- " +  adError.getErrorCode());
    }

    @Override
    public void onAdLoaded(Ad ad) {
        Log.d(TAG, "onAdLoaded: ");
    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.d(TAG, "onAdClicked: ");
    }

    @Override
    public void onLoggingImpression(Ad ad) {

        Log.d(TAG, "onLoggingImpression: ");
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
                    context.startActivity(intent);
                } else Utils.openSnackBar(context.getString(R.string.mobile_not_available), view);
            }
        }
        else{
            Utils.openLogin(context);
        }
    }

    @Override
    public void onChatClick(int position) {
        if(!(user_id.equals("0")|| user_id.isEmpty())) {

        }
        else{
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
                    dl.setInterest_status("no");
                } else {
                    ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_24, 0, 0, 0);
                    dl.setInterest_status("yes");
                }

                interest(dl.getId(), dl.getUser_id(), dl.getData_type(), dl.getTitle());
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


        Call<ResponseBody> call = mViewModel.interest(unique_id,rb_user_id , rb_to_user_id,
                rb_full_name,rb_listing_id,rb_type,rb_listing_title);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().string();
                            Utils.openSnackBar(result, view);

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

    @Override
    public void onEditClick(int position) {
        if(!(user_id.equals("0")|| user_id.isEmpty())) {

        }
        else{
            Utils.openLogin(context);
        }
    }

    @Override
    public void onItemClick(int position) {
        DashboardList dl = mAdapter.getSelected(position);
        Intent intent = new Intent(context, DashboardInnerActivity.class);
        intent.putExtra("data", dl);
        context.startActivity(intent);
    }




    public void updateLanguage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.Theme_AppCompat_Light_Dialog);
        builder.setTitle(R.string.lang);


        final String items[] = {"English", "मराठी", "हिंदी"};

        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case 0:
                                mLanguageCode="en";
                                break;
                            case 1:
                                mLanguageCode="mr";
                                break;
                            case 2:
                                mLanguageCode="hi";
                                break;
                        }
                        sessionManager.setToSessionManager(SessionManager.LANGUAGE, mLanguageCode);
                        LocaleHelper.setLocale(getContext(), mLanguageCode);
                        getActivity().recreate();
                        dialog.dismiss();
                    }
                });

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic here
                        // dismiss dialog too
                        sessionManager.setToSessionManager(SessionManager.LANGUAGE, mLanguageCode);
                        dialog.dismiss();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        sessionManager.setToSessionManager(SessionManager.LANGUAGE, mLanguageCode);
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
// display dialog
        dialog.show();


    }


    @Override
    public void onStop() {
        super.onStop();
        //add this code to pause videos (when app is minimised or paused)
        rvList.stopVideos();
    }

    @Override
    public void onResume() {
        super.onResume();
        rvList.playAvailableVideos(0);
    }
}