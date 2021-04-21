package com.garzoopvt.garzoo.Notification.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.garzoopvt.garzoo.Adapter.LoadingViewHolder;
import com.garzoopvt.garzoo.Adapter.SearchExhaustedViewHolder;
import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.Dashboard.Adapter.DashboardViewHolder;
import com.garzoopvt.garzoo.Notification.Model.Notification;
import com.garzoopvt.garzoo.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LIST_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int ITEM_BANNER = 3;
    private static final int EXHAUSTED_TYPE = 4;
    private static final int EndList_TYPE = 5;
    private List<Notification> mNotification;
    private OnNotificationListener mOnNotificationListener;

    public NotificationAdapter(OnNotificationListener mOnNotificationListener) {
        this.mOnNotificationListener = mOnNotificationListener;
        mNotification = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;


        switch (i) {
            case LIST_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_row, viewGroup, false);
                return new NotificationViewHolder(view, mOnNotificationListener);
            }
            case EXHAUSTED_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false);
                return new SearchExhaustedViewHolder(view);
            }
            case LOADING_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false);
                return new LoadingViewHolder(view);
            }
            default:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_row, viewGroup, false);
                return new NotificationViewHolder(view, mOnNotificationListener);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = getItemViewType(i);
        if(itemViewType == LIST_TYPE) {

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background);


            if (!(mNotification.get(i).getTitle().equals("LOADING...") || mNotification.get(i).getTitle().equals("EXHAUSTED..."))) {
                ((NotificationViewHolder) viewHolder).title.setText(mNotification.get(i).getTitle());
                ((NotificationViewHolder) viewHolder).date.setText(mNotification.get(i).getDate());
                String notification_type = mNotification.get(i).getType();


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
        }

    }


    @Override
    public int getItemViewType(int position) {
        int type=1;
        if(mNotification.size()>position) {
            if (mNotification.get(position).getTitle().equals("LOADING...")) {
                return LOADING_TYPE;
            } else if (mNotification.get(position).getTitle().equals("EXHAUSTED...")) {
                return EXHAUSTED_TYPE;
            }
            else {
                return LIST_TYPE;
            }
        } else{
            type= EXHAUSTED_TYPE;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        if(mNotification!=null) {
            return mNotification.size();
        }
        return 0;
    }

    public void setQueryExhausted(){
        hideLoading();
        Notification exhausted = new Notification();
        exhausted.setTitle("EXHAUSTED...");
        mNotification.add(exhausted);
        notifyDataSetChanged();
    }

    public void hideLoading(){
        if(isLoading()) {
            if (mNotification.get(0).getTitle().equals("LOADING...")) {
                mNotification.remove(mNotification.size() - 1);
            }
        }
        if(isLoading()){
            if(mNotification.get(mNotification.size() - 1).getTitle().equals("LOADING...")){
                mNotification.remove(mNotification.size() - 1);
            }
        }
        notifyDataSetChanged();
    }

    public void displayOnlyLoading(){
        clearRecipesList();
        Notification recipe = new Notification();
        recipe.setTitle("LOADING...");
        mNotification.add(recipe);
        notifyDataSetChanged();
    }


    private void clearRecipesList(){
        if(mNotification == null){
            mNotification = new ArrayList<>();
        }
        else {
            mNotification.clear();
        }
        notifyDataSetChanged();
    }

    public void displayLoading(){
        if(mNotification == null){
            mNotification = new ArrayList<>();
        }
        if(!isLoading()){
            Notification recipe = new Notification();
            recipe.setTitle("LOADING...");
            mNotification.add(recipe); // loading at bottom of screen
            notifyDataSetChanged();
        }
    }

    private boolean isLoading(){
        if(mNotification != null){
            if(mNotification.size() > 0){
                if(mNotification.get(mNotification.size() - 1).getTitle().equals("LOADING...")){
                    return true;
                }
            }
        }
        return false;
    }

    public Notification getSelected(int position){
        if(mNotification != null){
            if(mNotification.size() > 0){
                return mNotification.get(position);
            }
        }
        return null;
    }

    public void setList(List<Notification> buys){
        mNotification = buys;
        notifyDataSetChanged();
    }
    public void clearList(){
        mNotification = new ArrayList<>();
        notifyDataSetChanged();
    }
}
