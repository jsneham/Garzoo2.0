package com.garzoopvt.garzoo.Notification.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.garzoopvt.garzoo.Notification.Model.Notification;
import com.garzoopvt.garzoo.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Notification> mNotification;
    private OnNotificationListener mOnNotificationListener;

    public NotificationAdapter(OnNotificationListener mOnNotificationListener) {
        this.mOnNotificationListener = mOnNotificationListener;
        mNotification = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_row, viewGroup, false);
        return new NotificationViewHolder(view, mOnNotificationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_launcher_background);

        ((NotificationViewHolder)viewHolder).title.setText(mNotification.get(i).getTitle());
        ((NotificationViewHolder)viewHolder).date.setText(mNotification.get(i).getDate());
        String notification_type = mNotification.get(i).getNotification_type();


        switch (notification_type) {
            case "1":
                Glide.with(((NotificationViewHolder) viewHolder).itemView)
                        .setDefaultRequestOptions(options)
                        .load(R.drawable.ic_chat_one)
                        .into(((NotificationViewHolder) viewHolder).image);
                break;
            case "0":
                Glide.with(((NotificationViewHolder) viewHolder).itemView)
                        .setDefaultRequestOptions(options)
                        .load(R.drawable.ic_baseline_thumb_up_24)
                        .into(((NotificationViewHolder) viewHolder).image);
                break;
        }

    }

    @Override
    public int getItemCount() {
        if(mNotification!=null) {
            return mNotification.size();
        }
        return 0;
    }

    public void setNotification(List<Notification> notifications){
    mNotification = notifications;
    notifyDataSetChanged();
}
}
