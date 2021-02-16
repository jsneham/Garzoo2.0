package com.garzoopvt.garzoo.Rent.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.garzoopvt.garzoo.Adapter.MultipleImagesAdapter;
import com.garzoopvt.garzoo.Dashboard.Adapter.OnDashboardListener;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Rent.Model.Rent;
import com.garzoopvt.garzoo.Util.ExpandableTextView;
import com.garzoopvt.garzoo.Util.URLs;
import com.garzoopvt.garzoo.Util.Utils;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class RentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    OnDashboardListener mOnListener;
    ImageView ivEdit, ivCall;
    TextView txtView_title, head, tprice, tdescription, timestamp, tvLocation, ivSpeaker;
    TextView username, type, price;
    Button ivShare, ivInterested, ivChat;
    RecyclerView rvImages;
    ImageView image, img_playback;
    FrameLayout flVideo;
    MaterialCardView item_container;
    ExpandableTextView txtView_description;
    LinearLayout.LayoutParams params;
    RequestManager requestManager;
    ViewPreloadSizeProvider<String> preloadSizeProvider;


    public RentViewHolder(@NonNull View itemView, OnDashboardListener mOnListener, RequestManager requestManager, ViewPreloadSizeProvider<String> preloadSizeProvider) {
        super(itemView);
        this.mOnListener = mOnListener;
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
      //  image = itemView.findViewById(R.id.image);


        ivCall.setOnClickListener(this::onClick);
        ivChat.setOnClickListener(this::onClick);
        ivInterested.setOnClickListener(this::onClick);
        ivShare.setOnClickListener(this::onClick);
        ivEdit.setOnClickListener(this::onClick);
        item_container.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ivCall:
                mOnListener.onCallClick(getAdapterPosition());
                break;
            case R.id.ivChat:
                mOnListener.onChatClick(getAdapterPosition());
                break;

            case R.id.ivInterested:
                mOnListener.onLikeClick(getAdapterPosition(), ivInterested);
                break;
            case R.id.ivShare:
                mOnListener.onShareClick(getAdapterPosition());
                break;
            case R.id.ivEdit:
                mOnListener.onEditClick(getAdapterPosition());
                break;

            case R.id.item_container:
                mOnListener.onItemClick(getAdapterPosition());
                break;
        }
    }


    public void onBind(Rent mRentList, Context mContext, int i) {

        username.setText(i + ") " + mRentList.getFname() + " " + mRentList.getLname());
        txtView_title.setText(mRentList.getTitle());
        txtView_description.setText(mRentList.getDescription().trim());
        timestamp.setText(Utils.formateDate(mRentList.getDt()));
        ivSpeaker.setText(String.format("%1$s %2$s", mRentList.getDistance(), mContext.getString(R.string.distance)));
        if (mRentList.getAddress().isEmpty())
            tvLocation.setVisibility(View.GONE);
        else
            tvLocation.setText(mRentList.getAddress());

        if (mRentList.getPrice().isEmpty())
            price.setVisibility(View.GONE);
        else
            price.setText(String.format("₹ %1$s", mRentList.getPrice()));

        String listing_status = mRentList.getListing_status();
        switch (listing_status) {
            case "3":
                type.setText(String.format("%1$s", mContext.getString(R.string.rentrequiremnt)));
                break;
            case "2":
                type.setText(String.format("%1$s", mContext.getString(R.string.rentregist)));
                break;
        }

        head.setText(String.format("%1$s %2$s", mContext.getString(R.string.title), " : "));
        tdescription.setText(String.format("%1$s %2$s", mContext.getString(R.string.description_), " : "));
        tprice.setText(String.format("%1$s %2$s", mContext.getString(R.string.dar), " : "));

        String imge[] = mRentList.getImages().split(",");
        createGallery(imge, rvImages, mRentList, mContext);

    }


    private void createGallery(String[] images, RecyclerView rvImages, Rent productArrayList, Context mContext) {
        GridLayoutManager _sGridLayoutManager = new GridLayoutManager(mContext, 2);
        ArrayList<String> imagesList = new ArrayList<>();

        for (String img : images) {
            if (img.contains("video")) productArrayList.setVideo(URLs.IMAGE_URL + img);
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
//        }

        rvImages.setLayoutManager(_sGridLayoutManager);
        MultipleImagesAdapter rcAdapter = new MultipleImagesAdapter(mContext, imagesList, requestManager, preloadSizeProvider);
        rvImages.setAdapter(rcAdapter);
        rvImages.setLayoutFrozen(true);

    }

}
