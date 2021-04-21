package com.garzoopvt.garzoo.Notification.Fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.garzoopvt.garzoo.Chat.Activity.ChatRoomListingActivity;
import com.garzoopvt.garzoo.Home.HomeActivity;
import com.garzoopvt.garzoo.Notification.Adapter.NotificationAdapter;
import com.garzoopvt.garzoo.Notification.Adapter.OnNotificationListener;
import com.garzoopvt.garzoo.Notification.Model.Notification;
import com.garzoopvt.garzoo.Notification.ViewModel.NotificationViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;
import static com.garzoopvt.garzoo.Util.URLs.NOTIFICATION_ID;
import static com.garzoopvt.garzoo.Util.URLs.language;


public class NotificationFragment extends Fragment implements OnNotificationListener {


    //view
    private View view;
    private Context context;
    private RecyclerView rvList;
    private Button btnLogin;
    private static final String TAG = "NotificationFragment";

    //Data
    private String user_id = "0", notification_count = "0";
    private String language = "en";
    private int page_no = 1;
    private SessionManager sessionManager;


    //instances
    private NotificationViewModel mViewModel;
    private NotificationAdapter mAdapter;
   // private List<Notification> notificationsList = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.notification_fragment, container, false);
        context = getContext();
        mViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        sessionManager = new SessionManager(context);
        initView();
        initRecyclerView();
        if (!notification_count.equals("0")) ResetNotificationCount();

        if (!(user_id.isEmpty() || (user_id.equals("0")))) {
            subscribeObservers();
            getNotification();
        }
        else btnLogin.setVisibility(View.VISIBLE);


        getBannerAdv();
        RemoveNotificationIfAny();
        return view;
    }

    private void RemoveNotificationIfAny() {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
           if(notificationManager!=null) notificationManager.cancel(NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void initView() {
        notification_count = sessionManager.getFromSessionManager(SessionManager.NOTIFICATION_COUNT);
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        language = sessionManager.getFromSessionManager(SessionManager.LANGUAGE);
        if (user_id.isEmpty()) user_id = "0";

        rvList = view.findViewById(R.id.rvList);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openLogin(context);
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new NotificationAdapter(this);
        rvList.setAdapter(mAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        //VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        rvList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }


    private void subscribeObservers() {


        mViewModel.getNotification().observe(this, new Observer<Resource<List<Notification>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Notification>> listResource) {
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
                                Log.d(TAG, "onChanged: status: SUCCESS, : " + listResource.data.size());
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


//            @Override
//            public void onChanged(@Nullable List<Notification> notifications) {
//                if(notifications != null){
//                    notificationsList.addAll(notifications);
//                    Testing.printRecipes("network test", notifications);
//                    mAdapter.setNotification(notifications);
//
//                }
//
//            }
        });
    }


    private void getNotification() {
        mViewModel.getNotificationApi(user_id, 1, "", language);
    }

    @Override
    public void onCallClick(int position) {
        Notification dl = mAdapter.getSelected(position);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + dl.getPhone()));
        startActivity(intent);

    }

    @Override
    public void onChatClick(int position) {
        Notification dl = mAdapter.getSelected(position);
        Intent intent = new Intent(context, ChatRoomListingActivity.class);
        intent.putExtra("record_id", dl.getNotification_id());
        intent.putExtra("tuid", dl.getFrom_id());
        intent.putExtra("phone_no", dl.getPhone());
        intent.putExtra("to_name", dl.getFrom_name());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void callResetNotificationCount() {
        ((HomeActivity) getActivity()).callResetNotificationCount();
    }

    private void ResetNotificationCount() {

        Call<ResponseBody> call = mViewModel.ResetNotificationCount(user_id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        try {

                            String result = response.body().string();
                            Log.d("ResetNotificationCount", result);
                            if (result.equals("success"))
                                sessionManager.setToSessionManager(SessionManager.NOTIFICATION_COUNT, "0");
                            callResetNotificationCount();
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
    public void onDestroy() {
        mViewModel.cancelSearchRequest(true);
        super.onDestroy();
    }
}