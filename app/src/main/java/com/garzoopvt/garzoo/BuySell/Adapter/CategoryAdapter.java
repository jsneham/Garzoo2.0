package com.garzoopvt.garzoo.BuySell.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.garzoopvt.garzoo.R;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {


    private List<Category> headerArrayList;
    private OnCategoryListener onCategoryListener;
    Context context;

    int pos = 1;
    final boolean[] flip = {true};

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public TextView android_gridview_text;
        public ImageView android_gridview_image, android_gridview_image_replace;
        FrameLayout linear_card;
        OnCategoryListener onCategoryListener;
        MaterialCardView cardview;

        public MyViewHolder(View gridViewAndroid, OnCategoryListener onCategoryListener) {
            super(gridViewAndroid);
            this.onCategoryListener = onCategoryListener;
            // android_gridview_image_replace = (ImageView) gridViewAndroid.findViewById( R.id.android_gridview_image_replace);
            android_gridview_image = (ImageView) gridViewAndroid.findViewById(R.id.image);
            // linear_card = (FrameLayout) gridViewAndroid.findViewById(R.id.linear_card);
            android_gridview_text = (TextView) gridViewAndroid.findViewById(R.id.name);
            cardview = (MaterialCardView) itemView.findViewById( R.id.album_item_container);
            cardview.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {

            onCategoryListener.onCategoryItemClick(getAdapterPosition());
        }
    }


    public CategoryAdapter(Context context, OnCategoryListener onCategoryListener) {
        this.context = context;
        this.headerArrayList = new ArrayList<>();
        this.onCategoryListener = onCategoryListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sub_categories_row, parent, false);

        return new MyViewHolder(itemView,onCategoryListener);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Category v = headerArrayList.get(position);


        holder.android_gridview_text.setText(v.getName());
        Picasso.get().load(v.getImage()).into(holder.android_gridview_image);


    }


    @Override
    public int getItemCount() {
        return headerArrayList.size();
    }


    public void setList(List<Category> buys) {
        headerArrayList = buys;
        notifyDataSetChanged();
    }

    public Category getSelected(int position) {
        if (headerArrayList != null) {
            if (headerArrayList.size() > 0) {
                return headerArrayList.get(position);
            }
        }
        return null;
    }
}