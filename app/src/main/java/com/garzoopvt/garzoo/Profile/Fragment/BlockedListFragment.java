package com.garzoopvt.garzoo.MyListing.Fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Interested.ViewModel.InterestedViewModel;
import com.garzoopvt.garzoo.Model.FilterCategory;
import com.garzoopvt.garzoo.MyListing.Adapter.BlockedUserAdapter;
import com.garzoopvt.garzoo.MyListing.Model.BlockUser;
import com.garzoopvt.garzoo.MyListing.ViewModel.MyListingViewModel;
import com.garzoopvt.garzoo.Pagination.PaginationScrollListener;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Utility.SessionManager;
import com.garzoopvt.garzoo.Utility.URLs;
import com.garzoopvt.garzoo.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockedListFragment extends Fragment implements  BlockedUserAdapter.EventListener {


    private ArrayList<Object> recyclerItems;
    private RecyclerView rvList;
    private Context context;
    private BlockedUserAdapter myListingAdapter;
    private ArrayList<FilterCategory> filterCategoryArrayList;
    private SessionManager sessionManager;
    private String user_id;
    private SearchView searchView;
    View root;
    private InterestedViewModel mViewModel;
    RelativeLayout body;
    FrameLayout noConnectionLayout;
    Button btnRetry;


    LinearLayoutManager layoutManager;
    Picasso picasso;
    ProgressBar item_progress_bar;


    ProgressBar progressBar;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 100;
    private int currentPage = PAGE_START;
    private static final String TAG = "MyListingFragment";
    int currentOffset = 0;
    int mMaxDisplay_Size = 6;
    int mTotal_Size = 0;
    String type;
    LinearLayout llNodata;
    MyListingViewModel dashboardViewModel;

    public BlockedListFragment(){
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_interested, container, false);
        init();
        return root;
    }

    private void init() {
        context=getContext();
        progressBar = (ProgressBar) root.findViewById(R.id.main_progress);

        llNodata = root.findViewById(R.id.llNodata);
        rvList = root.findViewById(R.id.rv_main);
        sessionManager = new SessionManager(context);
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);

        body = root.findViewById(R.id.body);
        noConnectionLayout = root.findViewById(R.id.fl_retry_internet);
        btnRetry = root.findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternet(context);
            }
        });

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
            dashboardViewModel=null;
            myListingAdapter=null;
            recyclerItems=null;

        }  catch (Exception e) {

        }
    }





    private void populateList() {
        String URL = URLs.api_my_records;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                filterCategoryArrayList = new ArrayList<>();

                try {


                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray categoryArray = jsonObject.getJSONArray("category");
                    for (int i = 0; i < categoryArray.length(); i++) {

                        String id = categoryArray.getJSONObject(i).getString("id");
                        String name = categoryArray.getJSONObject(i).getString("name");
                        String status = categoryArray.getJSONObject(i).getString("status");
                        String last_modified = categoryArray.getJSONObject(i).getString("last_modified");
                        String dt = categoryArray.getJSONObject(i).getString("dt");
                        String image = URLs.IMAGE_URL + categoryArray.getJSONObject(i).getString("image");
                        boolean flag = (i == categoryArray.length() - 1) ? true : false;
                        FilterCategory filterCategory = new FilterCategory(id, name, status, last_modified, dt, image, flag);
                        filterCategoryArrayList.add(filterCategory);
                    }
//                    Collections.reverse(filterCategoryArrayList);


                    JSONArray jsonArray = jsonObject.getJSONArray("my_record");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        String id = jsonArray.getJSONObject(i).getString("id");
                        String admin_id = jsonArray.getJSONObject(i).getString("admin_id");
                        String category_id = jsonArray.getJSONObject(i).getString("category_id");
                        String user_id = jsonArray.getJSONObject(i).getString("user_id");
                        String title = jsonArray.getJSONObject(i).getString("title");
                        String description = jsonArray.getJSONObject(i).getString("description");
                        String price = jsonArray.getJSONObject(i).getString("price");
                        String image = jsonArray.getJSONObject(i).getString("image");
                        String images = jsonArray.getJSONObject(i).getString("images");
                        String latitude = jsonArray.getJSONObject(i).getString("latitude");
                        String longitude = jsonArray.getJSONObject(i).getString("longitude");
                        String listing_status = jsonArray.getJSONObject(i).getString("listing_status");
                        String status = jsonArray.getJSONObject(i).getString("status");
                        String last_modified = jsonArray.getJSONObject(i).getString("last_modified");
                        String dt = jsonArray.getJSONObject(i).getString("dt");
                        String fname = jsonArray.getJSONObject(i).getString("fname");
                        String lname = jsonArray.getJSONObject(i).getString("lname");
                        String category = jsonArray.getJSONObject(i).getString("category");
                        String data_type = jsonArray.getJSONObject(i).getString("data_type");
                        String address = jsonArray.getJSONObject(i).getString("address");
                        String available_status  = jsonArray.getJSONObject(i).getString("available_status");
                        String mobile  = jsonArray.getJSONObject(i).getString("mobile");
                        String interest_status  = jsonArray.getJSONObject(i).getString("interest_status");
                        String mobile_status  = jsonArray.getJSONObject(i).getString("mobile_status");

                        DashboardList product = new DashboardList(id,admin_id,category_id,user_id,title,description,price,image,latitude,longitude,status,last_modified,
                                dt,fname,lname,category,data_type,listing_status,address, available_status, mobile,interest_status, images,mobile_status,"" );

                    }
                    setAdapter();
                } catch (Exception e) {
                    Log.e("volley Error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof TimeoutError) {
                }
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("unique_id", URLs.unique_id);
                params.put("user_id", user_id);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new
                DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void setAdapter() {
        myListingAdapter= new BlockedUserAdapter(context,  user_id, BlockedListFragment.this);
        RecyclerView.LayoutManager mLayoutManager =new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(mLayoutManager);
        rvList.setAdapter(myListingAdapter);
    }








    private void checkInternet(Context context) {
        if (Utils.isConnectedToInternet(context)) {
            Utils.connectionAvailable(body, noConnectionLayout);
            dashboardViewModel = ViewModelProviders.of(this).get(MyListingViewModel.class);
            dashboardViewModel.init(user_id);
            setupRecyclerView();
        } else {
            Utils.showToast(context, "Please Enabled internet");
            Utils.connectionOut(body, noConnectionLayout);
        }
    }
    private void setupRecyclerView() {

        if (myListingAdapter == null) {
            myListingAdapter = new BlockedUserAdapter(context,  user_id, BlockedListFragment.this);
            layoutManager = new LinearLayoutManager(context);
            rvList.setLayoutManager(layoutManager);
            rvList.setItemAnimator(new DefaultItemAnimator());
            // rvList.setNestedScrollingEnabled(true);
            rvList.setAdapter(myListingAdapter);
            //call this functions when u want to start autoplay on loading async lists (eg firebase)
//            rvList.smoothScrollBy(0,1);
//            rvList.smoothScrollBy(0,-1);

            recyclerViewScroolListner();

        } else {
            myListingAdapter.notifyDataSetChanged();
        }


    }

    private void recyclerViewScroolListner() {
        rvList.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                Log.d(TAG, "currentPage count: " + currentPage);


                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 10000);
            }

            @Override
            public int getTotalPageCount() {
                return 0;
            }

