package com.garzoopvt.garzoo.Profile.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.garzoopvt.garzoo.Chat.Activity.ChatRoomListingActivity;
import com.garzoopvt.garzoo.Dashboard.Activity.DashboardInnerActivity;
import com.garzoopvt.garzoo.Dashboard.Adapter.OnDashboardListener;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Profile.Adapter.InterestedAdapter;
import com.garzoopvt.garzoo.Profile.ViewModel.ProfileViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.ExoPlayerActivity;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;

public class InterestedFragment extends Fragment implements OnDashboardListener, NativeAdsManager.Listener {

    private ProfileViewModel mViewModel;


    private RecyclerView rvList;
    private Context context;
    private InterestedAdapter mAdapter;


    private SessionManager sessionManager;
    private View view;

    private RelativeLayout body;
    private FrameLayout noConnectionLayout;
    private Button btnRetry;


    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 100;
    private int currentPage = PAGE_START;
    private static final String TAG = "MyListingFragment";
    private LinearLayout llNodata;


    private NativeAdsManager mNativeAdsManager;
    private String user_id = "0";
    private String username = "";
    private String search_name = "";
    private String latitude;
    private String longitude;
    private int page_no = 1;
    public final int ITEM_PER_ADV = 8;

    public InterestedFragment() {
        // this.setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_interested, container, false);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        init();
        fbNativeAds();
        initRecyclerView();
        subscribeObservers();
        checkInternet(context);
        return view;
    }

