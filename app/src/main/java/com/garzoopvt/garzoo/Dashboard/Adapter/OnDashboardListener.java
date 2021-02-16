package com.garzoopvt.garzoo.Dashboard.Adapter;

import android.widget.Button;

public interface OnDashboardListener {

    void onCallClick(int position);
    void onChatClick(int position);
    void onShareClick(int position);
    void onLikeClick(int position, Button ivInterested);
    void onEditClick(int position);
    void onItemClick(int position);
}
