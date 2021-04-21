package com.garzoopvt.garzoo.Profile.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Profile.Model.BlockedPeople;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface BlockedListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertData(BlockedPeople... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(BlockedPeople dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE blockedpeople SET block_record_id = :block_record_id,fname = :fname,lname = :lname, status = :status, flag = :flag, " +
            "last_modified = :last_modified, dt = :dt, image = :image  WHERE id = :id")
    void updateList(String id, String block_record_id,String fname,String lname, String status, boolean flag, String last_modified, String dt, String image);

    @Query("UPDATE blockedpeople SET status = :status WHERE id = :id")
    void updateList(String id, String status);


    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM blockedpeople WHERE fname LIKE '%' || :query || '%' LIMIT (:pageNumber * 8)")
    LiveData<List<BlockedPeople>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM blockedpeople WHERE id = :id")
    LiveData<BlockedPeople> getLIst(String id);

    @Query("SELECT * FROM blockedpeople WHERE status='0'")
    LiveData<List<BlockedPeople>> getLIst();

    @Query("DELETE FROM blockedpeople")
    void deleteAll();

    @Query("DELETE FROM blockedpeople WHERE id NOT IN(:lstIDUsers)")
    void deleteOldData(List<String> lstIDUsers);

}
