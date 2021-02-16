package com.garzoopvt.garzoo.Dashboard.Adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.garzoopvt.garzoo.R;


/**
 * ViewHolder for search categories
 */
public class CategoryViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener
{
    ImageView image;
    TextView name;
    OnDashboardListener listener;

    public CategoryViewHolder(@NonNull View itemView, OnDashboardListener onRecipeListener) {
        super(itemView);
        image = itemView.findViewById(R.id.image);
        name =  itemView.findViewById(R.id.name);
        listener = onRecipeListener;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}
