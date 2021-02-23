package com.garzoopvt.garzoo.Profile.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.Dashboard.Persistence.Converters;
import com.garzoopvt.garzoo.Profile.Model.BlockedPeople;


@Database(entities = { BlockedPeople.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class BlockedListDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "blockedpeople_db";

    private static BlockedListDatabase instance;

    public static BlockedListDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    BlockedListDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract BlockedListDao getListDao();
}
