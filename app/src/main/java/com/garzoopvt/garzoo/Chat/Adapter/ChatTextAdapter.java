package com.garzoopvt.garzoo.Chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.garzoopvt.garzoo.Business.Model.Business;
import com.garzoopvt.garzoo.Chat.Model.ChatIndividual;
import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.URLs;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.http.Url;

public class ChatTextAdapter extends RecyclerView.Adapter<ChatTextAdapter.ViewHolder> {


    //user id
    private String userId;
    private Context context;

    //Tag for tracking self message
    private int SELF = 786;
    private int OTHER = 890;

    //ArrayList of messages object containing all the messages in the thread
    private List<ChatIndividual> mMessagesList;
    View.OnClickListener listener;

    //Constructor
    public ChatTextAdapter(Context context, String userId, View.OnClickListener listener) {
        this.userId = userId;
        this.mMessagesList = new ArrayList<>();
        ;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Creating view
        View itemView;
        //if view type is self
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_self, parent, false);
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_other, parent, false);
        }
        //returing the view
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Adding messages to the views
        ChatIndividual message = mMessagesList.get(position);

        if (message.getMessage().equals("LOADING...")) return;
        holder.android_gridview_image.setDrawingCacheEnabled(true);
        String file_type = message.getFile_type().replaceAll("\\r\\n|\\r|\\n", "");

        switch (file_type) {
            case "1":
                holder.textViewMessage.setVisibility(View.VISIBLE);
                holder.android_gridview_image.setVisibility(View.GONE);
                holder.textViewMessage.setText(message.getMessage());
                break;
            case "i":
                holder.textViewMessage.setVisibility(View.GONE);
                holder.android_gridview_image.setVisibility(View.VISIBLE);
                if (message.getMessage().contains(URLs.IMAGE_URL))
                    Picasso.get().load(message.getMessage()).into(holder.android_gridview_image);
                else
                    Picasso.get().load(URLs.IMAGE_URL + message.getMessage()).into(holder.android_gridview_image);
                // Glide.with(context).load(URLs.IMAGE_URL+message.getMessage()).into(holder.android_gridview_image);
                break;
        }

        holder.textViewTime.setText(message.getDt());


        holder.android_gridview_image.setTag(R.string.btn_view_position, position);
        holder.android_gridview_image.setOnClickListener(listener);
    }


    @Override
    public int getItemCount() {
        return mMessagesList == null ? 0 : mMessagesList.size();
    }


    //IN this method we are tracking the self message
    @Override
    public int getItemViewType(int position) {
        if (mMessagesList.size() > 0) {
            //getting message object of current position
            ChatIndividual message = mMessagesList.get(position);
            //If its owner  id is  equals to the logged in user id
            if (message.getId() != null) {
                if (message.getFrom_uid().equals(userId)) {
                    //Returning self
                    return SELF;
                } else {
                    return OTHER;
                }
            }
            return position;
        } else
            return position;
    }


    public void setList(List<ChatIndividual> texts) {
        mMessagesList = texts;
        notifyDataSetChanged();
    }


    public void updateList(List<ChatIndividual> texts) {
        mMessagesList.add(texts.get(0));
        notifyDataSetChanged();
    }

    private boolean isLoading() {
        if (mMessagesList != null) {
            if (mMessagesList.size() > 0) {
                if (mMessagesList.get(mMessagesList.size() - 1).getMessage().equals("LOADING...")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void hideLoading() {
        if (isLoading()) {
            if (mMessagesList.get(0).getMessage().equals("LOADING...")) {
                mMessagesList.remove(mMessagesList.size() - 1);
            }
        }
        if (isLoading()) {
            if (mMessagesList.get(mMessagesList.size() - 1).getMessage().equals("LOADING...")) {
                mMessagesList.remove(mMessagesList.size() - 1);
            }
        }
        notifyDataSetChanged();
    }


    public void displayOnlyLoading() {
        clearRecipesList();
        ChatIndividual texts = new ChatIndividual();
        texts.setMessage("LOADING...");
        mMessagesList.add(texts);
        notifyDataSetChanged();
    }

    private void clearRecipesList() {
        if (mMessagesList == null) {
            mMessagesList = new ArrayList<>();
        } else {
            mMessagesList.clear();
        }
        notifyDataSetChanged();
    }

    public void displayLoading() {
        if (mMessagesList == null) {
            mMessagesList = new ArrayList<>();
        }
        if (!isLoading()) {
            ChatIndividual texts = new ChatIndividual();
            texts.setMessage("LOADING...");
            mMessagesList.add(texts); // loading at bottom of screen
            notifyDataSetChanged();
        }
    }

    public ChatIndividual getSelected(int position) {
        if (mMessagesList != null) {
            if (mMessagesList.size() > 0) {
                return mMessagesList.get(position);
            }
        }
        return null;
    }

    //Initializing views
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;
        public TextView textViewTime;
        public ImageView android_gridview_image;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            android_gridview_image = (ImageView) itemView.findViewById(R.id.android_gridview_image);
        }
    }

}
