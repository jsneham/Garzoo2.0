package com.garzoopvt.garzoo.Chat.Fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.garzoopvt.garzoo.Business.Model.Business;
import com.garzoopvt.garzoo.Chat.Activity.GroupChatListingActivity;
import com.garzoopvt.garzoo.Chat.Adapter.ChatAdapter;
import com.garzoopvt.garzoo.Chat.Adapter.GroupChatAdapter;
import com.garzoopvt.garzoo.Chat.Adapter.OnItemListener;
import com.garzoopvt.garzoo.Chat.Model.ChatGroup;
import com.garzoopvt.garzoo.Chat.ViewModel.ChatViewModel;
import com.garzoopvt.garzoo.Home.HomeActivity;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.RetrofitService.Resource;
import com.garzoopvt.garzoo.Util.SessionManager;
import com.garzoopvt.garzoo.Util.URLs;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;


public class ChatCommentFragment extends Fragment implements OnItemListener {

    private ArrayList<Object> recyclerItems = new ArrayList<>();
    public static final int ITEM_PER_ADV = 3;
    private Context context;
    private View view;
    private RecyclerView rvList;
    private String TAG = "CommentFrag";

    //instances
    private ChatViewModel mViewModel;
    private GroupChatAdapter mAdapter;
    private SessionManager sessionManager;

    //Data
    private String user_id = "0";
    private String chat_notification_count = "0";
    private String search_name = "";
    private int page_no = 1;

    public ChatCommentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        setSessionData();
        init();
        getBannerAdv();

        if (!user_id.equals("0")) {
            subscribeObservers();
        }


        if (!chat_notification_count.equals("0")) ResetNotificationCount();

        RemoveNotificationIfAny();
        return view;
    }


    private void RemoveNotificationIfAny() {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) notificationManager.cancel(URLs.NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!user_id.equals("0")) {
            getChatGroup();
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

    private void setSessionData() {
        context = getContext();
        sessionManager = new SessionManager(context);
        user_id = sessionManager.getFromSessionManager(SessionManager.USER_ID);
        if (user_id.isEmpty()) user_id = "0";
        chat_notification_count = sessionManager.getFromSessionManager(SessionManager.CHAT_COUNT);

    }

    private void init() {
        rvList = (RecyclerView) view.findViewById(R.id.rvList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(mLayoutManager);
        rvList.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new GroupChatAdapter(context, user_id, this);
        rvList.setAdapter(mAdapter);
    }


    private void subscribeObservers() {

        mViewModel.geChatGroup().observe(this, new Observer<Resource<List<ChatGroup>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ChatGroup>> listResource) {
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


    }

    private void getChatGroup() {
        mViewModel.getChatGroupListApi(user_id, page_no, search_name);
    }


    @Override
    public void onItemClick(int position) {
        ChatGroup dl = mAdapter.getSelected(position);

        Intent intent = new Intent(context, GroupChatListingActivity.class);
        intent.putExtra("id", dl.getId());
        intent.putExtra("title", dl.getTitle());
        startActivity(intent);
    }


    private void callResetNotificationCount() {
        ((HomeActivity) getActivity()).callResetChatNotificationCount();
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
                                sessionManager.setToSessionManager(SessionManager.CHAT_COUNT, "0");
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
}