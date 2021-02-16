//package com.garzoopvt.garzoo.Notification.Room;
//
//
//import androidx.lifecycle.LiveData;
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.Query;
//
//import com.garzoopvt.garzoo.Notification.Model.Notification;
//
//import java.util.List;
//
//import static androidx.room.OnConflictStrategy.IGNORE;
//import static androidx.room.OnConflictStrategy.REPLACE;
//
//@Dao
//public interface NotificationDao {
//
//    @Insert(onConflict = IGNORE)
//    long[] insertNotification(Notification... notification);
//
//    @Insert(onConflict = REPLACE)
//    void insertNotification(Notification notification);
//
//    @Query("UPDATE notification SET title = :title, phone = :phone, image = :image, from_name = :from_name " +
//            "WHERE notification_id = :notification_id")
//    void updateNotification(String notification_id, String title, String phone, String image, String from_name);
//
//
//    @Query("SELECT * FROM notification WHERE notification_id = :notification_id")
//    LiveData<Notification> getNotification(String notification_id);
//
//}
//
//
//
//
//
//
//
//
//
