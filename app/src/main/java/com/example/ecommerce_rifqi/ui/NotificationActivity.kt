package com.example.ecommerce_rifqi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.ListNotificationAdapter
import com.example.ecommerce_rifqi.data.local.Notification
import com.example.ecommerce_rifqi.databinding.ActivityNotificationBinding
import com.example.ecommerce_rifqi.ui.view.NotificationViewModel

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var viewModel: NotificationViewModel
    private lateinit var notificationAdapter: ListNotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationAdapter = ListNotificationAdapter()
        notificationAdapter.setOnItemClick(object : ListNotificationAdapter.OnAdapterListenerListProductFavorite{
            override fun onClick(data: Notification) {

                data.isRead = true

                val coba = data.isRead
                Log.e("Test Doang", coba.toString())

                val builder = AlertDialog.Builder(this@NotificationActivity)
                builder.setTitle("Judul Dialog")
                builder.setMessage("Pesan dalam dialog")
                builder.setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        })

        binding.apply {
            rvNotification.setHasFixedSize(true)
            rvNotification.layoutManager = LinearLayoutManager(applicationContext)
            rvNotification.adapter = notificationAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        getNotification()
    }

    private fun getNotification(){
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        viewModel.getNotification()?.observe(this){
            Log.e("Adakah datanya", it.toString())
            if (it != null) {
//                isDataEmpty(true)
                val list = mapList(it)
                if (list.isNotEmpty()) {
//                    isDataEmpty(false)
                    notificationAdapter.setData(list)
                }
            }
        }
    }

    private fun mapList(product: List<Notification>): ArrayList<Notification> {
        val listNotification = ArrayList<Notification>()
        for (data in product) {
            val dataMapped = Notification(
                data.id,
                data.title,
                data.message,
                data.date,
                data.isRead
            )
            listNotification.add(dataMapped)
        }
        return listNotification
    }
}