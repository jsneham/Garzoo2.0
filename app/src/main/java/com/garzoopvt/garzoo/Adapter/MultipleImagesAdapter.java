package com.garzoopvt.garzoo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.garzoopvt.garzoo.Dashboard.Adapter.DashboardViewHolder;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.URLs;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.ViewHolder>
        implements
        ListPreloader.PreloadModelProvider<String>{


    private Activity activity;
    private ArrayList<String> data;
    private static LayoutInflater inflater = null;
    View.OnClickListener listener;
    Context context;
    private static final int TYPE_FULL = 0;
    private static final int TYPE_HALF = 1;
    private static final int TYPE_QUARTER = 2;
    private boolean count = false;
    RequestManager requestManager;
    ViewPreloadSizeProvider<String> preloadSizeProvider;

    public MultipleImagesAdapter(Context context, ArrayList<String> data, RequestManager requestManager,  ViewPreloadSizeProvider<String> preloadSizeProvider) {
        this.context = context;
        this.data = data;
        this.requestManager = requestManager;
        this.preloadSizeProvider = preloadSizeProvider;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


//    @Override
//    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
//        final View itemView =
//                LayoutInflater.from(context).inflate(R.layout.images_row, parent, false);
//        itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                final int type = viewType;
//                final ViewGroup.LayoutParams lp = itemView.getLayoutParams();
//                if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
//                    StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
//                    switch (type) {
//                        case TYPE_FULL:
//                            sglp.setFullSpan(true);
//                            break;
//                        case TYPE_HALF:
//                            sglp.setFullSpan(false);
//                            sglp.width = itemView.getWidth() / 2;
//                            break;
//                        case TYPE_QUARTER:
//                            sglp.setFullSpan(false);
//                            sglp.width = itemView.getWidth() / 2;
//                            sglp.height = itemView.getHeight() / 2;
//                            break;
//                    }
//                    itemView.setLayoutParams(sglp);
//                    final StaggeredGridLayoutManager lm =
//                            (StaggeredGridLayoutManager) ((RecyclerView) parent).getLayoutManager();
//                    lm.invalidateSpanAssignments();
//                }
//                itemView.getViewTreeObserver().removeOnPreDrawListener(this);
//                return true;
//            }
//        });
//
//        ViewHolder holder = new ViewHolder(itemView);
//        return holder;
//    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
// set the image
//        RequestOptions options = new RequestOptions()
//                .centerCrop()
//                .error(R.drawable.ic_launcher_background);
//        Glide.with(((DashboardViewHolder) viewHolder).itemView)
//                .setDefaultRequestOptions(options)
//                .load(mDashboardList.get(i).getImage())
//                .into(((DashboardViewHolder) viewHolder).image);

        try {
            ArrayList<String> imageList = data;

            if (!imageList.get(i).equals("")) {
                requestManager
                        .load(URLs.IMAGE_URL + imageList.get(i))
                        .into(holder.ivImage);
               // Picasso.with(context).load(URLs.IMAGE_URL + imageList.get(i)).into(holder.ivImage);
                if ( imageList.size()>4 && i==3) {
                    holder.tvCount.setVisibility(View.VISIBLE);

                    int count = imageList.size() - i;

                    holder.tvCount.setText(String.format("%1$s%2$s%3$s", "(+" , count, ")"));
                }
            } else {
                holder.ivImage.setVisibility(View.GONE);
            }
            preloadSizeProvider.setView(holder.ivImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (data.size() > 3)
            return 4;
        else return data.size();
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        String url = URLs.IMAGE_URL +  data.get(position);
        if (TextUtils.isEmpty(url)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        return requestManager.load(item);
    }


//    @Override
//    public int getItemViewType(int position) {
//        final int modeEight = position % 8;
//        switch (modeEight) {
//            case 0:
//            case 5:
//                return TYPE_HALF;
//            case 1:
//            case 2:
//            case 4:
//            case 6:
//                return TYPE_QUARTER;
//        }
//        return TYPE_FULL;
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImage;
        public TextView tvCount;


        public ViewHolder(View vi) {
            super(vi);

            ivImage = vi.findViewById(R.id.ivImage); // thumb image
            tvCount = vi.findViewById(R.id.tvCount); // thumb image


        }


    }


}

