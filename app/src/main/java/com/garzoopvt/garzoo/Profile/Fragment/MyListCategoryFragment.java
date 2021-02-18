package com.garzoopvt.garzoo.MyListing.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.garzoopvt.garzoo.Dashboard.Adapter.RecycleAdapter_GridHome;
import com.garzoopvt.garzoo.Model.HomeGridModelClass;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Utility.RecyclerTouchListener;
import com.garzoopvt.garzoo.Utility.SessionManager;

import java.util.ArrayList;


public class MyListCategoryFragment extends Fragment{


    private Integer image[] = {R.drawable.kharedi,  R.drawable.bhade, R.drawable.rojgar, R.drawable.businessv, R.drawable.charcha};
    private Integer name[] = {R.string.sell,  R.string.rent, R.string.employement, R.string.buisness, R.string.promotion};
    private Context context;
    private View view;

    private RecyclerView rvTabs;
    private ArrayList<HomeGridModelClass> homeGridModelClasses;


    private RecycleAdapter_GridHome mAdapter;
    private SessionManager sessionManager;
    public final int ITEM_PER_ADV = 6;
//    private final String BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
    private ArrayList<Object> recyclerItems = new ArrayList<>();
    private final String TAG = "DashBoardFragment";
    AdView adView;





    public MyListCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_mylist_board, container, false);
            context = getContext();
            init();
            setData();
            getBannerAds();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }






    private void getBannerAds() {

//        final AdView adView = new AdView(context);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId(BANNER_ID);
        adView = view.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());

    }




    private void setData() {
        homeGridModelClasses.clear();
        homeGridModelClasses = new ArrayList<>();
        for (int i = 0; i < image.length; i++) {
            HomeGridModelClass beanClassForRecyclerView_contacts = new HomeGridModelClass(image[i], name[i]);
            homeGridModelClasses.add(beanClassForRecyclerView_contacts);
        }
        mAdapter = new RecycleAdapter_GridHome(getActivity(), homeGridModelClasses);
        rvTabs.setAdapter(mAdapter);


    }


    private void initRecyclerClick() {
        rvTabs.addOnItemTouchListener(new RecyclerTouchListener(context,
                rvTabs, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {



                switch (position) {
                    case 0:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new MyListingFragment("S"),"MyListingFragment").commit();
                        break;
                    case 1:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new MyListingFragment("R"),"MyListingFragment").commit();
                        break;
                    case 2:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new MyListingFragment("E"),"MyListingFragment").commit();
                        break;
                    case 3:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new MyListingFragment("B"),"MyListingFragment").commit();
                        break;
                    case 4:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new MyListingFragment("P"),"MyListingFragment").commit();
                        break;
                    default:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new MyListingFragment("S"),"MyListingFragment").commit();
                }


            }


            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void init() {
        try {

            MobileAds.initialize(context, "ca-app-pub-3940256099942544~3347511713");
            rvTabs = (RecyclerView) view.findViewById(R.id.rvTabs);
            homeGridModelClasses = new ArrayList<>();
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            rvTabs.setLayoutManager(mLayoutManager);
            rvTabs.setItemAnimator(new DefaultItemAnimator());
            initRecyclerClick();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
