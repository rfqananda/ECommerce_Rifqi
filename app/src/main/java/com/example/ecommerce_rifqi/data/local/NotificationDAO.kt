package com.example.ecommerce_rifqi.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToNotification(dataNotification: Notification)

    @Query("SELECT * FROM notification")
    fun getDataNotification(): LiveData<List<Notification>>

    @Query("UPDATE notification SET isRead = :condition WHERE id = :id")
    fun updateRead(id: Int, condition: Boolean)
}