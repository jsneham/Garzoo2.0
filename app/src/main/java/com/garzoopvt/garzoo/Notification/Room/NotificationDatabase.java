//package com.garzoopvt.garzoo.Notification.Room;
//
//
//import android.content.Context;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import androidx.room.TypeConverters;
//
//import com.garzoopvt.garzoo.Notification.Model.Notification;
//
//
//@Database(entities = {Notification.class}, version = 1)
//@TypeConverters({Converters.class})
//public abstract class NotificationDatabase extends RoomDatabase {
//
//    public static final String DATABASE_NAME = "notification_db";
//
//    private static NotificationDatabase instance;
//
//    public static NotificationDatabase getInstance(final Context context){
//        if(instance == null){
//            instance = Room.databaseBuilder(
//                    context.getApplicationContext(),
//                    NotificationDatabase.class,
//                    DATABASE_NAME
//            ).build();
//        }
//        return instance;
//    }
//
//    public abstract NotificationDao getRecipeDao();
//
//}
//
//
//
//
//
//
