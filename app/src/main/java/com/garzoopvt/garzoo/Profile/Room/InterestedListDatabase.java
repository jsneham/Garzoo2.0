package com.garzoopvt.garzoo.Dashboard.Persistence;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;


@Database(entities = { DashboardList.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class DashboardListDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "dashboardlist_db";

    private static DashboardListDatabase instance;

    public static DashboardListDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    DashboardListDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract DashboardListDao getDashboardListDao();
}
