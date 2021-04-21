package com.garzoopvt.garzoo.Rent.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.garzoopvt.garzoo.Rent.Model.Rent;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RentDao {

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    long[] insertData(Rent... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(Rent dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE rent SET title = :title, description = :description, price = :price, " +
            "address = :address, images = :images, image_id = :image_id" +
            " ,block_status=:block_status,status=:delete_status" +
            ",interest_status=:interest_status,latitude=:latitude,longitude=:longitude,distance=:distance WHERE id = :id")
    void updateList(String id, String title, String description, String price, String address, String images,
                    String image_id, String block_status ,String delete_status,String interest_status,String latitude,String longitude,String distance);

    @Query("UPDATE rent SET status=:delete_status WHERE id = :id")
    void updateList(String id,String delete_status );

    @Query("UPDATE rent SET interest_status=:interest_status WHERE id = :id")
    void updateInterestStatusList(String id,String interest_status );

    @Query("DELETE from rent WHERE user_id = :id")
    void delete(String id);

    @Query("UPDATE rent SET images=:images, image_id=:image_id WHERE id = :id")
    void updateImage(String images, String image_id,String id);
    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM rent WHERE title LIKE '%' || :query || '%'AND category_id=:category_id AND status='0' ORDER BY distance ASC LIMIT (:pageNumber * 10)")
    LiveData<List<Rent>> searchList(String query, int pageNumber,String category_id);


    @Query("SELECT * FROM rent WHERE title LIKE '%' || :query || '%' AND status='0' ORDER BY dt DESC LIMIT (:pageNumber * 10)")
    LiveData<List<Rent>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM rent WHERE id = :id")
    LiveData<Rent> getLIst(String id);

    @Query("DELETE FROM rent")
    void deleteAll();

    @Query("DELETE FROM rent WHERE id NOT IN(:lstIDUsers)")
    void deleteOldData(List<String> lstIDUsers);
}
