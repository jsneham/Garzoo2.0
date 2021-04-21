package com.garzoopvt.garzoo.Dashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.garzoopvt.garzoo.Adapter.MultipleImagesAdapter;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.ExoPlayerActivity;
import com.garzoopvt.garzoo.Util.ExpandableTextView;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DashboardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    OnDashboardListener mOnDashboardListener;
    public ImageView ivEdit, ivCall;
    TextView txtView_title, head, tprice, tdescription, timestamp, tvLocation, ivSpeaker;
    TextView username, type, price;
    Button ivShare, ivInterested, ivChat;
    RecyclerView rvImages;
    ImageView image, img_playback;
    FrameLayout flVideo;
    public MaterialCardView item_container;
    ExpandableTextView txtView_description;
    public LinearLayout.LayoutParams params;
    RequestManager requestManager;
    ViewPreloadSizeProvider<String> preloadSizeProvider;


    public DashboardViewHolder(@NonNull View itemView, OnDashboardListener mOnDashboardListener, RequestManager requestManager, ViewPreloadSizeProvider<String> preloadSizeProvider) {
        super(itemView);
        params = new LinearLayout.LayoutParams(0, 0);
        this.mOnDashboardListener = mOnDashboardListener;
        this.requestManager = requestManager;
        this.preloadSizeProvider = preloadSizeProvider;
        item_container = itemView.findViewById(R.id.item_container);
        params = new LinearLayout.LayoutParams(0, 0);
        ivInterested = itemView.findViewById(R.id.ivInterested);
        ivChat = itemView.findViewById(R.id.ivChat);
        ivEdit = itemView.findViewById(R.id.ivEdit);
        ivCall = itemView.findViewById(R.id.ivCall);
        ivShare = itemView.findViewById(R.id.ivShare);
        ivSpeaker = itemView.findViewById(R.id.ivSpeaker);
        txtView_title = itemView.findViewById(R.id.title);
        txtView_description = itemView.findViewById(R.id.description);
        username = itemView.findViewById(R.id.username);
        type = itemView.findViewById(R.id.type);
        price = itemView.findViewById(R.id.price);
        timestamp = itemView.findViewById(R.id.timestamp);
        tvLocation = itemView.findViewById(R.id.tvLocation);
        head = itemView.findViewById(R.id.head);
        tprice = itemView.findViewById(R.id.tprice);
        tdescription = itemView.findViewById(R.id.tdescription);
        tvLocation = itemView.findViewById(R.id.tvLocation);
        rvImages = itemView.findViewById(R.id.rvImages);
        //img_vol = itemView.findViewById(R.id.img_vol);

        img_playback = itemView.findViewById(R.id.img_playback);
        flVideo = itemView.findViewById(R.id.flVideo);
        image = itemView.findViewById(R.id.image);


        ivCall.setOnClickListener(this::onClick);
        ivChat.setOnClickListener(this::onClick);
        ivInterested.setOnClickListener(this::onClick);
        ivShare.setOnClickListener(this::onClick);
        ivEdit.setOnClickListener(this::onClick);
        item_container.setOnClickListener(this::onClick);
        flVideo.setOnClickListener(this::onClick);
        img_playback.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivCall:
                mOnDashboardListener.onCallClick(getAdapterPosition());
                break;
            case R.id.ivChat:
                mOnDashboardListener.onChatClick(getAdapterPosition());
                break;

            case R.id.ivInterested:
                mOnDashboardListener.onLikeClick(getAdapterPosition(), ivInterested);
                break;
            case R.id.ivShare:
                mOnDashboardListener.onShareClick(getAdapterPosition());
                break;
            case R.id.ivEdit:
                mOnDashboardListener.onEditClick(getAdapterPosition(), ivEdit);
                break;

            case R.id.item_container:
                mOnDashboardListener.onItemClick(getAdapterPosition());
                break;

            case R.id.flVideo:
                mOnDashboardListener.onVideoClick(getAdapterPosition());
                break;

            case R.id.img_playback:
                mOnDashboardListener.onVideoClick(getAdapterPosition());
                break;
        }
    }


    public void onBind(DashboardList mDashboardList, Context mContext, int i, String user_id) {

        if (mDashboardList.getMobile_status().equals("1"))
            Glide.with(mContext).load(R.drawable.ic_baseline_call_hide).into(ivCall);

        if (mDashboardList.getUser_id().equals(user_id)) {
            ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_own, 0, 0, 0);
            if (!mDashboardList.getData_type().equals("P"))
                ivChat.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_own, 0, 0, 0);
            Glide.with(mContext).load(R.drawable.ic_baseline_call_hide).into(ivCall);
        } else {
            if (mDashboardList.getInterest_status().equalsIgnoreCase("yes"))
                ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_24, 0, 0, 0);
            if (mDashboardList.getInterest_status().equalsIgnoreCase("no"))
                ivInterested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_thumb_up_24, 0, 0, 0);

        }

        username.setText(mDashboardList.getFname() + " " + mDashboardList.getLname());
        txtView_title.setText(mDashboardList.getTitle());
        txtView_description.setText(mDashboardList.getDescription().trim());
        timestamp.setText(Utils.formateDate(mDashboardList.getDt()));
        ivSpeaker.setText(String.format("%1$s %2$s", mDashboardList.getDistance(), mContext.getString(R.string.distance)));
        if (mDashboardList.getAddress().isEmpty())
            tvLocation.setVisibility(View.GONE);
        else
            tvLocation.setText(mDashboardList.getAddress());

        if (mDashboardList.getPrice().isEmpty())
            price.setVisibility(View.GONE);
        else
            price.setText(String.format("₹ %1$s", mDashboardList.getPrice()));

        String listing_status = mDashboardList.getListing_status();
        switch (listing_status) {
            case "1":
                type.setText(String.format("%1$s", mContext.getString(R.string.sell_)));
                break;
            case "2":
                type.setText(String.format("%1$s", mContext.getString(R.string.rentregist)));
                break;
            case "3":
                type.setText(String.format("%1$s", mContext.getString(R.string.rentrequiremnt)));
                break;
        }


        String data_type = mDashboardList.getData_type();
        switch (data_type) {
            case "E":
                String emp_status = (mDashboardList.getEmp_status().equals("1")) ? mContext.getString(R.string.kam_pahije) : mContext.getString(R.string.roj_pahije);
                type.setText(emp_status);
                head.setText(String.format("%1$s %2$s", mContext.getString(R.string.emp_title), " : "));
                tdescription.setText(String.format("%1$s %2$s", mContext.getString(R.string.emp_description), " : "));
                tprice.setText(String.format("%1$s %2$s", mContext.getString(R.string.emp_price), " : "));
                mDashboardList.setType("3");
                break;
            case "B":
                type.setText(String.format("%1$s %2$s %3$s", "(", mContext.getString(R.string.buisness), ")"));
                head.setText(String.format("%1$s %2$s", mContext.getString(R.string.busi_title), " : "));
                tdescription.setText(String.format("%1$s %2$s", mContext.getString(R.string.busi_description), " : "));
                tprice.setText(String.format("%1$s %2$s", mContext.getString(R.string.busi_type), " : "));
                price.setText(String.format("%1$s", mDashboardList.getCategory_id()));
                price.setTextColor(mContext.getResources().getColor(R.color.black));
                price.setVisibility(View.VISIBLE);
                tprice.setVisibility(View.VISIBLE);
                mDashboardList.setType("2");
                break;

            case "P":
                String pd_status = (mDashboardList.getPd_status().equals("1")) ? mContext.getString(R.string.jahirat_) : mContext.getString(R.string.charcha_);
                type.setText(pd_status);
                ;
                if (mDashboardList.getPd_status().equals("1")) {
                    head.setText(String.format("%1$s %2$s", mContext.getString(R.string.jahirat_title), " : "));
                    tdescription.setText(String.format("%1$s %2$s", mContext.getString(R.string.jahirat_des), " : "));
                } else {
                    head.setText(String.format("%1$s %2$s", mContext.getString(R.string.charcha_title), " : "));
                    tdescription.setText(String.format("%1$s %2$s", mContext.getString(R.string.charcha_des), " : "));
                }
                price.setVisibility(View.GONE);
                tprice.setVisibility(View.GONE);
                mDashboardList.setType("4");
                break;
            default:
                tprice.setVisibility(View.VISIBLE);
                head.setText(String.format("%1$s %2$s", mContext.getString(R.string.title1), " : "));
                tdescription.setText(String.format("%1$s %2$s", mContext.getString(R.string.description1), " : "));
                tprice.setText(String.format("%1$s %2$s", mContext.getString(R.string.price1), " : "));
                mDashboardList.setType("1");
                break;
        }


        String imge[] = mDashboardList.getImages().split(",");
        createGallery(imge, rvImages, mDashboardList, mContext);

        if (mDashboardList.getVideo() == null || mDashboardList.getVideo().equals("")) {
            flVideo.setVisibility(View.GONE);
        } else {
            flVideo.setVisibility(View.VISIBLE);
            //Picasso.get().load(URLs.IMAGE_URL + mDashboardList.getImage()).into(image);
            Glide.with(mContext)
                    .load(mDashboardList.getVideo()).into(image);
        }


        // setVideoUrl(mDashboardList.getVideo());
        // setLooping(true); //optional - true by default

        //to play pause videos manually (optional)
