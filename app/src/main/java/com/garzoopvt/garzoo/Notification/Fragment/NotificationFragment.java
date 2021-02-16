package com.garzoopvt.garzoo.Notification.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.garzoopvt.garzoo.Notification.Adapter.NotificationAdapter;
import com.garzoopvt.garzoo.Notification.Adapter.OnNotificationListener;
import com.garzoopvt.garzoo.Notification.Model.Notification;
import com.garzoopvt.garzoo.Notification.ViewModel.NotificationViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.Testing;
import com.garzoopvt.garzoo.Util.VerticalSpacingItemDecorator;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment implements OnNotificationListener {


    //view
    private View view;
    private Context context;
    private RecyclerView rvList;
    private static final String TAG = "NotificationFragment";

    //Data
    private String user_id="0";
    private int page_no=1;

    //instances
    private NotificationViewModel mViewModel;
    private NotificationAdapter mAdapter;
    private List<Notification> notificationsList=new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.notification_fragment, container, false);
        context=getContext();
        mViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        initView();
        initRecyclerView();
        subscribeObservers();
        getNotification();
        return view;
    }

    private void initView() {
        rvList = view.findViewById(R.id.rvList);
    }

    private void initRecyclerView() {
        mAdapter = new NotificationAdapter(this);
        rvList.setAdapter(mAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        //VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        rvList.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
    }







    private void subscribeObservers(){

        mViewModel.getNotification().observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable List<Notification> notifications) {
                if(notifications != null){
                    notificationsList.addAll(notifications);
                    Testing.printRecipes("network test", notifications);
                    mAdapter.setNotification(notifications);

                }

            }
        });
    }



    private void getNotification(){
        mViewModel.getNotificationApi(user_id, 1);
    }

    @Override
    public void onCallClick(int position) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + notificationsList.get(position).getPhone()));
        context.startActivity(intent);

    }

    @Override
    public void onChatClick(int position) {
//        String title = notificationsList.get(position).getTitle();
//        String id_ = notificationsList.get(position).getFrom_id();
//        String name_ = notificationsList.get(position).getFrom_name();
//        Intent intent = new Intent(context, ChatRoomListingActivity.class);
//        intent.putExtra("to_id", id_);
//        intent.putExtra("to_name", name_);
//        context.startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mViewModel.onBackPressed();
        super.onDestroy();
    }
}