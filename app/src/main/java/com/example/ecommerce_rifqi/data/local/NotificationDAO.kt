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

    @Query("SELECT * FROM notification ORDER BY isRead = 1 ASC, id DESC")
    fun getDataNotification(): LiveData<List<Notification>>

    @Query("UPDATE notification SET isRead = :condition WHERE id = :id")
    fun updateRead(id: Int, condition: Boolean)

    @Query("SELECT COUNT(*) FROM notification WHERE isRead = 0")
    fun countNotification(): Int

    @Query("UPDATE notification SET isRead = 1")
    fun readAll()

    @Query("DELETE FROM notification WHERE isChecked = 1")
    fun deleteCheckedProducts()
}