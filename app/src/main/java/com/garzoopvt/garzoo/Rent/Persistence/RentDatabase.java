package com.garzoopvt.garzoo.Rent.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.Rent.Model.Rent;


@Database(entities = { Rent.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class RentDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "rent_db";

    private static RentDatabase instance;

    public static RentDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    RentDatabase.class,
                    DATABASE_NAME
            ).allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract RentDao getListDao();
}
