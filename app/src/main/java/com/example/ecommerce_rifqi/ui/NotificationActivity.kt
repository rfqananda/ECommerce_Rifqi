package com.example.ecommerce_rifqi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.ListNotificationAdapter
import com.example.ecommerce_rifqi.data.local.Notification
import com.example.ecommerce_rifqi.databinding.ActivityNotificationBinding
import com.example.ecommerce_rifqi.ui.view.NotificationViewModel
import kotlinx.coroutines.launch
import okhttp3.internal.checkOffsetAndCount

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var viewModel: NotificationViewModel
    private lateinit var notificationAdapter: ListNotificationAdapter

    private var isMultipleSelect = false
    private lateinit var myMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomToolbar()

    }

    override fun onStart() {
        super.onStart()
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        getNotification()
    }

    private fun getNotification(){
        notificationAdapter = ListNotificationAdapter(isMultipleSelect)
        viewModel.getNotification()?.observe(this){
            Log.e("Adakah datanya", it.toString())
            if (it != null) {
//                isDataEmpty(true)
                val list = mapList(it)
                if (list.isNotEmpty()) {
//                    isDataEmpty(false)
                    notificationAdapter.setData(list)

                    notificationAdapter.setOnItemClick(object : ListNotificationAdapter.OnAdapterListenerListProductFavorite{
                        override fun onClick(data: Notification) {

                            isRead(data.id, true)

                            val coba = data.isRead
                            Log.e("Test Doang", coba.toString())

                            val builder = AlertDialog.Builder(this@NotificationActivity)
                            builder.setTitle(data.title)
                            builder.setMessage(data.message)
                            builder.setPositiveButton("OK") { dialog, which ->
                                dialog.dismiss()
                            }
                            val dialog = builder.create()
                            dialog.show()
                        }

                        override fun onChecked(data: Notification, isChecked: Boolean) {

                        }
                    })

                    binding.apply {
                        rvNotification.setHasFixedSize(true)
                        rvNotification.layoutManager = LinearLayoutManager(applicationContext)
                        rvNotification.adapter = notificationAdapter
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notification, menu)
        if (menu != null) {
            myMenu = menu
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.set_notification_item -> {
                setMultipleSelect()
            }
            R.id.read_all -> {
                viewModel.readAllNotification()
                onBackPressed()
            }
            R.id.delete -> {
                viewModel.deleteNotification()
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }




    private fun isRead(id: Int, isRead: Boolean){
        viewModel.isRead(id, isRead)
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

    private fun setNotificationListData() {
        lifecycleScope.launch {
//            notificationViewModel.getAllNotification().collect() { data ->
//                with(binding) {
//                    tvNoNotifMsg.isVisible = data.isNullOrEmpty()
//                    rvNotification.isVisible = !data.isNullOrEmpty()
//
//                    if (!data.isNullOrEmpty()) {
//                        adapter = NotificationListAdapter(
//                            isMultipleSelect = isMultipleSelect,
//                            context = this@NotificationActivity,
//                            onItemClicked = { data ->
//                                onNotificationItemClicked(data)
//                            },
//                            onCheckboxChecked = { data ->
//                                onCheckboxChecked(data)
//                            }
//                        )
//                        val linearLayoutManager = LinearLayoutManager(this@NotificationActivity)
//                        adapter.submitList(data)
//                        rvNotification.adapter = adapter
//                        rvNotification.layoutManager = linearLayoutManager
//                        rvNotification.setHasFixedSize(true)
//                    }
//                }
//            }
        }
    }


    private fun setMultipleSelect() {
        isMultipleSelect = !isMultipleSelect
        getNotification()

        if (isMultipleSelect) {
            myMenu.findItem(R.id.read_all)?.isVisible = true
            myMenu.findItem(R.id.delete)?.isVisible = true
            myMenu.findItem(R.id.set_notification_item)?.isVisible = false

            binding.tvToolbarTitle.text = "Multiple Select"
        } else {
            myMenu.findItem(R.id.read_all)?.isVisible = false
            myMenu.findItem(R.id.delete)?.isVisible = false
            myMenu.findItem(R.id.set_notification_item)?.isVisible = true

            binding.tvToolbarTitle.text = "Notification"
        }
    }


    private fun setCustomToolbar() {
        setSupportActionBar(binding.customToolbar)
        with(supportActionBar) {
            this?.setDisplayShowTitleEnabled(false)
            this?.setDisplayShowHomeEnabled(true)
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onBackPressed() {
        if (isMultipleSelect) {
            setMultipleSelect()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        lifecycleScope.launch {
//            notificationViewModel.setAllUnchecked()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (isMultipleSelect) {
            setMultipleSelect()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        lifecycleScope.launch {
//            notificationViewModel.setAllUnchecked()
        }
        return true
    }
}