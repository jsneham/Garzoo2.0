package com.garzoopvt.garzoo.Profile.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserMoreListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertData(DashboardList... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(DashboardList dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE dashboardlists SET title = :title, description = :description, price = :price, " +
            "address = :address,data_type = :type,user_id = :user_id WHERE id = :id")
    void updateList(String id, String title, String description, String price, String address, String type, String user_id);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM dashboardlists WHERE data_type LIKE '%' || :query || '%' LIMIT (:pageNumber * 8)")
    LiveData<List<DashboardList>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM dashboardlists WHERE user_id = :id AND data_type=:data_type  LIMIT (:pageNumber * 8)")
    LiveData<List<DashboardList>> getLIst(String id, int pageNumber, String data_type);

    @Query("DELETE FROM dashboardlists")
    void deleteAll();

    @Query("DELETE FROM dashboardlists WHERE id NOT IN(:lstIDUsers)")
    void deleteOldData(List<String> lstIDUsers);
}
