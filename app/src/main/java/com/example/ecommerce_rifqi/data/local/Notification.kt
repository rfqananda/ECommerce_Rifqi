package com.example.ecommerce_rifqi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class Notification (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String = "",
    var message: String = "",
    var date: String = "",
    var isRead : Boolean = false,
    var isChecked : Boolean = false)