package com.garzoopvt.garzoo.Dashboard.Adapter;

import android.view.View;
import android.widget.Button;

public interface OnDashboardListener {

    void onCallClick(int position);
    void onChatClick(int position);
    void onShareClick(int position);
    void onLikeClick(int position, Button ivInterested);
    void onEditClick(int position, View view);
    void onItemClick(int position);
    void onVideoClick(int position);
}