//        img_playback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isPlaying()) {
//                    pauseVideo();
//                    setPaused(true);
//                } else {
//                    playVideo();
//                    setPaused(false);
//                }
//            }
//        });

        //to mute/un-mute video (optional)
//        img_vol.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isMuted) {
//                    unmuteVideo();
//                   img_vol.setImageResource(R.drawable.ic_unmute);
//                } else {
//                    muteVideo();
//                    img_vol.setImageResource(R.drawable.ic_mute);
//                }
//                isMuted = ! isMuted;
//            }
//        });

//        if (mDashboardList.getVideo() == null) {
//            //img_vol.setVisibility(View.GONE);
//            img_playback.setVisibility(View.GONE);
//            flVideo.setVisibility(View.GONE);
//        } else {
//            //img_vol.setVisibility(View.VISIBLE);
//            img_playback.setVisibility(View.VISIBLE);
//            flVideo.setVisibility(View.VISIBLE);
//        }

    }


    private void createGallery(String[] images, RecyclerView rvImages, DashboardList productArrayList, Context mContext) {
        GridLayoutManager _sGridLayoutManager = new GridLayoutManager(mContext, 2);
        ArrayList<String> imagesList = new ArrayList<>();

        for (String img : images) {
            if (img.contains("video")){
                productArrayList.setVideo(URLs.IMAGE_URL + img);
            }
            else if (!img.equals("")) imagesList.add(img);
        }


        _sGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (imagesList.size() == 1) return 2;
                else if (imagesList.size() == 3) {
                    if (position == 2) return 2;
                    else return 1;
                } else
                    return 1;

            }
        });


        rvImages.setLayoutManager(_sGridLayoutManager);
        MultipleImagesAdapter rcAdapter = new MultipleImagesAdapter(mContext, imagesList, requestManager, preloadSizeProvider);
        rvImages.setAdapter(rcAdapter);
        rvImages.setLayoutFrozen(true);

    }


}
