package com.garzoopvt.garzoo.Chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.garzoopvt.garzoo.Chat.Model.ChatGroupList;
import com.garzoopvt.garzoo.Chat.Model.ChatIndividual;
import com.garzoopvt.garzoo.R;
import com.garzoopvt.garzoo.Util.URLs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.ViewHolder> {

    //user id
    private String userId;
    private Context context;

    //Tag for tracking self message
    private int SELF = 786;
    private int OTHER = 890;

    //ArrayList of messages object containing all the messages in the thread
    private List<ChatGroupList> mMessagesList;
    View.OnClickListener listener;

    //Constructor
    public GroupChatListAdapter(Context context, String userId, View.OnClickListener listener) {
        this.userId = userId;
        this.mMessagesList = new ArrayList<>();;
        this.context = context;
        this.listener = listener;
    }

    //IN this method we are tracking the self message
    @Override
    public int getItemViewType(int position) {
        if(mMessagesList.size()>0) {
            //getting message object of current position
            ChatGroupList message = mMessagesList.get(position);
            //If its owner  id is  equals to the logged in user id
            if(message.getId()!=null) {
                if (message.getUser_id().equals(userId)) {
                    //Returning self
                    return SELF;
                }
                else{
                    return OTHER;
                }
            }
            return position;
        }
        else
            return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creating view
        View itemView;
        //if view type is self
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread_comment, parent, false);
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread_other_comment, parent, false);
        }
        //returing the view
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Adding messages to the views
        ChatGroupList message = mMessagesList.get(position);
        if(message.getComment().equals("LOADING...")) return;


        holder.textName.setText(message.getFname()  +  " "+message.getLname()  );
        holder.textViewTime.setText(message.getDt());


        String file_type= message.getFile_type().replaceAll("\\r\\n|\\r|\\n", "");

        switch (file_type){
            case "1":
                holder.textViewMessage.setVisibility(View.VISIBLE);
                holder.android_gridview_image.setVisibility(View.GONE);
                holder.textViewMessage.setText(message.getComment());
                break;
            case "i":
                holder.textViewMessage.setVisibility(View.GONE);
                holder.android_gridview_image.setVisibility(View.VISIBLE);
                //Glide.with(context).load(URLs.IMAGE_URL+message.getComment()).into(holder.android_gridview_image);
                if (message.getComment().contains(URLs.IMAGE_URL))
                    Picasso.get().load(message.getComment()).into(holder.android_gridview_image);
                else
                    Picasso.get().load(URLs.IMAGE_URL + message.getComment()).into(holder.android_gridview_image);
                break;
        }

        holder.textViewTime.setText(message.getDt());

        holder.android_gridview_image.setTag(R.string.btn_view_position, position);
        holder.android_gridview_image.setOnClickListener(listener);
    }


    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

    public void setList(List<ChatGroupList> texts){
        mMessagesList = texts;
        notifyDataSetChanged();
    }


    public void updateList(List<ChatGroupList> texts){
        mMessagesList.add(texts.get(0));
        notifyDataSetChanged();
    }

    private boolean isLoading(){
        if(mMessagesList != null){
            if(mMessagesList.size() > 0){
                if(mMessagesList.get(mMessagesList.size() - 1).getComment().equals("LOADING...")){
                    return true;
                }
            }
        }
        return false;
    }

    public void hideLoading(){
        if(isLoading()) {
            if (mMessagesList.get(0).getComment().equals("LOADING...")) {
                mMessagesList.remove(mMessagesList.size() - 1);
            }
        }
        if(isLoading()){
            if(mMessagesList.get(mMessagesList.size() - 1).getComment().equals("LOADING...")){
                mMessagesList.remove(mMessagesList.size() - 1);
            }
        }
        notifyDataSetChanged();
    }


    public void displayOnlyLoading(){
        clearRecipesList();
        ChatGroupList texts = new ChatGroupList();
        texts.setComment("LOADING...");
        mMessagesList.add(texts);
        notifyDataSetChanged();
    }

    private void clearRecipesList(){
        if(mMessagesList == null){
            mMessagesList = new ArrayList<>();
        }
        else {
            mMessagesList.clear();
        }
        notifyDataSetChanged();
    }

    public void displayLoading(){
        if(mMessagesList == null){
            mMessagesList = new ArrayList<>();
        }
        if(!isLoading()){
            ChatGroupList texts = new ChatGroupList();
            texts.setComment("LOADING...");
            mMessagesList.add(texts); // loading at bottom of screen
            notifyDataSetChanged();
        }
    }

    public ChatGroupList getSelected(int position){
        if(mMessagesList != null){
            if(mMessagesList.size() > 0){
                return mMessagesList.get(position);
            }
        }
        return null;
    }

    //Initializing views
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;
        public TextView textViewTime;
        public TextView textName;
        public ImageView android_gridview_image;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            textName = (TextView) itemView.findViewById(R.id.textName);
            android_gridview_image = (ImageView) itemView.findViewById(R.id.android_gridview_image);
        }
    }
}