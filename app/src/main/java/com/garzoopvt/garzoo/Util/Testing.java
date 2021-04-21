package com.garzoopvt.garzoo.Util;

import android.util.Log;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Notification.Model.Notification;

import java.util.List;

public class Testing {

    public static void printRecipes(String tag, List<Notification> list) {
        for (Notification r : list) {
            Log.d(tag, "printRecipes: " + r.getNotification_id() + ", " + r.getTitle());
        }
    }

    public static void printRecipess(String tag, List<DashboardList> list) {
        for (DashboardList r : list) {
            Log.d(tag, "printRecipes: " + r.getListing_id() + ", " + r.getTitle());
        }
    }
}
