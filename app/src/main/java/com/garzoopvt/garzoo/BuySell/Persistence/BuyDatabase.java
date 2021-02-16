package com.garzoopvt.garzoo.BuySell.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.BuySell.Model.Buy;
import com.garzoopvt.garzoo.Dashboard.Persistence.Converters;



@Database(entities = { Buy.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class BuyDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "buy_db";

    private static BuyDatabase instance;

    public static BuyDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    BuyDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract BuyDao getListDao();
}
