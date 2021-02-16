package com.garzoopvt.garzoo.Employement.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.Employement.Model.Employment;


@Database(entities = { Employment.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class EmploymentDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "employment_db";

    private static EmploymentDatabase instance;

    public static EmploymentDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    EmploymentDatabase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }

    public abstract EmploymentDao getListDao();
}
