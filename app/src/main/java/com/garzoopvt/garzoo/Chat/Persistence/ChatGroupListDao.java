package com.garzoopvt.garzoo.Chat.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.garzoopvt.garzoo.Chat.Model.ChatGroupList;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChatGroupListDao {

    @Insert(onConflict = IGNORE)
    long[] insertData(ChatGroupList... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertList(ChatGroupList dashboardList);


    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE chatgrouplist SET fname = :fname, lname = :lname, comment = :message, user_id=:from_id, pd_id=:to_uid," +
            " file_type=:file_type  WHERE id = :id")
    void updateList(String id, String fname, String lname, String message,String from_id, String to_uid, String file_type );

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM chatgrouplist WHERE pd_id LIKE '%' || :query || '%' ")
    LiveData<List<ChatGroupList>> searchList(String query);

    @Query("SELECT * FROM chatgrouplist WHERE pd_id =:pd_id ORDER BY dt ASC")
    LiveData<List<ChatGroupList>> getLIst(String pd_id);

//    @Query("SELECT * FROM chatindividual WHERE from_uid = :from_id AND to_uid=:to_uid")
//    LiveData<ChatIndividual> getLIst(String from_id, String to_uid);
}
