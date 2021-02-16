package com.garzoopvt.garzoo.Chat.Fragment;

import android.content.Context;
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
import com.garzoopvt.garzoo.Chat.Adapter.ChatAdapter;
import com.garzoopvt.garzoo.Chat.Adapter.GroupChatAdapter;
import com.garzoopvt.garzoo.Chat.Adapter.OnItemListener;
import com.garzoopvt.garzoo.Chat.Model.ChatGroup;
import com.garzoopvt.garzoo.Chat.ViewModel.ChatViewModel;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.RetrofitService.Resource;

import java.util.ArrayList;
import java.util.List;

import static com.garzoopvt.garzoo.Dashboard.ViewModel.DashboardViewModel.QUERY_EXHAUSTED;


public class ChatCommentFragment extends Fragment  implements OnItemListener {

    private ArrayList<Object> recyclerItems = new ArrayList<>();
    public static final int ITEM_PER_ADV = 3;
    private Context context;
    private View view;
    private RecyclerView rvList;
    private String TAG= "CommentFrag";

    //instances
    private ChatViewModel mViewModel;
    private GroupChatAdapter mAdapter;

    //Data
    private String user_id = "71";
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
        view= inflater.inflate(R.layout.fragment_chat, container, false);
        mViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        setSessionData();
        init();
        subscribeObservers();
        getChatGroup();
        return view;
    }



    private void setSessionData() {
        context=getContext();


    }

    private void init() {
        rvList = (RecyclerView) view.findViewById(R.id.rvList);
        RecyclerView.LayoutManager mLayoutManager =new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(mLayoutManager);
        rvList.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new GroupChatAdapter(context,user_id, this);
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

    private void getChatGroup() {
        mViewModel.getChatGroupListApi(user_id, page_no, search_name);
    }


    @Override
    public void onItemClick(int position) {
        ChatGroup dl=  mAdapter.getSelected(position);
    }
}