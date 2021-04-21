package com.garzoopvt.garzoo.Profile.Fragment;

import android.content.Context;
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


import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.garzoopvt.garzoo.Business.Persistence.BusinessDao;
import com.garzoopvt.garzoo.Business.Persistence.BusinessDatabase;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;

import com.garzoopvt.garzoo.Profile.Adapter.BlockedUserAdapter;
import com.garzoopvt.garzoo.Profile.Adapter.InterestedAdapter;
import com.garzoopvt.garzoo.Profile.Adapter.OnBlockedListener;
import com.garzoopvt.garzoo.Profile.Model.BlockedPeople;
import com.garzoopvt.garzoo.Profile.Room.BlockedListDao;
import com.garzoopvt.garzoo.Profile.Room.BlockedListDatabase;
import com.garzoopvt.garzoo.Profile.ViewModel.ProfileViewModel;
import com.garzoopvt.garzoo.R;


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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;

public class BlockedListFragment extends Fragment implements OnBlockedListener {


    private ProfileViewModel mViewModel;

    private RecyclerView rvList;
    private Context context;
    private BlockedUserAdapter mAdapter;

    private SessionManager sessionManager;
    private String user_id;
    View view;

    RelativeLayout body;
    FrameLayout noConnectionLayout;
    Button btnRetry;


    LinearLayoutManager layoutManager;



    ProgressBar progressBar;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 100;
    private int currentPage = PAGE_START;
    private static final String TAG = "MyListingFragment";

    LinearLayout llNodata;


    public BlockedListFragment(){
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_interested, container, false);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        init();
        initRecyclerView();
        subscribeObservers();
        return view;
    }

    private void init() {
        context=getContext();


        llNodata = view.findViewById(R.id.llNodata);
        rvList = view.findViewById(R.id.rv_main);
        sessionManager = new SessionManager(context);
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);

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

    private void checkInternet(Context context) {
        if (Utils.isConnectedToInternet(context)) {
            getList();
        } else {
            Utils.showToast(context, "Please Enabled internet");
            Utils.connectionOut(body, noConnectionLayout);
        }
    }

    private void initRecyclerView() {
        mAdapter= new BlockedUserAdapter(context,  user_id, BlockedListFragment.this);
        rvList.setNestedScrollingEnabled(false);
        rvList.setLayoutManager(new LinearLayoutManager(context));



        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!rvList.canScrollVertically(1)){
                    // search for the next page
                    mViewModel.NextBlockedListPage(user_id);

                }
            }
        });


        rvList.setAdapter(mAdapter);
    }



    private void subscribeObservers(){

        mViewModel.getBlocked().observe(this, new Observer<Resource<List<BlockedPeople>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<BlockedPeople>> listResource) {
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
                                //     Toast.makeText(context, listResource.message, Toast.LENGTH_SHORT).show();

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



  }

    private void getList(){
        mViewModel.getBlockedListApi(user_id,1);
    }


    @Override
    public void onStop() {
        super.onStop();
        //add this code to pause videos (when app is minimised or paused)

    }

    @Override
    public void onResume() {
        super.onResume();
        checkInternet(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {

        }  catch (Exception e) {

        }
    }






    @Override
    public void onDestroy() {
        mViewModel.cancelSearchRequest(true);
        super.onDestroy();

    }

    @Override
    public void onEvent(int position) {
        BlockedPeople dl= mAdapter.getSelected(position);
        Call<ResponseBody> call = mViewModel.RemoveBlock(dl.getBlock_record_id());
        String blocked_status= dl.getStatus();
        if(blocked_status.equals("0")) blocked_status="1";

        String finalBlocked_status = blocked_status;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().string();
                            if(result.equals("success")) {
                                mAdapter.deleteSelected(position);
                                BlockedListDao bDao = BlockedListDatabase.getInstance(context).getListDao();
                                bDao.updateList(dl.getId(), finalBlocked_status);

                            }
                            else
                                Utils.openSnackBar(getString(R.string.block_fail_response), view);


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
}