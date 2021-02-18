package com.garzoopvt.garzoo.Profile.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Dashboard.Persistence.Converters;
import com.garzoopvt.garzoo.Dashboard.Persistence.DashboardListDao;


@Database(entities = { DashboardList.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class InterestedListDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "interestedlist_db";

    private static InterestedListDatabase instance;

    public static InterestedListDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    InterestedListDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract InterestedListDao getDashboardListDao();
}
