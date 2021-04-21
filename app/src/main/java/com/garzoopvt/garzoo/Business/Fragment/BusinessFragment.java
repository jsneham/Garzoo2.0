package com.garzoopvt.garzoo.Business.Fragment;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.garzoopvt.garzoo.Business.Activity.AddBusinessListingActivity;
import com.garzoopvt.garzoo.Business.Activity.BusinessInnerActivity;
import com.garzoopvt.garzoo.Business.Activity.EditBusinessListingActivity;
import com.garzoopvt.garzoo.Business.Adapter.BusinessAdapter;
import com.garzoopvt.garzoo.Business.Model.Business;
import com.garzoopvt.garzoo.Business.Persistence.BusinessDao;
import com.garzoopvt.garzoo.Business.Persistence.BusinessDatabase;
import com.garzoopvt.garzoo.Business.ViewModel.BusinessViewModel;
import com.garzoopvt.garzoo.BuySell.Activity.EditSellListingActivity;
import com.garzoopvt.garzoo.Chat.Activity.ChatRoomListingActivity;
import com.garzoopvt.garzoo.Dashboard.Adapter.OnDashboardListener;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Employement.Activity.EditEmpListingActivity;
import com.garzoopvt.garzoo.Employement.Model.Employment;
import com.garzoopvt.garzoo.Employement.Persistence.EmploymentDao;
import com.garzoopvt.garzoo.Employement.Persistence.EmploymentDatabase;
import com.garzoopvt.garzoo.Promotion.Activity.EditPromoListingActivity;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Rent.Activity.EditRentListingActivity;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.ExoPlayerActivity;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.garzoopvt.garzoo.services.LocationService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;


public class BusinessFragment extends Fragment implements NativeAdsManager.Listener, OnDashboardListener {


    //view
    private ImageView ivClose, ivShare, ivAdvanceSearch, ivMic;
    private View view;
    private Context context;
    private RecyclerView rvList, rvTabs;
    private TextView btnRegistartaion;
    private NestedScrollView rvNestedScroll;
    private EditText searchView;
    private static final String TAG = "BuyFragment";
    private SessionManager sessionManager;
    public String[] text = {""};
    public EditText etEnquiry;

    //instances
    private BusinessViewModel mViewModel;
    private BusinessAdapter mAdapter;
    private NativeAdsManager mNativeAdsManager;


    //Data
    private String category_id = "";
    private String user_id = "0";
    private String username;
    private String search_name = "";
    private String latitude = "";
    private String longitude = "";
    private int page_no = 1;
    public final int ITEM_PER_ADV = 8;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bus, container, false);
        context = getContext();
        mViewModel = ViewModelProviders.of(this).get(BusinessViewModel.class);
        startLocationService();
        sessionManager = new SessionManager(context);
        getSessionData();

        fbNativeAds();
        initView();
        initRecyclerView();
        subscribeObservers();
