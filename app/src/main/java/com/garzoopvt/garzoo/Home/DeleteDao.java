//package com.garzoopvt.garzoo.Home;
//
//import androidx.lifecycle.LiveData;
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.Query;
//
//import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
//
//import java.util.List;
//
//import static androidx.room.OnConflictStrategy.IGNORE;
//import static androidx.room.OnConflictStrategy.REPLACE;
//
//@Dao
//public interface DeleteDao {
//
//
//    @Query("DELETE FROM dashboardlists")
//    void dashboardlists();
//
//    @Query("DELETE FROM business")
//    void business();
//
//    @Query("DELETE FROM buy")
//    void buy();
//
//    @Query("DELETE FROM rent")
//    void rent();
//
//    @Query("DELETE FROM chatgroup")
//    void chatgroup();
//
//    @Query("DELETE FROM chatgrouplist")
//    void chatgrouplist();
//
//    @Query("DELETE FROM chatindividual")
//    void chatindividual();
//
//    @Query("DELETE FROM chatuser")
//    void chatuser();
//
//    @Query("DELETE FROM employment")
//    void employment();
//
//    @Query("DELETE FROM notification")
//    void notification();
//
//    @Query("DELETE FROM blockedpeople")
//    void blockedpeople();
//
//    @Query("DELETE FROM promotion")
//    void promotion();
//
//
//}
