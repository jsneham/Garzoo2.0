//package com.garzoopvt.garzoo.Home;
//
//import android.content.Context;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import androidx.room.TypeConverters;
//
//import com.garzoopvt.garzoo.Employement.Model.Employment;
//import com.garzoopvt.garzoo.Employement.Persistence.Converters;
//import com.garzoopvt.garzoo.Employement.Persistence.EmploymentDao;
//
//
//@Database(entities = { DeleteDb.class}, version = 1)
//@TypeConverters({Converters.class})
//public abstract  class DeletetDatabase extends RoomDatabase {
//
//    public static final String DATABASE_NAME = "deletedb";
//
//    private static DeletetDatabase instance;
//
//    public static DeletetDatabase getInstance(final Context context){
//        if(instance == null){
//            instance = Room.databaseBuilder(
//                    context.getApplicationContext(),
//                    DeletetDatabase.class,
//                    DATABASE_NAME
//            ).allowMainThreadQueries().build();
//        }
//        return instance;
//    }
//
//    public abstract DeleteDao getListDao();
//}