//            @Override
//            public int getTotalPageCount() {
//                return TOTAL_PAGES;
//            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        // mocking network delay for API call
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setViewModel();
            }
        }, 1000);

    }



    private void setViewModel() {
        try {
            recyclerItems=new ArrayList<>();
            dashboardViewModel.get_blocked_user_list(user_id, String.valueOf(PAGE_START)).observe(this, dataResponse -> {
               if(dataResponse!=null) {
                   List<BlockUser> dataArticles = dataResponse;
                   if (dataArticles.size() > 0) {
                       recyclerItems.addAll(dataArticles);
                       Log.d(TAG, "Dash board Product Count: 1STPAGE" + dataArticles.size());
                       //                getBannerAds();
                       //                LoadBannerAds();
                       // dashboardListAdapter.notifyDataSetChanged();

                       progressBar.setVisibility(View.GONE);
                       myListingAdapter.addAll(recyclerItems);


                       llNodata.setVisibility(View.GONE);

                       if (dataArticles.size() < 10) {
                           TOTAL_PAGES = currentPage;
                           isLastPage = true;
                       } else {
                           if (currentPage <= TOTAL_PAGES) myListingAdapter.addLoadingFooter();
                           else isLastPage = true;

                       }
                   }
                   else {
                       progressBar.setVisibility(View.GONE);
                       rvList.setVisibility(View.GONE);
                       llNodata.setVisibility(View.VISIBLE);

                   }

               }
               else {
                   progressBar.setVisibility(View.GONE);
                   rvList.setVisibility(View.GONE);
                   llNodata.setVisibility(View.VISIBLE);

               }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        recyclerItems=new ArrayList<>();
        dashboardViewModel.get_blocked_user_list(user_id, String.valueOf(currentPage)).observe(this, dataResponse -> {
            List<BlockUser> dataArticles = dataResponse;
            if(dataArticles.size()>0) {

                recyclerItems.addAll(dataArticles);
                Log.d(TAG, "Dash board Product Count: " + dataArticles.size());

                //  getBannerAds();
                //  LoadBannerAds();
                isLoading = false;
                myListingAdapter.removeLoadingFooter();
                myListingAdapter.addAll(recyclerItems);


                if(dataArticles.size()!=10)TOTAL_PAGES=currentPage;

                if (currentPage != TOTAL_PAGES)
                    myListingAdapter.addLoadingFooter();
                else
                    isLastPage = true;
            }
            else{
                // rentAdapter.removeLoadingFooter();
                // isLastPage = true;
            }


        });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dashboardViewModel=null;
        myListingAdapter=null;
        recyclerItems=null;
    }

    @Override
    public void onEvent(int data) {
        currentPage = 1;
       if(recyclerItems!=null) recyclerItems.clear();
        recyclerItems = new ArrayList<>();
        myListingAdapter = new BlockedUserAdapter(context,  user_id, BlockedListFragment.this);
        rvList.setAdapter(myListingAdapter);
        setViewModel();
    }
}