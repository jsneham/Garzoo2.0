package com.garzoopvt.garzoo.Promotion.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.garzoopvt.garzoo.Promotion.Model.Promotion;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PromotionDao {

    @Insert(onConflict = IGNORE)
    long[] insertData(Promotion... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(Promotion dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE promotion SET title = :title, description = :description, address = :address WHERE id = :id")
    void updateList(String id, String title, String description,  String address);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM promotion WHERE title LIKE '%' || :query || '%' LIMIT (:pageNumber * 8)")
    LiveData<List<Promotion>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM promotion WHERE id = :id")
    LiveData<Promotion> getLIst(String id);

}
