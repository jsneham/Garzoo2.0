package com.garzoopvt.garzoo.Rent.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdsManager;
import com.garzoopvt.garzoo.Adapter.EndListViewHolder;
import com.garzoopvt.garzoo.Adapter.LoadingViewHolder;
import com.garzoopvt.garzoo.Adapter.SearchExhaustedViewHolder;
import com.garzoopvt.garzoo.Dashboard.Adapter.OnDashboardListener;
import com.garzoopvt.garzoo.Promotion.Model.Promotion;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Rent.Model.Rent;

import java.util.ArrayList;
import java.util.List;

public class RentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private static final int LIST_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int ITEM_BANNER = 3;
    private static final int EXHAUSTED_TYPE = 4;
    private static final int EndList_TYPE = 5;

    public final int ITEM_PER_ADV = 8;

    private List<Rent> mRentList;
    private List<NativeAd> mAdItems;
    private OnDashboardListener mOnListener;
    private Context mContext;
    private NativeAdsManager mNativeAdsManager;
    private RequestManager requestManager;
    private ViewPreloadSizeProvider<String> preloadSizeProvider;

    public RentAdapter(OnDashboardListener mOnListener, Context mContext,
                       NativeAdsManager mNativeAdsManager,
                       RequestManager requestManager, ViewPreloadSizeProvider<String> preloadSizeProvider) {
        this.mOnListener = mOnListener;
        this.mContext = mContext;
        this.mNativeAdsManager = mNativeAdsManager;
        this.requestManager = requestManager;
        this.preloadSizeProvider = preloadSizeProvider;
        mRentList = new ArrayList<>();
        mAdItems = new ArrayList<>();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;

        switch (i) { // i is the view type constant
            case LIST_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_row, viewGroup, false);
                return new RentViewHolder(view, mOnListener,requestManager,preloadSizeProvider);
            }
            case LOADING_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false);
                return new LoadingViewHolder(view);
            }
            case EXHAUSTED_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false);
                return new SearchExhaustedViewHolder(view);
            }
            case EndList_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_endlist, viewGroup, false);
                return new EndListViewHolder(view);
            }
            case ITEM_BANNER: {
                 view = (NativeAdLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.native_ad_unit, viewGroup, false);
                return new BannerViewViewHolder((NativeAdLayout)view);
            }
            default:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_row, viewGroup, false);
                return new RentViewHolder(view, mOnListener,requestManager,preloadSizeProvider);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        try {
            int itemViewType = getItemViewType(i);
            if(itemViewType == LIST_TYPE) {
                int index = i - (i /ITEM_PER_ADV ) - 1;
//                if(index==-1){
//                    ((DashboardViewHolder) viewHolder).item_container.setLayoutParams(((DashboardViewHolder) viewHolder).params);
//                }else {

                ((RentViewHolder)viewHolder).onBind(mRentList.get(i), mContext,i);

//                }
            }
            else if(itemViewType == ITEM_BANNER){
                NativeAd ad;

                if (mAdItems.size() > i / ITEM_PER_ADV) {
                    ad = mAdItems.get(i / ITEM_PER_ADV);
                } else {
                    ad = mNativeAdsManager.nextNativeAd();
                    if(ad!=null){
                        if (!ad.isAdInvalidated()) {
                            mAdItems.add(ad);
                        } else {
                            Log.d("DashboardListAdapter", "Ad is invalidated!");
                        }
                    }
                }

                BannerViewViewHolder adHolder = (BannerViewViewHolder) viewHolder;
                adHolder.adChoicesContainer.removeAllViews();

                if (ad != null) {

                    adHolder.tvAdTitle.setText(ad.getAdvertiserName());
                    adHolder.tvAdBody.setText(ad.getAdBodyText());
                    adHolder.tvAdSocialContext.setText(ad.getAdSocialContext());
                    adHolder.tvAdSponsoredLabel.setText(R.string.sponsored);
                    adHolder.btnAdCallToAction.setText(ad.getAdCallToAction());
                    adHolder.btnAdCallToAction.setVisibility(
                            ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                    AdOptionsView adOptionsView = new AdOptionsView(mContext, ad, adHolder.nativeAdLayout);
                    adHolder.adChoicesContainer.addView(adOptionsView, 0);

                    List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(adHolder.ivAdIcon);
                    clickableViews.add(adHolder.mvAdMedia);
                    clickableViews.add(adHolder.btnAdCallToAction);
                    ad.registerViewForInteraction(
                            adHolder.nativeAdLayout, adHolder.mvAdMedia, adHolder.ivAdIcon, clickableViews);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
            return mRentList == null ? 0 : mRentList.size() + mAdItems.size();
    }
    public void setList(List<Rent> rents){
        mRentList = rents;
        notifyDataSetChanged();
    }



    public int getItemViewType1(int position) {
        int type=1;
        try {
            if(mRentList.size()>=position) {
                if (mRentList.get(position).getTitle().equals("LOADING...")) {
                    type = LOADING_TYPE;
                }
                else if(position == mRentList.size() - 1
                        && position != 0
                        && !mRentList.get(position).getTitle().equals("EXHAUSTED...")){
                    type= LOADING_TYPE;
                }
                else if (position>0 && position % ITEM_PER_ADV == 0) {
                    type= ITEM_BANNER;
                }
                else{
                    type= LIST_TYPE;
                }
            }
            else{
                type= ITEM_BANNER;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         return  type;

    }


    @Override
    public int getItemViewType(int position) {
        int type=1;
        if(mRentList.size()>position) {
            if (mRentList.get(position).getTitle().equals("LOADING...")) {
                return LOADING_TYPE;
            } else if (mRentList.get(position).getTitle().equals("EXHAUSTED...")) {
                return EXHAUSTED_TYPE;
            }
            else if (position > 0 && position % ITEM_PER_ADV == 0) {
                return ITEM_BANNER;
            }
            else {
                return LIST_TYPE;
            }
        } else{
            type= EndList_TYPE;
        }
        return type;
    }


    public void setQueryExhausted(){
        hideLoading();
        Rent exhausted = new Rent();
        exhausted.setTitle("EXHAUSTED...");
        mRentList.add(exhausted);
        notifyDataSetChanged();
    }

    public void hideLoading(){
        if(isLoading()) {
            if (mRentList.get(0).getTitle().equals("LOADING...")) {
                mRentList.remove(mRentList.size() - 1);
            }
        }
        if(isLoading()){
            if(mRentList.get(mRentList.size() - 1).getTitle().equals("LOADING...")){
                mRentList.remove(mRentList.size() - 1);
            }
        }
        notifyDataSetChanged();
    }

    public void displayOnlyLoading(){
        clearRecipesList();
        Rent recipe = new Rent();
        recipe.setTitle("LOADING...");
        mRentList.add(recipe);
        notifyDataSetChanged();
    }


    private void clearRecipesList(){
        if(mRentList == null){
            mRentList = new ArrayList<>();
        }
        else {
            mRentList.clear();
        }
        notifyDataSetChanged();
    }

    public void displayLoading(){
        if(mRentList == null){
            mRentList = new ArrayList<>();
        }
        if(!isLoading()){
            Rent recipe = new Rent();
            recipe.setTitle("LOADING...");
            mRentList.add(recipe); // loading at bottom of screen
            notifyDataSetChanged();
        }
    }

    private boolean isLoading(){
        if(mRentList != null){
            if(mRentList.size() > 0){
                if(mRentList.get(mRentList.size() - 1).getTitle().equals("LOADING...")){
                    return true;
                }
            }
        }
        return false;
    }

    public Rent getSelected(int position){
        if(mRentList != null){
            if(mRentList.size() > 0){
                return mRentList.get(position);
            }
        }
        return null;
    }

    public void clearList(){
        mRentList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public class BannerViewViewHolder extends RecyclerView.ViewHolder {

        NativeAdLayout nativeAdLayout;
        MediaView mvAdMedia;
        MediaView ivAdIcon;
        TextView tvAdTitle;
        TextView tvAdBody;
        TextView tvAdSocialContext;
        TextView tvAdSponsoredLabel;
        Button btnAdCallToAction;
        LinearLayout adChoicesContainer;

        public BannerViewViewHolder(NativeAdLayout adLayout) {
            super(adLayout);
            nativeAdLayout = adLayout;
            mvAdMedia = adLayout.findViewById(R.id.native_ad_media);
            tvAdTitle = adLayout.findViewById(R.id.native_ad_title);
            tvAdBody = adLayout.findViewById(R.id.native_ad_body);
            tvAdSocialContext = adLayout.findViewById(R.id.native_ad_social_context);
            tvAdSponsoredLabel = adLayout.findViewById(R.id.native_ad_sponsored_label);
            btnAdCallToAction = adLayout.findViewById(R.id.native_ad_call_to_action);
            ivAdIcon = adLayout.findViewById(R.id.native_ad_icon);
            adChoicesContainer = adLayout.findViewById(R.id.ad_choices_container);
        }
    }

}
