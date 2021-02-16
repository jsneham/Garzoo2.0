package com.garzoopvt.garzoo.BuySell.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.garzoopvt.garzoo.BuySell.Model.Category;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CategoryDao {

    @Insert(onConflict = IGNORE)
    long[] insertData(Category... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(Category dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE category SET name = :name, image = :image  WHERE id = :id")
    void updateList(String id, String name, String image);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM category WHERE name LIKE '%' || :query ")
    LiveData<List<Category>> searchList(String query);

    @Query("SELECT * FROM category WHERE id = :id")
    LiveData<Category> getLIst(String id);

}
