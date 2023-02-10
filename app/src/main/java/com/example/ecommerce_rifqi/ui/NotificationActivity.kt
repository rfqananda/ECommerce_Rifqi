package com.example.ecommerce_rifqi.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.ListNotificationAdapter
import com.example.ecommerce_rifqi.data.local.Notification
import com.example.ecommerce_rifqi.databinding.ActivityNotificationBinding
import com.example.ecommerce_rifqi.ui.view.NotificationViewModel
import kotlinx.coroutines.launch

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

    private fun getNotification() {
        notificationAdapter = ListNotificationAdapter(isMultipleSelect)
        viewModel.getNotification()?.observe(this) {
            Log.e("Adakah datanya", it.toString())
            if (it != null) {
                val list = mapList(it)
                if (list.isNotEmpty()) {
                    isDataEmpty(false)
                    notificationAdapter.setData(list)

                    notificationAdapter.setOnItemClick(object :
                        ListNotificationAdapter.OnAdapterListenerListProductFavorite {
                        override fun onClick(data: Notification) {

                            isRead(data.id)

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
                            isChecked(data.id, isChecked)
                        }
                    })

                    binding.apply {
                        rvNotification.setHasFixedSize(true)
                        rvNotification.layoutManager = LinearLayoutManager(applicationContext)
                        rvNotification.adapter = notificationAdapter
                    }
                } else isDataEmpty(true)
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.set_notification_item -> {
                if (binding.emptyData.isVisible) {
                    showMessage(resources.getString(R.string.txt_no_notification))
                } else setMultipleSelect()

            }
            R.id.read_notification -> {
                var isChecked = false
                for (i in 0 until binding.rvNotification.childCount) {
                    val child = binding.rvNotification.getChildAt(i)
                    val rvCheckBox = child.findViewById<CheckBox>(R.id.cb_notification)
                    if (rvCheckBox.isChecked) {
                        isChecked = true
                        break
                    }
                }

                if (!isChecked) {
                    showMessage("Belum Ada Satupun yang Diceklis")
                } else viewModel.readAllNotification()

                onBackPressed()
            }
            R.id.delete -> {
                var isChecked = false
                for (i in 0 until binding.rvNotification.childCount) {
                    val child = binding.rvNotification.getChildAt(i)
                    val rvCheckBox = child.findViewById<CheckBox>(R.id.cb_notification)
                    if (rvCheckBox.isChecked) {
                        isChecked = true
                        break
                    }
                }

                if (!isChecked) {
                    showMessage("Belum Ada Satupun yang Diceklis")
                } else {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.apply {
                        setTitle("Delete Product?")
                        setMessage("Are you sure you want to delete this notification?")
                        setNegativeButton("Cancel") { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }
                        setPositiveButton("Delete") { dialogInterface, i ->
                            viewModel.deleteNotification()
                            binding.rvNotification.adapter?.notifyDataSetChanged()
                            onBackPressed()
                        }
                    }.show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun isRead(id: Int) {
        viewModel.isRead(id)
    }

    private fun isChecked(id: Int, isChecked: Boolean) {
        viewModel.isChecked(id, isChecked)
    }

    private fun unselectNotification() {
        viewModel.unselectNotification()
    }

    private fun mapList(product: List<Notification>): ArrayList<Notification> {
        val listNotification = ArrayList<Notification>()
        for (data in product) {
            val dataMapped = Notification(
                data.id,
                data.title,
                data.message,
                data.date,
                data.isRead,
                data.isChecked
            )
            listNotification.add(dataMapped)
        }
        return listNotification
    }

    private fun setMultipleSelect() {
        isMultipleSelect = !isMultipleSelect
        getNotification()

        if (isMultipleSelect) {
            myMenu.findItem(R.id.read_notification)?.isVisible = true
            myMenu.findItem(R.id.delete)?.isVisible = true
            myMenu.findItem(R.id.set_notification_item)?.isVisible = false


            binding.tvToolbarTitle.text = "Multiple Select"
        } else {
            myMenu.findItem(R.id.read_notification)?.isVisible = false
            myMenu.findItem(R.id.delete)?.isVisible = false
            myMenu.findItem(R.id.set_notification_item)?.isVisible = true



            binding.tvToolbarTitle.text = resources.getString(R.string.txt_notification)
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
            unselectNotification()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (isMultipleSelect) {
            setMultipleSelect()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        lifecycleScope.launch {
            unselectNotification()

        }
        return true
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isDataEmpty(isEmpty: Boolean) {
        binding.apply {
            if (isEmpty) {
                emptyData.visibility = View.VISIBLE
                rvNotification.visibility = View.GONE
            } else {
                emptyData.visibility = View.GONE
                rvNotification.visibility = View.VISIBLE
            }
        }
    }
}