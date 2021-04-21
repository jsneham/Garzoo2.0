package com.garzoopvt.garzoo.Promotion.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.garzoopvt.garzoo.Promotion.Model.Promotion;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PromotionDao {

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    long[] insertData(Promotion... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(Promotion dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE promotion SET title = :title, description = :description," +
            "address = :address, images = :images, image_id = :image_id ,block_status=:block_status" +
            ",status=:delete_status" +
            ",interest_status=:interest_status,latitude=:latitude,longitude=:longitude,distance=:distance WHERE id = :id")
    void updateList(String id, String title, String description,  String address, String block_status
            , String delete_status, String interest_status, String latitude, String longitude, String distance, String images,String image_id );

    @Query("UPDATE promotion SET status=:delete_status WHERE id = :id")
    void updateList(String id,String delete_status );

    @Query("UPDATE promotion SET interest_status=:interest_status WHERE id = :id")
    void updateInterestStatusList(String id,String interest_status );

    @Query("DELETE from promotion WHERE user_id = :id")
    void delete(String id);

    @Query("UPDATE promotion SET images=:images, image_id=:image_id WHERE id = :id")
    void updateImage(String images, String image_id,String id);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM promotion WHERE title LIKE '%' || :query || '%' AND status='0' ORDER BY distance ASC LIMIT (:pageNumber * 10)")
    LiveData<List<Promotion>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM promotion WHERE id = :id")
    LiveData<Promotion> getLIst(String id);

    @Query("DELETE FROM promotion")
    void deleteAll();

    @Query("DELETE FROM promotion WHERE id NOT IN(:lstIDUsers)")
    void deleteOldData(List<String> lstIDUsers);

}
