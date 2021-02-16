package com.garzoopvt.garzoo.Employement.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.garzoopvt.garzoo.Employement.Model.Employment;


import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface EmploymentDao {

    @Insert(onConflict = IGNORE)
    long[] insertData(Employment... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(Employment dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE employment SET title = :title, description = :description, price = :price, " +
            "address = :address WHERE id = :id")
    void updateList(String id, String title, String description, String price, String address);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM employment WHERE title LIKE '%' || :query || '%' LIMIT (:pageNumber * 8)")
    LiveData<List<Employment>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM employment WHERE id = :id")
    LiveData<Employment> getLIst(String id);

}
