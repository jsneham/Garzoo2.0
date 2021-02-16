package com.garzoopvt.garzoo.Chat.Persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.garzoopvt.garzoo.Chat.Model.ChatIndividual;

import com.garzoopvt.garzoo.Dashboard.Persistence.Converters;



    @Database(entities = { ChatIndividual.class}, version = 1)
    @TypeConverters({Converters.class})
    public abstract  class ChatIndividualDatabase extends RoomDatabase {

        public static final String DATABASE_NAME = "chatindividual_db";

        private static ChatIndividualDatabase instance;

        public static ChatIndividualDatabase getInstance(final Context context){
            if(instance == null){
                instance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        ChatIndividualDatabase.class,
                        DATABASE_NAME
                ).build();
            }
            return instance;
        }

        public abstract ChatIndividualDao getListDao();
    }

