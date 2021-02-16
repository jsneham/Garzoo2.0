package com.garzoopvt.garzoo.Chat.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.garzoopvt.garzoo.Chat.Model.ChatUser;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChatUserDao {

    @Insert(onConflict = IGNORE)
    long[] insertData(ChatUser... dashboardList);

    @Insert(onConflict = REPLACE)
    void insertRecipe(ChatUser dashboardList);

    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE chatuser SET fname = :fname, lname = :lname, last_message = :last_message, " +
            "chat_count = :chat_count, phone_no=:phone_no WHERE id = :id")
    void updateList(String id, String fname, String lname, String last_message, String chat_count, String phone_no);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM chatuser WHERE fname LIKE '%' || :query || '%' LIMIT (:pageNumber * 8)")
    LiveData<List<ChatUser>> searchList(String query, int pageNumber);

    @Query("SELECT * FROM chatuser WHERE id = :id")
    LiveData<ChatUser> getLIst(String id);

}
