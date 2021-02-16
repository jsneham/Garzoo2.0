package com.garzoopvt.garzoo.Chat.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.garzoopvt.garzoo.Chat.Model.ChatGroup;
import com.garzoopvt.garzoo.Chat.Model.ChatIndividual;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChatIndividualDao {

    @Insert(onConflict = IGNORE)
    long[] insertData(ChatIndividual... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertList(ChatIndividual dashboardList);


    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE chatindividual SET ufname = :fname, ulname = :lname, message = :message, from_uid=:from_id, to_uid=:to_uid," +
            " file_type=:file_type  WHERE id = :id")
    void updateList(String id, String fname, String lname, String message,String from_id, String to_uid, String file_type );

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM chatindividual WHERE to_uid LIKE '%' || :query || '%' LIMIT (:pageNumber * 50)")
    LiveData<List<ChatIndividual>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM chatindividual WHERE (from_uid = :from_id AND to_uid =:to_uid ) OR (from_uid = :to_uid AND to_uid =:from_id )")
    LiveData<List<ChatIndividual>> getLIst(String from_id, String to_uid);

//    @Query("SELECT * FROM chatindividual WHERE from_uid = :from_id AND to_uid=:to_uid")
//    LiveData<ChatIndividual> getLIst(String from_id, String to_uid);
}
