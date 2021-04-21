package com.garzoopvt.garzoo.BuySell.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.NativeAdsManager;
import com.garzoopvt.garzoo.BuySell.Activity.AddSellListingActivity;
import com.garzoopvt.garzoo.BuySell.Adapter.BuyAdapter;
import com.garzoopvt.garzoo.BuySell.Adapter.CategoryAdapter;
import com.garzoopvt.garzoo.BuySell.Adapter.OnCategoryListener;
import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.garzoopvt.garzoo.BuySell.ViewModel.BuyViewModel;
import com.garzoopvt.garzoo.Dashboard.Adapter.RecycleAdapter_GridHome;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Rent.ViewModel.RentViewModel;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;
import java.util.List;

import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;


public class SellSubCategoryFragment extends Fragment implements OnCategoryListener {


    //view
    private View view;
    private Context context;
    private RecyclerView rvTabs;
    private static final String TAG = "SellSubCategoryFragment";


    //instances
    private BuyViewModel mViewModel;
    private CategoryAdapter mCatAdapter;
    private String user_id;
    private SessionManager sessionManager;
    private String mLanguageCode = "en";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_sub, container, false);
        context = getContext();
        sessionManager = new SessionManager(context);
        mViewModel = ViewModelProviders.of(this).get(BuyViewModel.class);
        getSessionData();
        initView();
        subscribeObservers();
        getList();
        getBannerAdv();
        return view;
    }

    private void getBannerAdv() {

//        final AdView adView = new AdView(context);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId(BANNER_ID);
        AdView adView = view.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //Toast.makeText(getContext(), "Loaded", Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                //  Toast.makeText(getContext(), adError.getCode() + ", "+ adError.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                //Toast.makeText(getContext(), "onAdOpened", Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Toast.makeText(getContext(), "onAdClicked", Toast.LENGTH_SHORT).show();
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(getContext(), "onAdLeftApplication", Toast.LENGTH_SHORT).show();
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Toast.makeText(getContext(), "onAdClosed", Toast.LENGTH_SHORT).show();
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }


    private void getSessionData() {
        mLanguageCode = sessionManager.getFromSessionManager(SessionManager.LANGUAGE);
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        if (user_id.isEmpty()) user_id = "0";
    }

    private void initView() {
        rvTabs = view.findViewById(R.id.rvTabs);
        mCatAdapter = new CategoryAdapter(context, this::onCategoryItemClick);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvTabs.setLayoutManager(mLayoutManager);
        rvTabs.setItemAnimator(new DefaultItemAnimator());
        rvTabs.setAdapter(mCatAdapter);

    }

    private void getList() {
        mViewModel.getCategoryListApi(mLanguageCode);
    }

    private void subscribeObservers() {


        mViewModel.getCategory().observe(this, new Observer<Resource<List<Category>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Category>> listResource) {
                if (listResource != null) {
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    if (listResource.data != null) {
                        // Testing.printRecipess("data: ", listResource.data);

                        switch (listResource.status) {
                            case SUCCESS: {
                                Log.d(TAG, "onChanged: cache has been refreshed.");
                                Log.d(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size());
                                mCatAdapter.setList(listResource.data);
                                break;
                            }

                        }
                    }


                }
            }

        });
    }

    @Override
    public void onCategoryItemClick(int position) {
        if(!(user_id.equals("0")|| user_id.isEmpty())) {
        Category ct = mCatAdapter.getSelected(position);
        Intent in = new Intent(context, AddSellListingActivity.class);
        in.putExtra("category_id", ct.getId());
        in.putExtra("category_name", ct.getName());
        startActivity(in);
        }
        else{
            Utils.openLogin(context);
        }
    }
}