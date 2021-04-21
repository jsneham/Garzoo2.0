package com.garzoopvt.garzoo.Chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.garzoopvt.garzoo.Adapter.EndListViewHolder;
import com.garzoopvt.garzoo.Adapter.LoadingViewHolder;
import com.garzoopvt.garzoo.Adapter.SearchExhaustedViewHolder;
import com.garzoopvt.garzoo.Chat.Model.ChatGroup;

import com.garzoopvt.garzoo.Dashboard.Adapter.DashboardViewHolder;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.R;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LIST_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int ITEM_BANNER = 3;
    private static final int EXHAUSTED_TYPE = 4;
    private static final int EndList_TYPE = 5;
    Context context;
    List<ChatGroup> userArrayList;
    String user_id;
    int[] androidColors;
    int randomAndroidColor;
    GradientDrawable bgShape;
    View.OnClickListener listener;
    public static final int ITEM_PER_ADV = 3;
    OnItemListener onItemListener;
    View rootView;

    public GroupChatAdapter(Context context, String user_id,  OnItemListener onItemListener) { //ArrayList<Chat> userArrayList,View.OnClickListener listener
        this.context = context;
        this.userArrayList = new ArrayList<>();
        this.user_id = user_id;
        this.onItemListener = onItemListener;
        androidColors = context.getResources().getIntArray(R.array.androidcolors);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;
        switch (viewType) {
            case LIST_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_row_group, viewGroup, false);
                return new RecyclerViewViewHolder(view,onItemListener);
            }
            case LOADING_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false);
                return new LoadingViewHolder(view);
            }
            case EXHAUSTED_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false);
                return new SearchExhaustedViewHolder(view);
            }
            case EndList_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_endlist, viewGroup, false);
                return new EndListViewHolder(view);
            }
            default: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_row, viewGroup, false);
                return new RecyclerViewViewHolder(view,onItemListener);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            int viewType = getItemViewType(position);

            switch (viewType) {
                case LIST_TYPE:
                    ChatGroup chat = (ChatGroup) userArrayList.get(position);
                    RecyclerViewViewHolder viewHolder = (RecyclerViewViewHolder) holder;
                    randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                    viewHolder.txtView_title.setText(chat.getTitle());
                    viewHolder.tvImage.setText(chat.getTitle().charAt(0) + "");
                    viewHolder.date.setText(chat.getDt().split(" ")[1].substring(0, 5));

                    viewHolder.llcounter.setVisibility(View.GONE);

                    viewHolder.msg.setText(chat.getLast_message());

                    bgShape = (GradientDrawable) viewHolder.tvImage.getBackground();
                    bgShape.setColor(randomAndroidColor);


                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemViewType(int position) {
        int type = 1;
        if (userArrayList.size() > position) {
            if(userArrayList.get(position).getTitle()!= null) {
                if (userArrayList.get(position).getTitle().equals("LOADING...")) {
                    return LOADING_TYPE;
                } else if (userArrayList.get(position).getTitle().equals("EXHAUSTED...")) {
                    return EXHAUSTED_TYPE;
                } else {
                    return LIST_TYPE;
                }
            }
            else {
                return EndList_TYPE;
            }
        } else {
            type = EndList_TYPE;
        }
        return type;
    }


    public void setQueryExhausted() {
        hideLoading();
        ChatGroup exhaustedRecipe = new ChatGroup();
        exhaustedRecipe.setTitle("EXHAUSTED...");
        userArrayList.add(exhaustedRecipe);
        notifyDataSetChanged();
    }

    public void hideLoading() {
        if (isLoading()) {
            if (userArrayList.get(0).getTitle().equals("LOADING...")) {
                userArrayList.remove(userArrayList.size() - 1);
            }
        }
        if (isLoading()) {
            if (userArrayList.get(userArrayList.size() - 1).getTitle().equals("LOADING...")) {
                userArrayList.remove(userArrayList.size() - 1);
            }
        }
        notifyDataSetChanged();
    }

    public void displayOnlyLoading() {
        clearRecipesList();
        ChatGroup recipe = new ChatGroup();
        recipe.setTitle("LOADING...");
        userArrayList.add(recipe);
        notifyDataSetChanged();
    }


    private void clearRecipesList() {
        if (userArrayList == null) {
            userArrayList = new ArrayList<>();
        } else {
            userArrayList.clear();
        }
        notifyDataSetChanged();
    }

    public void displayLoading() {
        if (userArrayList == null) {
            userArrayList = new ArrayList<>();
        }
        if (!isLoading()) {
            ChatGroup recipe = new ChatGroup();
            recipe.setTitle("LOADING...");
            userArrayList.add(recipe); // loading at bottom of screen
            notifyDataSetChanged();
        }
    }

    private boolean isLoading() {
        if (userArrayList != null) {
            if (userArrayList.size() > 0) {
                if (userArrayList.get(userArrayList.size() - 1).getTitle().equals("LOADING...")) {
                    return true;
                }
            }
        }
        return false;
    }


    public ChatGroup getSelected(int position) {
        if (userArrayList != null) {
            if (userArrayList.size() > 0) {
                return userArrayList.get(position);
            }
        }
        return null;
    }

    public void setList(List<ChatGroup> list){
        userArrayList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class RecyclerViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        PorterShapeImageView imgView_icon;
        TextView txtView_title, date, count, tvImage, msg;
        LinearLayout llRow,llcounter;
        OnItemListener onItemListener;

        public RecyclerViewViewHolder(@NonNull View itemView,  OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;
            imgView_icon = itemView.findViewById(R.id.image);
            txtView_title = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            count = itemView.findViewById(R.id.count);
            msg = itemView.findViewById(R.id.msg);
            tvImage = itemView.findViewById(R.id.tvImage);
            llRow = itemView.findViewById(R.id.llRow);
            llcounter = itemView.findViewById(R.id.llcounter);

            llRow.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }


}