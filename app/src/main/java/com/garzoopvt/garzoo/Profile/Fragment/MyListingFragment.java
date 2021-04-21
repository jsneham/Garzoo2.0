package com.garzoopvt.garzoo.Profile.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.garzoopvt.garzoo.Business.Activity.DbEditBusinessListingActivity;
import com.garzoopvt.garzoo.BuySell.Activity.DbEditSellListingActivity;
import com.garzoopvt.garzoo.Chat.Activity.ChatRoomListingActivity;
import com.garzoopvt.garzoo.Dashboard.Activity.DashboardInnerActivity;
import com.garzoopvt.garzoo.Dashboard.Adapter.OnDashboardListener;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;

import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDao;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDatabase;
import com.garzoopvt.garzoo.Employement.Activity.DbEditEmpListingActivity;
import com.garzoopvt.garzoo.Profile.Adapter.InterestedAdapter;
import com.garzoopvt.garzoo.Profile.Adapter.MyListingAdapter;
import com.garzoopvt.garzoo.Profile.Adapter.OnMyListListener;
import com.garzoopvt.garzoo.Profile.ViewModel.ProfileViewModel;
import com.garzoopvt.garzoo.Promotion.Activity.DbEditPromoListingActivity;
import com.garzoopvt.garzoo.R;

import com.garzoopvt.garzoo.Rent.Activity.DbEditRentListingActivity;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;

public class MyListingFragment extends Fragment implements OnMyListListener, NativeAdsManager.Listener {

    private ProfileViewModel mViewModel;


    private RecyclerView rvList;
    private Context context;
    private MyListingAdapter mAdapter;


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
    private String type;
    private int page_no = 1;
    public final int ITEM_PER_ADV = 8;


    public MyListingFragment(String type) {
        this.type = type;
        this.search_name = type;
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
        mAdapter = new MyListingAdapter(this, context, mNativeAdsManager, initGlide(), viewPreloader, user_id);
        rvList.setNestedScrollingEnabled(false);
        rvList.setLayoutManager(new LinearLayoutManager(context));


        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!rvList.canScrollVertically(1)) {
                    // search for the next page
                    mViewModel.searchMyListNextPage(user_id, search_name, latitude, longitude, type);

                }
            }
        });

        // autoplayVideoRVWork();
        rvList.setAdapter(mAdapter);
    }


    private void subscribeObservers() {

        mViewModel.getMyList().observe(this, new Observer<Resource<List<DashboardList>>>() {
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
                                //     Toast.makeText(context, listResource.message, Toast.LENGTH_SHORT).show();

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
        mViewModel.getMyListApi(user_id, 1, search_name, latitude, longitude, type);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkInternet(context);
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
    public void onDestroy() {
        mViewModel.cancelSearchRequest(true);
        super.onDestroy();

    }


    @Override
    public void onDeleteClick(int position) {
        DashboardList dl = mAdapter.getSelected(position);
        deletePostCheck(dl, position);

    }

    @Override
    public void onRenewClick(int position) {
        DashboardList dl = mAdapter.getSelected(position);
        mViewModel.renew(dl.getData_type(), dl.getListing_id());
    }


    @Override
    public void onEditClick(int position) {
        if (!(user_id.equals("0") || user_id.isEmpty())) {

            DashboardList dl = mAdapter.getSelected(position);
            openEditPage(dl);
        } else {
            Utils.openLogin(context);
        }
    }

    public void openEditPage(DashboardList productArrayList) {
        String data_type = productArrayList.getData_type();
        String listing_status = productArrayList.getListing_status();
        Intent EditIntent = null;
        switch (data_type) {
            case "E":
                EditIntent = new Intent(context, DbEditEmpListingActivity.class);

                break;
            case "B":
                EditIntent = new Intent(context, DbEditBusinessListingActivity.class);
                break;
            case "P":
                EditIntent = new Intent(context, DbEditPromoListingActivity.class);
                break;
            case "S":
                EditIntent = new Intent(context, DbEditSellListingActivity.class);
                break;
            case "R":
                EditIntent = new Intent(context, DbEditRentListingActivity.class);
                break;
        }

        EditIntent.putExtra("data", productArrayList);
        EditIntent.putExtra("from_page", "mylist");
        context.startActivity(EditIntent);

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


    public void deletePostCheck(DashboardList productArrayList, int position) {
        String data_type = productArrayList.getData_type();
        String listing_status = productArrayList.getListing_status();
        String post_id = productArrayList.getListing_id();
        String master_id = productArrayList.getId();
        switch (data_type) {
            case "E":
                ConfirmationPoup("E", post_id, position,master_id);
                break;
            case "B":
                ConfirmationPoup("B", post_id, position,master_id);
                break;
            case "P":
                ConfirmationPoup("P", post_id, position,master_id);
                break;
            case "S":

                ConfirmationPoup("S", post_id, position,master_id);
                break;
            case "R":
                ConfirmationPoup("R", post_id, position,master_id);

                break;
        }


    }

    public void ConfirmationPoup(String type, String post_id, int position,String master_id) {

        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage(R.string.delete_post)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                deletePost(type, post_id, position,master_id);
                            }

                        }).setNegativeButton(R.string.No, null).show();

        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);

        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);

    }

    private void deletePost(String type, String post_id, int position,String master_id) {
        mViewModel.deletePost(type, post_id,master_id);
        mAdapter.deleteSelected(position);

        DashboardListDao dao = DashboardListDatabase.getInstance(context).getDashboardListDao();
        dao.updateList(master_id,"1");

//        Call<ResponseBody> call = mViewModel.deletePost(type, post_id);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response != null) {
//                    if (response.isSuccessful()) {
//                        try {
//                            String result = response.body().string();
//                            Utils.openSnackBar(result, view);
//                            mAdapter.deleteSelected(position);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
//                Log.e("main", "on error is called and the error is  ----> " + throwable.getMessage());
//
//            }
//        });
    }

}