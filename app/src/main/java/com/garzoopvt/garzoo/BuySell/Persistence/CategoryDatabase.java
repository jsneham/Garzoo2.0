package com.garzoopvt.garzoo.BuySell.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.garzoopvt.garzoo.Dashboard.Persistence.Converters;


@Database(entities = { Category.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class CategoryDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "category_db";

    private static CategoryDatabase instance;

    public static CategoryDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    CategoryDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract CategoryDao getListDao();
}
