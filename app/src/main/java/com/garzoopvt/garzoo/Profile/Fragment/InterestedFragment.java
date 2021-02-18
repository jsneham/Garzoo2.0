package com.garzoopvt.garzoo.Interested.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.garzoopvt.garzoo.Chat.Activity.ChatRoomListingActivity;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Interested.Activity.InterestedInnerActivity;
import com.garzoopvt.garzoo.Interested.Adapter.InterestedAdapter;
import com.garzoopvt.garzoo.Interested.ViewModel.InterestedViewModel;
import com.garzoopvt.garzoo.Model.FilterCategory;
import com.garzoopvt.garzoo.Model.Product;
import com.garzoopvt.garzoo.Pagination.PaginationScrollListener;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Utility.SessionManager;
import com.garzoopvt.garzoo.Utility.URLs;
import com.garzoopvt.garzoo.Utility.UsefulIntent;
import com.garzoopvt.garzoo.Utility.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterestedFragment extends Fragment implements View.OnClickListener {


    private ArrayList<Object> recyclerItems;
    private RecyclerView recyclerView;
    private Context context;
    private InterestedAdapter interestedAdapter;
    private ArrayList<Product> productArrayList;
    private ArrayList<FilterCategory> filterCategoryArrayList;
    private SessionManager sessionManager;
    private String user_name,user_id,latitude, longitude;
    private SearchView searchView;
    View root;
    private InterestedViewModel mViewModel;
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
    int currentOffset = 0;
    int mMaxDisplay_Size = 6;
    int mTotal_Size = 0;
    String type;
    LinearLayout llNodata;



    public InterestedFragment(){
       // this.setHasOptionsMenu(true);
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
        recyclerView = root.findViewById(R.id.rv_main);
        sessionManager = new SessionManager(context);
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        user_name = sessionManager.getFromSessionManager(SessionManager.USERNAME);
        latitude = sessionManager.getFromSessionManager(SessionManager.LATITUDE);
        longitude = sessionManager.getFromSessionManager(SessionManager.LONGITUDE);
        progressBar = (ProgressBar) root.findViewById(R.id.main_progress);
        llNodata = root.findViewById(R.id.llNodata);
        body = root.findViewById(R.id.body);
        noConnectionLayout = root.findViewById(R.id.fl_retry_internet);
        btnRetry = root.findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternet(context);
            }
        });
        mViewModel = ViewModelProviders.of(this).get(InterestedViewModel.class);
        mViewModel.init(user_id);
    }


    @Override
    public void onResume() {
        super.onResume();
        checkInternet(context);
    }




    private void populateList() {
        String URL = URLs.api_listing_interest;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                productArrayList = new ArrayList<>();
                try {


                    // JSONObject jsonObject = new JSONObject(response);


                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        String id = jsonArray.getJSONObject(i).getString("id");
                        String admin_id = jsonArray.getJSONObject(i).getString("admin_id");
                        String category_id = jsonArray.getJSONObject(i).getString("category_id");
                        String user_id = jsonArray.getJSONObject(i).getString("user_id");
                        String title = jsonArray.getJSONObject(i).getString("title");
                        String description = jsonArray.getJSONObject(i).getString("description");
                        String price = jsonArray.getJSONObject(i).getString("price");
                        String image = URLs.BASE_URL + jsonArray.getJSONObject(i).getString("image");
                        String latitude = jsonArray.getJSONObject(i).getString("latitude");
                        String longitude = jsonArray.getJSONObject(i).getString("longitude");
                        String listing_status = jsonArray.getJSONObject(i).getString("listing_status");
                        String status = jsonArray.getJSONObject(i).getString("status");
                        String last_modified = jsonArray.getJSONObject(i).getString("last_modified");
                        String dt = jsonArray.getJSONObject(i).getString("dt");
                        String fname = jsonArray.getJSONObject(i).getString("fname");
                        String lname = jsonArray.getJSONObject(i).getString("lname");
                        String category = jsonArray.getJSONObject(i).getString("category");
                        Product product = new Product(id, admin_id, category_id, user_id, title, description, price, image, latitude, longitude, listing_status, status, last_modified, dt, fname, lname, category,"","","images");
                        productArrayList.add(product);

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
        interestedAdapter = new InterestedAdapter(context, user_id, user_name);
        RecyclerView.LayoutManager mLayoutManager =new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(interestedAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.ivReport:
//                final int position = (int) v.getTag(R.string.btn_view_position);
//                String id = productArrayList.get(position).getId();
//                openReportPopup(id);
//                break;
            case R.id.ivEdit:
                final int position = (int) v.getTag(R.string.btn_view_position);
                String id = productArrayList.get(position).getId();
                showMenuOption(v.findViewById(R.id.ivEdit), id, position);
                break;


            case R.id.image:
                final int pos = (int) v.getTag(R.string.btn_view_position);
               // openInnerActivity(pos);
                break;

            case R.id.ivInterested:
                final int po = (int) v.getTag(R.string.btn_view_position);
                String id_ = productArrayList.get(po).getId();
                removeinterest(id_);
                break;

            case R.id.ivCall:
                final int posi = (int) v.getTag(R.string.btn_view_position);
                String number = productArrayList.get(posi).getMobile();
                UsefulIntent.openCallFunction(number,context);
                break;

            case R.id.ivShare:
                final int share = (int) v.getTag(R.string.btn_view_position);
                String imagurl = productArrayList.get(share).getImage();
                String text = String.format("Details: %1$s, Description: %2$s Mobile: %3$s", productArrayList.get(share).getTitle(),
                        productArrayList.get(share).getDescription(),productArrayList.get(share).getMobile() );
//                String text = productArrayList.get(share).getTitle() + "\n" +  productArrayList.get(share).getDescription() ;
                UsefulIntent.shareIntent(context,imagurl,text);
                break;

            case R.id.ivChat:
                final int pot = (int) v.getTag(R.string.btn_view_position);
                String ids = productArrayList.get(pot).getUser_id();
                String name_ = String.format("%1$s %2$s", productArrayList.get(pot).getFname(),
                        productArrayList.get(pot).getLname());
                openChatActivity(ids,name_);
                break;

        }
    }

    private void openChatActivity(String id, String name) {
        Intent intent= new Intent(context, ChatRoomListingActivity.class);
        intent.putExtra("to_id" , id);
        intent.putExtra("to_name" , name);
        startActivity(intent);
    }

    private void showMenuOption(View v, final String id, final int position) {

        PopupMenu popup = new PopupMenu(context, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.submenu_pop_up, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Report")) {

                    openReportPopup(id);
                } else {

//                    ContactUsPopup(id, position);

                }
                return true;
            }
        });

        popup.show();
    }

    private void openCallFunction(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }


    private void removeinterest(String listing_id) {
        String URL = URLs.api_add_listing_interest;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                    populateList();
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
                params.put("listing_id", listing_id);
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



    private void openInnerActivity(int pos) {
        Intent intent= new Intent(context, InterestedInnerActivity.class);
        intent.putExtra("data" , productArrayList.get(pos));
        startActivity(intent);
    }

    private void openReportPopup(String id) {
        View view = getLayoutInflater().inflate(R.layout.report_view, null);
        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        EditText etEnquiry = (EditText) view.findViewById(R.id.etEnquiry);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(view);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(etEnquiry.getText().toString())) {
                    alertDialog.dismiss();
                    ReportPost(id,etEnquiry.getText().toString() );

                } else {
                    etEnquiry.setError("give reason/ write something");
                    etEnquiry.setFocusable(true);
                }

            }
        });
    }



    private void ReportPost(String business_id,String report) {
        String URL = URLs.report_post;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
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
                params.put("listing_id", "0");
                params.put("employment_id", "0");
                params.put("business_id", "business_id");
                params.put("report", report);
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

    private void checkInternet(Context context) {
        if (Utils.isConnectedToInternet(context)) {
            Utils.connectionAvailable(body, noConnectionLayout);
            setupRecyclerView();

        } else {
            Utils.showToast(context, "Please Enabled internet");
            Utils.connectionOut(body, noConnectionLayout);
        }
    }


    private void setupRecyclerView() {

        if (interestedAdapter == null) {
            interestedAdapter = new InterestedAdapter(context,  user_id, user_name);
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(interestedAdapter);


            recyclerViewScroolListner();

        } else {
            interestedAdapter.notifyDataSetChanged();
        }


    }

    private void recyclerViewScroolListner() {
        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
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
            mViewModel.getDataRepository(user_id, String.valueOf(PAGE_START),latitude, longitude).observe(this, dataResponse -> {
                if(dataResponse!=null) {
                    List<DashboardList> dataArticles = dataResponse.getInterest();
                    if (dataArticles.size() > 0) {
                        recyclerItems.addAll(dataArticles);
                        Log.d(TAG, "Dash board Product Count: 1STPAGE" + dataArticles.size());
                        //                getBannerAds();
                        //                LoadBannerAds();
                        // dashboardListAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                        interestedAdapter.addAll(recyclerItems);


                        llNodata.setVisibility(View.GONE);

                        if (dataArticles.size() < 10) {
                            TOTAL_PAGES = currentPage;
                            isLastPage = true;
                        } else {
                            if (currentPage <= TOTAL_PAGES) interestedAdapter.addLoadingFooter();
                            else isLastPage = true;

                        }
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        llNodata.setVisibility(View.VISIBLE);

                    }

                }
                else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
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
        mViewModel.getDataRepository(user_id, String.valueOf(currentPage),latitude, longitude).observe(this, dataResponse -> {
            List<DashboardList> dataArticles = dataResponse.getInterest();
            if(dataArticles.size()>0) {

                recyclerItems.addAll(dataArticles);
                Log.d(TAG, "Dash board Product Count: " + dataArticles.size());

                //  getBannerAds();
                //  LoadBannerAds();
                isLoading = false;
                interestedAdapter.removeLoadingFooter();
                interestedAdapter.addAll(recyclerItems);


                if(dataArticles.size()!=10)TOTAL_PAGES=currentPage;

                if (currentPage != TOTAL_PAGES)
                    interestedAdapter.addLoadingFooter();
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
        mViewModel=null;
        interestedAdapter=null;
        recyclerItems=null;
    }


}