package com.garzoopvt.garzoo.Profile.Adapter;

import android.view.View;
import android.widget.Button;

public interface OnMyListListener {


    void onDeleteClick(int position);
    void onRenewClick(int position);
    void onEditClick(int position);
    void onItemClick(int position);
}
