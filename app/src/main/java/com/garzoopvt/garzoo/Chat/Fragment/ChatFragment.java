package com.garzoopvt.garzoo.Chat.Fragment;

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

import com.garzoopvt.garzoo.Chat.Activity.IndividualChatActivity;
import com.garzoopvt.garzoo.Chat.Adapter.ChatAdapter;
import com.garzoopvt.garzoo.Chat.Adapter.OnItemListener;
import com.garzoopvt.garzoo.Chat.Model.ChatGroup;
import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.garzoopvt.garzoo.Chat.ViewModel.ChatViewModel;
import com.garzoopvt.garzoo.Dashboard.Adapter.DashboardAdapter;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.RetrofitService.Resource;

import java.util.List;

import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;


public class ChatFragment extends Fragment implements OnItemListener {

    private Context context;
    private View view;
    private RecyclerView rvList;
    private String TAG = "ChatFrag";

    //instances
    private ChatViewModel mViewModel;
    private ChatAdapter mAdapter;

    //Data
    private String user_id = "71";
    private String search_name = "";
    private int page_no = 1;

    public ChatFragment() {
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
        subscribeObservers();
        getChatUser();
        return view;
    }


    private void setSessionData() {
        context = getContext();

    }

    private void init() {
        rvList = (RecyclerView) view.findViewById(R.id.rvList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(mLayoutManager);
        rvList.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ChatAdapter(context,user_id,this);
        rvList.setAdapter(mAdapter);

    }




    private void subscribeObservers() {

        mViewModel.getChatUser().observe(this, new Observer<Resource<List<ChatUser>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<ChatUser>> listResource) {
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
                                Toast.makeText(context, listResource.message, Toast.LENGTH_SHORT).show();

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

    private void getChatUser() {
        mViewModel.getChatUserListApi(user_id, page_no, search_name);
    }



    @Override
    public void onItemClick(int position) {
        ChatUser chatArrayList=  mAdapter.getSelected(position);
        Intent intent= new Intent(context, IndividualChatActivity.class);
        intent.putExtra("data" , chatArrayList);
        context.startActivity(intent);
    }
}