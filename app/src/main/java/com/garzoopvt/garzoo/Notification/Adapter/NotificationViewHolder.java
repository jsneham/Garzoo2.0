package com.garzoopvt.garzoo.Notification.Adapter;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.garzoopvt.garzoo.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView title,date;
    Button ivCall,ivChat;
    OnNotificationListener  onNotificationListener;
    AppCompatImageView image;



    public NotificationViewHolder(@NonNull View itemView, OnNotificationListener onNotificationListener) {
        super(itemView);
        this.onNotificationListener= onNotificationListener;
        title=itemView.findViewById(R.id.title);
        date=itemView.findViewById(R.id.date);
        ivCall=itemView.findViewById(R.id.ivCall);
        ivChat=itemView.findViewById(R.id.ivChat);
        image=itemView.findViewById(R.id.image);

        ivCall.setOnClickListener(this::onClick);
        ivChat.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ivCall:
                onNotificationListener.onCallClick(getAdapterPosition());
                break;
            case R.id.ivChat:
                onNotificationListener.onChatClick(getAdapterPosition());
                break;
        }

    }
}