    private void init() {
        context = getContext();
        rvList = view.findViewById(R.id.rv_main);
        sessionManager = new SessionManager(context);
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        username = sessionManager.getFromSessionManager(SessionManager.USERNAME);
        latitude = sessionManager.getFromSessionManager(SessionManager.LATITUDE);
        longitude = sessionManager.getFromSessionManager(SessionManager.LONGITUDE);

        llNodata = view.findViewById(R.id.llNodata);
        body = view.findViewById(R.id.body);
        noConnectionLayout = view.findViewById(R.id.fl_retry_internet);
        btnRetry = view.findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternet(context);
            }
        });


    }

    private void fbNativeAds() {
        String placement_id = context.getString(R.string.fb_placement_id);
        mNativeAdsManager = new NativeAdsManager(getActivity(), placement_id, ITEM_PER_ADV);
        mNativeAdsManager.loadAds();
        mNativeAdsManager.setListener(this);
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background);

        return Glide.with(this).setDefaultRequestOptions(options);
    }


    private void initRecyclerView() {
        ViewPreloadSizeProvider<String> viewPreloader = new ViewPreloadSizeProvider<>();
        mAdapter = new InterestedAdapter(this, context, mNativeAdsManager, initGlide(), viewPreloader, user_id);
        rvList.setNestedScrollingEnabled(false);
        rvList.setLayoutManager(new LinearLayoutManager(context));


        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!rvList.canScrollVertically(1)) {
                    // search for the next page
                    mViewModel.searchNextPage(user_id, search_name, latitude, longitude);

                }
            }
        });

        // autoplayVideoRVWork();
        rvList.setAdapter(mAdapter);
    }

    private void autoplayVideoRVWork() {
//        //todo before setAdapter
//        rvList.setActivity(getActivity());
//        //optional - to play only first visible video
//        rvList.setPlayOnlyFirstVideo(true); // false by default
//        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
//        rvList.setCheckForMp4(false); //true by default
//        rvList.setDownloadVideos(false); // false by default

    }


    private void subscribeObservers() {

        mViewModel.getInterested().observe(this, new Observer<Resource<List<DashboardList>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<DashboardList>> listResource) {
                if (listResource != null) {
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    if (listResource.data != null) {
                        // Testing.printRecipess("data: ", listResource.data);

                        switch (listResource.status) {
                            case LOADING: {
                                if (mViewModel.getPageNumber() > 1) {
                                    mAdapter.displayLoading();
                                } else {
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
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message);
                                Log.e(TAG, "onChanged: status: ERROR, #Recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setList(listResource.data);
                                //      Toast.makeText(context, listResource.message, Toast.LENGTH_SHORT).show();

                                if (listResource.message.equals(QUERY_EXHAUSTED)) {
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

    private void getDashboardList() {
        mViewModel.getInterestedListApi(user_id, 1, search_name, latitude, longitude);
    }


    @Override
    public void onDestroy() {
        mViewModel.cancelSearchRequest(true);
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private void checkInternet(Context context) {
        if (Utils.isConnectedToInternet(context)) {
            Utils.connectionAvailable(body, noConnectionLayout);
            getDashboardList();

        } else {
            Utils.showToast(context, "Please Enabled internet");
            Utils.connectionOut(body, noConnectionLayout);
        }
    }


    @Override
    public void onCallClick(int position) {

        if (!(user_id.equals("0") || user_id.isEmpty())) {
            DashboardList dl = mAdapter.getSelected(position);
            if (!dl.getUser_id().equals(user_id)) {
                if (dl.getMobile_status().equals("0")) {
                    String number = dl.getMobile();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + number));
                    startActivity(intent);
                } else Utils.openSnackBar(context.getString(R.string.mobile_not_available), view);
            }
        } else {
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
        if (!(user_id.equals("0") || user_id.isEmpty())) {

            DashboardList dl = mAdapter.getSelected(position);
            if (!dl.getUser_id().equals(user_id)) {
                if (dl.getInterest_status().equalsIgnoreCase("yes")) {
                    ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_thumb_up_24, 0, 0, 0);
                    dl.setInterest_status("no");
                } else {
                    ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_24, 0, 0, 0);
                    dl.setInterest_status("yes");
                }

                interest(dl.getListing_id(), dl.getUser_id(), dl.getData_type(), dl.getTitle(), position, dl.getInterest_status());
            }
        } else {
            Utils.openLogin(context);
        }
    }

    private void interest(String id, String to_user_id, String data_type, String title, int position, String interest_status) {


        RequestBody unique_id = RequestBody.create(MultipartBody.FORM, URLs.unique_id);
        RequestBody rb_user_id = RequestBody.create(MultipartBody.FORM, user_id);
        RequestBody rb_to_user_id = RequestBody.create(MultipartBody.FORM, to_user_id);
        RequestBody rb_full_name = RequestBody.create(MultipartBody.FORM, username);
        RequestBody rb_listing_id = RequestBody.create(MultipartBody.FORM, id);
        RequestBody rb_type = RequestBody.create(MultipartBody.FORM, data_type);
        RequestBody rb_listing_title = RequestBody.create(MultipartBody.FORM, title);
        RequestBody rb_type_listing = RequestBody.create(MultipartBody.FORM, "D");
        RequestBody language = RequestBody.create(MultipartBody.FORM, sessionManager.getFromSessionManager(SessionManager.LANGUAGE));
//        mViewModel.interest1(unique_id,rb_user_id , rb_to_user_id,
//                rb_full_name,rb_listing_id,rb_type,rb_listing_title,rb_type_listing);
//        logActivity(id, to_user_id, user_id, "2");

        Call<ResponseBody> call = mViewModel.interest(unique_id, rb_user_id, rb_to_user_id,
                rb_full_name, rb_listing_id, rb_type, rb_listing_title, rb_type_listing,language);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().string();

                            mAdapter.deleteSelected(position);
                            mViewModel.addRemoveFromFrav(id, interest_status);

//                            Utils.openSnackBar(getString(R.string.sucess_msg), view);

//                            logActivity(id, to_user_id, user_id, "2");


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
        mViewModel.logActivity(post_id, post_user_id, userId, type);
    }

    @Override
    public void onEditClick(int position, View view) {
        if (!(user_id.equals("0") || user_id.isEmpty())) {

        } else {
            Utils.openLogin(context);
        }
    }

    @Override
    public void onItemClick(int position) {
        DashboardList dl = mAdapter.getSelected(position);
        Intent intent = new Intent(context, DashboardInnerActivity.class);
        intent.putExtra("data", dl);
        startActivity(intent);
    }


    @Override
    public void onAdsLoaded() {

    }

    @Override
    public void onAdError(AdError adError) {

    }

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
}