package com.garzoopvt.garzoo.BuySell.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.garzoopvt.garzoo.BuySell.Model.Buy;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface BuyDao {

    @Insert(onConflict = IGNORE)
    long[] insertData(Buy... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(Buy dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE buy SET title = :title, description = :description, price = :price, " +
            "address = :address, images = :images, image_id = :image_id WHERE id = :id")
    void updateList(String id, String title, String description, String price, String address, String images, String image_id);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM buy WHERE title LIKE '%' || :query || '%' AND category_id=:category_id LIMIT (:pageNumber * 8)")
    LiveData<List<Buy>> searchList(String query, int pageNumber, String category_id);

    @Query("SELECT * FROM buy WHERE id = :id")
    LiveData<Buy> getLIst(String id);

}
