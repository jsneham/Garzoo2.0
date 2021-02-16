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
        mViewModel = ViewModelProviders.of(this).get(BuyViewModel.class);

        initView();
        subscribeObservers();
        getList();
        return view;
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
        mViewModel.getCategoryListApi();
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
        Category ct = mCatAdapter.getSelected(position);
        Intent in = new Intent(context, AddSellListingActivity.class);
        in.putExtra("category_id", ct.getId());
        in.putExtra("category_name", ct.getName());
        startActivity(in);
    }
}