package com.garzoopvt.garzoo.Notification.Room;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.garzoopvt.garzoo.Notification.Model.Notification;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface NotificationDao {

    @Insert(onConflict = IGNORE)
    long[] insertNotification(Notification... notification);

    @Insert(onConflict = REPLACE)
    void insertNotification(Notification notification);

    @Query("UPDATE notification SET title = :title, phone = :phone, image = :image, from_name = :from_name, date = :date " +
            "WHERE notification_id = :notification_id")
    void updateNotification(String notification_id, String title, String phone, String image, String from_name, String date);


    @Query("SELECT * FROM notification WHERE notification_id = :notification_id")
    LiveData<Notification> getNotification(String notification_id);


    @Query("SELECT * FROM notification ORDER BY date DESC")
    LiveData<List<Notification>> getNotification();

    @Query("DELETE FROM notification")
    void deleteAll();

    @Query("DELETE FROM notification WHERE notification_id NOT IN(:lstIDUsers)")
    void deleteOldData(List<String> lstIDUsers);

}









