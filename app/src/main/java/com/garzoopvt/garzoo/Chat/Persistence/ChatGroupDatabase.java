package com.garzoopvt.garzoo.Chat.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


import com.garzoopvt.garzoo.Chat.Model.ChatGroup;
import com.garzoopvt.garzoo.Dashboard.Persistence.Converters;


@Database(entities = { ChatGroup.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class ChatGroupDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "chatgroup_db";

    private static ChatGroupDatabase instance;

    public static ChatGroupDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ChatGroupDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract ChatGroupDao getListDao();
}
