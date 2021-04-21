package com.garzoopvt.garzoo.Chat.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.garzoopvt.garzoo.Chat.Model.ChatGroup;
import com.garzoopvt.garzoo.Chat.Model.ChatUser;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChatGroupDao {

    @Insert(onConflict = IGNORE)
    long[] insertData(ChatGroup... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(ChatGroup dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE chatgroup SET title = :fname, last_message = :last_message WHERE id = :id")
    void updateList(String id, String fname,  String last_message);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM chatgroup WHERE title LIKE '%' || :query || '%' LIMIT (:pageNumber * 8)")
    LiveData<List<ChatGroup>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM chatgroup WHERE id = :id")
    LiveData<ChatGroup> getLIst(String id);

    @Query("SELECT * FROM chatgroup")
    LiveData<List<ChatGroup>> getList();

    @Query("DELETE  FROM chatgroup")
    void delete();

    @Query("DELETE FROM chatgroup WHERE id NOT IN(:lstIDUsers)")
    void deleteOldData(List<String> lstIDUsers);
}
