package com.garzoopvt.garzoo.Rent.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.BuySell.Model.Category;
import com.garzoopvt.garzoo.Dashboard.Persistence.Converters;


@Database(entities = { Category.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class RentCategoryDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "rent_category_db";

    private static RentCategoryDatabase instance;

    public static RentCategoryDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    RentCategoryDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract RentCategoryDao getListDao();
}