//        getBusinessList();
        initSearchView();
        getBannerAdv();
        setRefreshListLayout();

        return view;
    }


    private void setRefreshListLayout() {
        SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        getBusinessList();
                        swipeContainer.setRefreshing(false);
                    }
                }, 3000); // Delay in millis

            }
        });
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

    @Override
    public void onResume() {
        super.onResume();
        getBusinessList();
    }

    private void getSessionData() {
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        username = sessionManager.getFromSessionManager(SessionManager.USERNAME);
        if (user_id.isEmpty()) user_id = "0";
        latitude = sessionManager.getFromSessionManager(SessionManager.LATITUDE);
        if (latitude.isEmpty()) latitude = "0";
        longitude = sessionManager.getFromSessionManager(SessionManager.LONGITUDE);
        if (longitude.isEmpty()) longitude = "0";
    }


    private void initView() {
        btnRegistartaion = view.findViewById(R.id.btnRegistartaion);
        rvNestedScroll = view.findViewById(R.id.rvNestedScroll);
        rvList = view.findViewById(R.id.rvList);
        searchView = view.findViewById(R.id.etSearchBox);

        btnRegistartaion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddSheet();
            }
        });

        ivMic = view.findViewById(R.id.ivMic);
        ivShare = view.findViewById(R.id.ivShare);
        ivClose = view.findViewById(R.id.ivClose);
        ivAdvanceSearch = view.findViewById(R.id.ivAdvanceSearch);
        ivAdvanceSearch.setVisibility(View.GONE);


        ivMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput(REQ_CODE_SPEECH_INPUT);
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setText("");
            }
        });
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.shareIntent(context);
            }
        });
    }


    private void startVoiceInput(int code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        String mLanguageCode = sessionManager.getFromSessionManager(SessionManager.LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mLanguageCode + URLs.language);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.help_text));
        try {
            startActivityForResult(intent, code);
        } catch (ActivityNotFoundException a) {

        }
    }


    private void openAddSheet() {
        if (!(user_id.equals("0") || user_id.isEmpty())) {
            Intent intent = new Intent(context, AddBusinessListingActivity.class);
            startActivity(intent);
        } else {
            Utils.openLogin(context);
        }

    }

    private void initRecyclerView() {
        ViewPreloadSizeProvider<String> viewPreloader = new ViewPreloadSizeProvider<>();
        mAdapter = new BusinessAdapter(this, context, mNativeAdsManager, initGlide(), viewPreloader, user_id);
        rvList.setNestedScrollingEnabled(false);
        rvList.setLayoutManager(new LinearLayoutManager(context));

//        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>(Glide.with(context), mAdapter, viewPreloader, ITEM_PER_ADV);
//        rvList.addOnScrollListener(preloader);

        rvNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!v.canScrollVertically(1)) {
                    // search for the next page
                    mViewModel.searchNextPage(user_id, search_name, latitude, longitude, category_id);

                }
            }
        });


        rvList.setAdapter(mAdapter);
    }


    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background);

        return Glide.with(this).setDefaultRequestOptions(options);
    }

    private void subscribeObservers() {

        mViewModel.getBusiness().observe(this, new Observer<Resource<List<Business>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Business>> listResource) {
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
                                //    Toast.makeText(context, listResource.message, Toast.LENGTH_SHORT).show();

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

    private void getBusinessList() {
        mViewModel.getBusinessListApi(user_id, page_no, search_name, latitude, longitude, category_id);
    }

    private void initSearchView() {
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                search_name = editable.toString();
                mViewModel.getBusinessListApi(user_id, page_no, search_name, latitude, longitude, category_id);
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
        if (!(user_id.equals("0") || user_id.isEmpty())) {
            Business dl = mAdapter.getSelected(position);
            if (!dl.getUser_id().equals(user_id)) {
                if (dl.getMobile_status().equals("0")) {
                    logActivity(dl.getId(), dl.getUser_id(), user_id, "1");
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
            Business dl = mAdapter.getSelected(position);
            if (!dl.getUser_id().equals(user_id)) {
                Intent intent = new Intent(context, ChatRoomListingActivity.class);
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
        Business dl = mAdapter.getSelected(position);
        logActivity(dl.getId(), dl.getUser_id(), user_id, "3");
        Utils.shareIntent(context);
    }

    @Override
    public void onLikeClick(int position, Button ivInterested) {
        if (!(user_id.equals("0") || user_id.isEmpty())) {
            Business dl = mAdapter.getSelected(position);
            if (!dl.getUser_id().equals(user_id)) {
                if (dl.getInterest_status().equalsIgnoreCase("yes")) {
                    ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_thumb_up_24, 0, 0, 0);
                    dl.setInterest_status("no");
                } else {
                    ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_24, 0, 0, 0);
                    dl.setInterest_status("yes");
                }
                interest(dl.getId(), dl.getUser_id(), "B", dl.getTitle(), dl.getInterest_status());
            }
        } else {
            Utils.openLogin(context);
        }
    }

    private void interest(String id, String to_user_id, String data_type, String title, String interest_status) {


        RequestBody unique_id = RequestBody.create(MultipartBody.FORM, URLs.unique_id);
        RequestBody rb_user_id = RequestBody.create(MultipartBody.FORM, user_id);
        RequestBody rb_to_user_id = RequestBody.create(MultipartBody.FORM, to_user_id);
        RequestBody rb_full_name = RequestBody.create(MultipartBody.FORM, username);
        RequestBody rb_listing_id = RequestBody.create(MultipartBody.FORM, id);
        RequestBody rb_type = RequestBody.create(MultipartBody.FORM, data_type);
        RequestBody rb_listing_title = RequestBody.create(MultipartBody.FORM, title);
        RequestBody language = RequestBody.create(MultipartBody.FORM, sessionManager.getFromSessionManager(SessionManager.LANGUAGE));

//        mViewModel.interest(unique_id, rb_user_id, rb_to_user_id,
//                rb_full_name, rb_listing_id, rb_type, rb_listing_title,rb_type);
//        logActivity(id, to_user_id, user_id, "2");


        Call<ResponseBody> call = mViewModel.interest(unique_id, rb_user_id, rb_to_user_id,
                rb_full_name, rb_listing_id, rb_type, rb_listing_title,rb_type,language);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().string();
                            Utils.openSnackBar(result, view);
                            BusinessDao businessDao = BusinessDatabase.getInstance(context).getListDao();
                            businessDao.updateInterestStatusList(id, interest_status);
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


    @Override
    public void onItemClick(int position) {
        Business dl = mAdapter.getSelected(position);
        logActivity(dl.getId(), dl.getUser_id(), user_id, "4");
        Intent intent = new Intent(context, BusinessInnerActivity.class);
        intent.putExtra("data", dl);
        startActivity(intent);
    }


    @Override
    public void onEditClick(int position, View view) {
        if (!(user_id.equals("0") || user_id.isEmpty())) {
            Business dl = mAdapter.getSelected(position);
            if (user_id.equals(dl.getUser_id())) {
                showSelfMenuOption(view, dl, position);
            } else {
                showMenuOption(view, dl.getUser_id(), user_id, dl.getId(), position);
            }


        } else {
            Utils.openLogin(context);
        }
    }

    public void showSelfMenuOption(View v, Business productArrayList, int position) {
        PopupMenu popup = new PopupMenu(context, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.submenu_self_pop_up, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_edit) {
                    openEditPage(productArrayList);
                } else if (item.getItemId() == R.id.action_delete) {
                    deletePostCheck(productArrayList, position);
                }
                return true;
            }
        });

        popup.show();
    }

    public void openEditPage(Business dl) {
        Intent intent = new Intent(context, EditBusinessListingActivity.class);
        intent.putExtra("data", dl);
        startActivity(intent);

    }

    public void deletePostCheck(Business productArrayList, int position) {
        String post_id = productArrayList.getId();
        ConfirmationPoup(post_id, position);
    }

    public void ConfirmationPoup(String post_id, int position) {

        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage(R.string.delete_post)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                deletePost(post_id, position);
                            }

                        }).setNegativeButton(R.string.No, null).show();

        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);

        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);

    }


    public void showMenuOption(View v, String to_user_id, String self_user_id, String post_id, int postion) {

        PopupMenu popup = new PopupMenu(context, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.submenu_pop_up, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_report) {
                    openReportPopup(post_id, to_user_id, postion);
                } else {
                    AddBlock(self_user_id, to_user_id, postion);
                }
                return true;
            }
        });

        popup.show();
    }

    public void openReportPopup(String id, String user_id, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_view, null);
        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        RadioGroup rbReport = (RadioGroup) view.findViewById(R.id.rbReport);
        etEnquiry = (EditText) view.findViewById(R.id.etEnquiry);
        ImageView gifEnquiry = (ImageView) view.findViewById(R.id.gifEnquiry);
        ImageView ivClear = (ImageView) view.findViewById(R.id.ivClear);


        rbReport.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                text[0] = radioButton.getText().toString();

            }
        });


        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEnquiry.setText("");
            }
        });

        gifEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, URLs.language);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.help_text));
                try {
//                    ((Activity) context).startActivityForResult(intent, 1);
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException a) {

                }
            }
        });

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

                if (!(text[0].equals("") && etEnquiry.getText().toString().equals(""))) {
                    alertDialog.dismiss();
                    ReportPost(id, "0", "0", text[0], position);

                } else {
                    etEnquiry.setError("कृपया आपले कारण सबमिट करा किंवा वर दिलेल्या पर्यायांपैकी एक तपासा");
                    //etEnquiry.setFocusable(true);
                }

            }
        });
    }

    private void AddBlock(String self_user_id, String to_user_id, int position) {


        Call<ResponseBody> call = mViewModel.block(self_user_id, to_user_id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {
                            String result = response.body().string();
                            if(result.equals("success")) {
                                Utils.openSnackBar(getString(R.string.block_sucess_response), view);
                                mAdapter.deleteSelected(position);
                                mViewModel.blockRemovefromDb(to_user_id);

                                getBusinessList();
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

    private void deletePost(String post_id, int position) {
        mViewModel.deletePost(post_id);
        mAdapter.deleteSelected(position);
//        Call<ResponseBody> call =mViewModel.deletePost(post_id);
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

    private void ReportPost(String post_id, String employment_id, String business_id, String report, int position) {

        Call<ResponseBody> call = mViewModel.ReportPost(user_id, post_id, employment_id, business_id, report);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {

                            String result = response.body().string();
                            Utils.openSnackBar(result, view);
//                            mAdapter.deleteSelected(position);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setText(result.get(0));
                }
                break;

            case 1:
                if (resultCode == RESULT_OK && null != data) {
                    int pos = etEnquiry.getSelectionStart();
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etEnquiry.setText(result.get(0));
                    etEnquiry.setSelection(etEnquiry.getText().toString().length());
                    etEnquiry.requestFocus();
                    text[0] = text[0] + " " + etEnquiry.getText().toString();
                }
                break;
        }
    }

    private void logActivity(String post_id, String post_user_id, String userId, String type) {
        mViewModel.logActivity(post_id,post_user_id,userId,type);
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

    @Override
    public void onDestroy() {
        mViewModel.cancelSearchRequest(true);
        stopLocationService();
        super.onDestroy();
    }

    @Override
    public void onVideoClick(int position) {
        Business dl = mAdapter.getSelected(position);
        openVideoActivity(context, dl);
    }

    private void openVideoActivity(Context context, Business productArrayList) {
        Intent mIntent = ExoPlayerActivity.getStartIntent(context, productArrayList.getVideo());
        mIntent.putExtra("data", productArrayList.getVideo());
        mIntent.putExtra("listing_status", "0");
        mIntent.putExtra("data_type", "B");
        mIntent.putExtra("emp_status", "B");
        mIntent.putExtra("pd_status", "B");
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