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
public interface InterestedListDao {

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


    @Query("UPDATE dashboardlists SET interest_status=:interest_status WHERE listing_id = :id")
    void updateInterestStatusList(String id,String interest_status );

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM dashboardlists WHERE title LIKE '%' || :query || '%' AND interest_status ='yes' AND status='0' LIMIT (:pageNumber * 8)")
    LiveData<List<DashboardList>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM dashboardlists WHERE interest_status ='yes' AND status='0'")
    LiveData<List<DashboardList>> getLIst();


    @Query("DELETE FROM dashboardlists")
    void deleteAll();

    @Query("DELETE FROM dashboardlists WHERE id NOT IN(:lstIDUsers)")
    void deleteOldData(List<String> lstIDUsers);

}
