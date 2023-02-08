package com.example.ecommerce_rifqi.ui.view

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce_rifqi.data.local.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application): AndroidViewModel(application) {
    private var notificationDAO: NotificationDAO?
    private var productDB: ProductDatabase?

    init {
        productDB = ProductDatabase.getDatabase(application)
        notificationDAO = productDB?.notificationDAO()
    }

    fun addToNotification(id: Int, title: String, message: String, date: String){
        CoroutineScope(Dispatchers.IO).launch {
            val notification = Notification(id, title, message, date)
            notificationDAO?.addToNotification(notification)
        }
    }

    fun getNotification(): LiveData<List<Notification>>?{
        return notificationDAO?.getDataNotification()
    }

}