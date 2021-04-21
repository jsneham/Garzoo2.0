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
public interface MyListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertData(DashboardList... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(DashboardList dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE dashboardlists SET title = :title, description = :description, price = :price, " +
            "address = :address, images = :images, image_id = :image_id ,block_status=:block_status,status=:delete_status" +
            ",interest_status=:interest_status,latitude=:latitude,longitude=:longitude,distance=:distance,listing_id=:listing_id WHERE id = :id")
    void updateList(String id, String title, String description, String price, String address
            , String images, String image_id, String block_status , String delete_status, String interest_status, String latitude, String longitude , String distance , String listing_id );

    @Query("UPDATE dashboardlists SET status=:delete_status WHERE id = :id")
    void updateList(String id,String delete_status );

    @Query("UPDATE dashboardlists SET images=:images, image_id=:image_id WHERE id = :id")
    void updateImage(String images, String image_id,String id);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM dashboardlists WHERE data_type LIKE '%' || :query || '%' AND status='0' LIMIT (:pageNumber * 10)")
    LiveData<List<DashboardList>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM dashboardlists WHERE id = :id")
    LiveData<DashboardList> getLIst(String id);

    @Query("DELETE FROM dashboardlists")
    void deleteAll();

    @Query("DELETE FROM dashboardlists WHERE id NOT IN(:lstIDUsers)")
    void deleteOldData(List<String> lstIDUsers);
}
