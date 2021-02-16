package com.garzoopvt.garzoo.Chat.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


import com.garzoopvt.garzoo.Chat.Model.ChatUser;
import com.garzoopvt.garzoo.Dashboard.Persistence.Converters;


@Database(entities = { ChatUser.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class ChatUserDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "chatuser_db";

    private static ChatUserDatabase instance;

    public static ChatUserDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ChatUserDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract ChatUserDao getListDao();
}
