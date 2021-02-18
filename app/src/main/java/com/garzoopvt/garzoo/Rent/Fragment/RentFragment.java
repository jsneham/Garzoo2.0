package com.garzoopvt.garzoo.Rent.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.garzoopvt.garzoo.BuySell.Adapter.CategoryAdapter;
import com.garzoopvt.garzoo.BuySell.Adapter.OnCategoryListener;
import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.garzoopvt.garzoo.Dashboard.Activity.DashboardInnerActivity;
import com.garzoopvt.garzoo.Dashboard.Adapter.OnDashboardListener;
import com.garzoopvt.garzoo.Dashboard.Adapter.RecycleAdapter_GridHome;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Rent.Activity.AddRentListingActivity;
import com.garzoopvt.garzoo.Rent.Activity.RentInnerActivity;
import com.garzoopvt.garzoo.Rent.Adapter.RentAdapter;
import com.garzoopvt.garzoo.Rent.Model.Rent;
import com.garzoopvt.garzoo.Rent.ViewModel.RentViewModel;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;


public class RentFragment extends Fragment implements NativeAdsManager.Listener, OnDashboardListener, OnCategoryListener, View.OnClickListener {

    //view
    private View view;
    private Context context;
    private RecyclerView rvList,rvTabs;
    private TextView btnRegistartaion;
    private NestedScrollView rvNestedScroll;
    private EditText searchView;
    private static final String TAG = "BuyFragment";
    private SessionManager sessionManager;

    //instances
    private RentViewModel mViewModel;
    private RentAdapter mAdapter;
    private CategoryAdapter mCatAdapter;
    private NativeAdsManager mNativeAdsManager;


    //Data
    private ArrayList<Category> categoryArrayList=new ArrayList<>();
    private String category_id="";
    private String user_id="0";  private String username="Sneha";
    private String search_name="";
    private String latitude="19.108589";
    private String longitude="72.827072";
    private int page_no=1;
    public final int ITEM_PER_ADV = 8;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_rent, container, false);
        context=getContext();
        mViewModel = ViewModelProviders.of(this).get(RentViewModel.class);
        sessionManager =new SessionManager(context);
        getSessionData();

        fbNativeAds();
        initView();
        initRecyclerView();
        subscribeObservers();
        getRentList();
        initSearchView();
        return view;
    }

    private void getSessionData() {
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        if(user_id.isEmpty()) user_id="0";
    }

    private void initView() {
        btnRegistartaion = view.findViewById(R.id.btnRegistartaion);
        rvNestedScroll = view.findViewById(R.id.rvNestedScroll);
        rvList = view.findViewById(R.id.rvList);
        rvTabs = view.findViewById(R.id.rvTabs);
        searchView = view.findViewById(R.id.etSearchBox);

        btnRegistartaion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(user_id.equals("0")|| user_id.isEmpty())) {
                Intent in= new Intent(context, AddRentListingActivity.class);
                in.putExtra("category", categoryArrayList);
                startActivity(in);
                }
                else{
                    Utils.openLogin(context);
                }
            }
        });

    }

    private void initRecyclerView() {
        ViewPreloadSizeProvider<String> viewPreloader=new ViewPreloadSizeProvider<>();
        mAdapter = new RentAdapter(this, context,mNativeAdsManager, initGlide(), viewPreloader);
        rvList.setNestedScrollingEnabled(false);
        rvList.setLayoutManager(new LinearLayoutManager(context));
//        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>(Glide.with(context), mAdapter, viewPreloader, ITEM_PER_ADV);
//        rvList.addOnScrollListener(preloader);
        rvNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(!v.canScrollVertically(1)){
                    // search for the next page
                    mViewModel.searchNextPage(user_id, search_name, latitude,longitude,category_id);

                }
            }
        });
        rvList.setAdapter(mAdapter);


        mCatAdapter = new CategoryAdapter(context,this::onCategoryItemClick);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvTabs.setLayoutManager(mLayoutManager);
        rvTabs.setItemAnimator(new DefaultItemAnimator());
        rvTabs.setAdapter(mCatAdapter);
    }


    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background);

        return Glide.with(this).setDefaultRequestOptions(options);
    }

    private void subscribeObservers(){

        mViewModel.getRent().observe(this, new Observer<Resource<List<Rent>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Rent>> listResource) {
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
                                categoryArrayList.addAll(listResource.data);
                                break;
                            }

                        }
                    }


                }
            }

        });
    }

    private void getRentList(){
        mViewModel.getCategoryListApi();
        mViewModel.getRentListApi(user_id, page_no, search_name, latitude,longitude, category_id);
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
                mViewModel.getRentListApi(user_id, page_no, search_name, latitude,longitude ,category_id);
            }
        });
    }







    private void fbNativeAds() {
        String placement_id = context.getString(R.string.fb_placement_id);
        mNativeAdsManager = new NativeAdsManager(getActivity(), placement_id, ITEM_PER_ADV);
        mNativeAdsManager.loadAds();
        mNativeAdsManager.setListener(this);
    }
    @Override
    public void onAdsLoaded() {
        Log.d(TAG, "onAdsLoaded: ");
    }

    @Override
    public void onAdError(AdError adError) {
        Log.d(TAG, "onAdError: ");
    }


    @Override
    public void onCallClick(int position) {
        if(!(user_id.equals("0")|| user_id.isEmpty())) {
            Rent dl = mAdapter.getSelected(position);
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
            Rent dl = mAdapter.getSelected(position);
            if (!dl.getUser_id().equals(user_id)) {
                if (dl.getInterest_status().equalsIgnoreCase("yes")) {
                    ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_thumb_up_24, 0, 0, 0);
                    dl.setInterest_status("no");
                } else {
                    ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_24, 0, 0, 0);
                    dl.setInterest_status("yes");
                }
                interest(dl.getId(), dl.getUser_id(), "L", dl.getTitle());
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
        Rent dl = mAdapter.getSelected(position);
        Intent intent = new Intent(context, RentInnerActivity.class);
        intent.putExtra("data", dl);
        context.startActivity(intent);
    }

    @Override
    public void onCategoryItemClick(int position) {
        Category ct = mCatAdapter.getSelected(position);
        mViewModel.getRentListApi(user_id, page_no, search_name, latitude, longitude, ct.getId());
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.llButton:
//
//                break;
//
//        }
    }
}