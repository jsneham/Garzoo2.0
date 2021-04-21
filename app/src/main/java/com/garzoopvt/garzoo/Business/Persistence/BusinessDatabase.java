package com.garzoopvt.garzoo.Business.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.Business.Model.Business;

import com.garzoopvt.garzoo.Employement.Persistence.Converters;



@Database(entities = { Business.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class BusinessDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "business_db";

    private static BusinessDatabase instance;

    public static BusinessDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    BusinessDatabase.class,
                    DATABASE_NAME
            ).allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract BusinessDao getListDao();
}
