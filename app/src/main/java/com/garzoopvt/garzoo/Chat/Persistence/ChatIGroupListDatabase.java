package com.garzoopvt.garzoo.Chat.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.Chat.Model.ChatGroupList;
import com.garzoopvt.garzoo.Dashboard.Persistence.Converters;


@Database(entities = { ChatGroupList.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class ChatIGroupListDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "chatgrouplist_db";

    private static ChatIGroupListDatabase instance;

    public static ChatIGroupListDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ChatIGroupListDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract ChatGroupListDao getListDao();
}

