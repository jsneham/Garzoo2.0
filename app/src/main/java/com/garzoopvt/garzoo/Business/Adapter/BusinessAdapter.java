package com.garzoopvt.garzoo.Business.Adapter;

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
import com.garzoopvt.garzoo.Business.Model.Business;

import com.garzoopvt.garzoo.Dashboard.Adapter.OnDashboardListener;
import com.garzoopvt.garzoo.R;

import java.util.ArrayList;
import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private static final int LIST_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int ITEM_BANNER = 3;
    private static final int EXHAUSTED_TYPE = 4;
    private static final int EndList_TYPE = 5;

    public final int ITEM_PER_ADV = 8;

    private List<Business> mBusinessList;
    private List<NativeAd> mAdItems;
    private OnDashboardListener mOnListener;
    private Context mContext;
    private NativeAdsManager mNativeAdsManager;
    private RequestManager requestManager;
    private ViewPreloadSizeProvider<String> preloadSizeProvider;

    public BusinessAdapter(OnDashboardListener mOnListener, Context mContext,
                           NativeAdsManager mNativeAdsManager,
                           RequestManager requestManager, ViewPreloadSizeProvider<String> preloadSizeProvider) {
        this.mOnListener = mOnListener;
        this.mContext = mContext;
        this.mNativeAdsManager = mNativeAdsManager;
        this.requestManager = requestManager;
        this.preloadSizeProvider = preloadSizeProvider;
        mBusinessList = new ArrayList<>();
        mAdItems = new ArrayList<>();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;

        switch (i) { // i is the view type constant
            case LIST_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_row, viewGroup, false);
                return new BusinessViewHolder(view, mOnListener,requestManager,preloadSizeProvider);
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
                return new BusinessViewHolder(view, mOnListener,requestManager,preloadSizeProvider);
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

                ((BusinessViewHolder)viewHolder).onBind(mBusinessList.get(i), mContext,i);

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
            return mBusinessList == null ? 0 : mBusinessList.size() + mAdItems.size();
    }
    public void setList(List<Business> rents){
        mBusinessList = rents;
        notifyDataSetChanged();
    }



    public int getItemViewType1(int position) {
        int type=1;
        try {
            if(mBusinessList.size()>=position) {
                if (mBusinessList.get(position).getTitle().equals("LOADING...")) {
                    type = LOADING_TYPE;
                }
                else if(position == mBusinessList.size() - 1
                        && position != 0
                        && !mBusinessList.get(position).getTitle().equals("EXHAUSTED...")){
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
        if(mBusinessList.size()>position) {
            if (mBusinessList.get(position).getTitle().equals("LOADING...")) {
                return LOADING_TYPE;
            } else if (mBusinessList.get(position).getTitle().equals("EXHAUSTED...")) {
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
        Business exhausted = new Business();
        exhausted.setTitle("EXHAUSTED...");
        mBusinessList.add(exhausted);
        notifyDataSetChanged();
    }

    public void hideLoading(){
        if(isLoading()) {
            if (mBusinessList.get(0).getTitle().equals("LOADING...")) {
                mBusinessList.remove(mBusinessList.size() - 1);
            }
        }
        if(isLoading()){
            if(mBusinessList.get(mBusinessList.size() - 1).getTitle().equals("LOADING...")){
                mBusinessList.remove(mBusinessList.size() - 1);
            }
        }
        notifyDataSetChanged();
    }

    public void displayOnlyLoading(){
        clearRecipesList();
        Business recipe = new Business();
        recipe.setTitle("LOADING...");
        mBusinessList.add(recipe);
        notifyDataSetChanged();
    }


    private void clearRecipesList(){
        if(mBusinessList == null){
            mBusinessList = new ArrayList<>();
        }
        else {
            mBusinessList.clear();
        }
        notifyDataSetChanged();
    }

    public void displayLoading(){
        if(mBusinessList == null){
            mBusinessList = new ArrayList<>();
        }
        if(!isLoading()){
            Business recipe = new Business();
            recipe.setTitle("LOADING...");
            mBusinessList.add(recipe); // loading at bottom of screen
            notifyDataSetChanged();
        }
    }

    private boolean isLoading(){
        if(mBusinessList != null){
            if(mBusinessList.size() > 0){
                if(mBusinessList.get(mBusinessList.size() - 1).getTitle().equals("LOADING...")){
                    return true;
                }
            }
        }
        return false;
    }

    public Business getSelected(int position){
        if(mBusinessList != null){
            if(mBusinessList.size() > 0){
                return mBusinessList.get(position);
            }
        }
        return null;
    }

    public void deleteSelected(int position){
        if(mBusinessList != null){
            if(mBusinessList.size() > 0){
                mBusinessList.remove(position);

                notifyDataSetChanged();
            }
        }

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
