package com.garzoopvt.garzoo.Profile.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Dashboard.Persistence.Converters;


@Database(entities = { DashboardList.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class UserMoreDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "usermorelist_db";

    private static UserMoreDatabase instance;

    public static UserMoreDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    UserMoreDatabase.class,
                    DATABASE_NAME
            ).build();
        }


        return instance;
    }

    public abstract UserMoreListDao getDashboardListDao();
}
