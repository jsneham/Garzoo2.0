package com.garzoopvt.garzoo.Adapter;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.garzoopvt.garzoo.R;

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
