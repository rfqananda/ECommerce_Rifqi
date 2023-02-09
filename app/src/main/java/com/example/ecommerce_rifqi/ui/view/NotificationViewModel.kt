package com.example.ecommerce_rifqi.ui.view

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private var _totalData = MutableLiveData<Int>()
    val totalData: LiveData<Int> = _totalData


    fun addToNotification(id: Int, title: String, message: String, date: String){
        CoroutineScope(Dispatchers.IO).launch {
            val notification = Notification(id, title, message, date)
            notificationDAO?.addToNotification(notification)
        }
    }

    fun getNotification(): LiveData<List<Notification>>?{
        return notificationDAO?.getDataNotification()
    }

    fun isRead(id: Int, isRead: Boolean){
        CoroutineScope(Dispatchers.IO).launch {
            notificationDAO?.updateRead(id, isRead)
        }
    }

    fun getNotificationCount() {
        CoroutineScope(Dispatchers.IO).launch {
            _totalData.postValue(notificationDAO?.countNotification())
        }
    }

    fun readAllNotification() {
        CoroutineScope(Dispatchers.IO).launch {
            notificationDAO?.readAll()
        }
    }

    fun deleteNotification() {
        CoroutineScope(Dispatchers.IO).launch {
            notificationDAO?.deleteCheckedProducts()
        }
    }

}