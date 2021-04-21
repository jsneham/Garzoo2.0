package com.garzoopvt.garzoo.Promotion.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.Employement.Persistence.Converters;
import com.garzoopvt.garzoo.Promotion.Model.Promotion;


@Database(entities = { Promotion.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class PromotionDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "promotion_db";

    private static PromotionDatabase instance;

    public static PromotionDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    PromotionDatabase.class,
                    DATABASE_NAME
            ).allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract PromotionDao getListDao();
}
