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
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.ListNotificationAdapter
import com.example.ecommerce_rifqi.data.local.Notification
import com.example.ecommerce_rifqi.databinding.ActivityNotificationBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.ui.view.NotificationViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var viewModel: NotificationViewModel
    private lateinit var notificationAdapter: ListNotificationAdapter
    private var isMultipleSelect = false
    private lateinit var myMenu: Menu
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        setCustomToolbar()
        getNotification()
    }

    override fun onResume() {
        super.onResume()

        //Firebase Onload
        firebaseAnalytics = Firebase.analytics
        val onload = Bundle()
        onload.putString("screen_name", "Notification")
        onload.putString("screen_class", this.javaClass.simpleName)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, onload)
    }

    private fun getNotification() {
        notificationAdapter = ListNotificationAdapter(isMultipleSelect)
        binding.apply {
            rvNotification.adapter = notificationAdapter
            rvNotification.layoutManager = LinearLayoutManager(this@NotificationActivity)
            rvNotification.setHasFixedSize(true)
        }
        viewModel.getNotification()?.observe(this) {
            Log.e("Adakah datanya", it.toString())
            if (it != null) {
                val list = mapList(it)
                if (list.isNotEmpty()) {
                    isDataEmpty(false)
                    notificationAdapter.setData(list)

                    notificationAdapter.setOnItemClick(object :
                        ListNotificationAdapter.OnAdapterListenerNotification {
                        override fun onClick(data: Notification) {
                            isRead(data.id)

                            val builder = AlertDialog.Builder(this@NotificationActivity)
                            builder.setTitle(data.title)
                            builder.setMessage(data.message)
                            builder.setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            val dialog = builder.create()
                            dialog.show()

                            //Firebase On Click Item Notification
                            firebaseAnalytics = Firebase.analytics
                            val selectItem = Bundle()
                            selectItem.putString("screen_name", "Notification")
                            selectItem.putString("title", data.title)
                            selectItem.putString("message", data.message)
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, selectItem)
                        }

                        override fun onChecked(data: Notification, isChecked: Boolean, position: Int) {
                            isChecked(data.id, isChecked)

                            //Firebase On Select CheckBox
                            firebaseAnalytics = Firebase.analytics
                            val selectItem = Bundle()
                            selectItem.putString("screen_name", "Multiple Select")
                            selectItem.putString("title", data.title)
                            selectItem.putString("message", data.message)
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, selectItem)
                        }
                    })
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
        var count = 0
        for (i in 0 until binding.rvNotification.childCount) {
            val child = binding.rvNotification.getChildAt(i)
            val rvCheckBox = child.findViewById<CheckBox>(R.id.cb_notification)
            if (rvCheckBox.isChecked) {
                count++
            }
        }

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
                    showMessage(resources.getString(R.string.txt_checked))
                } else{
                    viewModel.readAllNotification()
                }

                //Firebase On Click Read Icon
                firebaseAnalytics = Firebase.analytics
                val selectItem = Bundle()
                selectItem.putString("screen_name", "Multiple Select")
                selectItem.putString("button_name", "Read Icon")
                selectItem.putInt("total_select_item", count)
                firebaseAnalytics.logEvent(Constant.button_click, selectItem)

                onBackPressed()
            }
            R.id.delete -> {
                //Firebase On Click Delete Icon
                firebaseAnalytics = Firebase.analytics
                val selectItem = Bundle()
                selectItem.putString("screen_name", "Multiple Select")
                selectItem.putString("button_name", "Delete Icon")
                selectItem.putInt("total_select_item", count)
                firebaseAnalytics.logEvent(Constant.button_click, selectItem)

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
                    showMessage(resources.getString(R.string.txt_checked))
                } else {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.apply {
                        setTitle(resources.getString(R.string.txt_delete_product))
                        setMessage(resources.getString(R.string.txt_delete_notification))
                        setNegativeButton(resources.getString(R.string.txt_cancel)) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                        setPositiveButton(resources.getString(R.string.txt_delete)) { _, _ ->
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

            binding.tvToolbarTitle.text = resources.getString(R.string.txt_multiple)

            //Firebase OnClick Multiple Select Icon
            firebaseAnalytics = Firebase.analytics
            val buttonClick = Bundle()
            buttonClick.putString("screen_name", "Notification")
            buttonClick.putString("button_name", "Multiple Select Item")
            firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
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

            //Firebase On Click Back Button
            firebaseAnalytics = Firebase.analytics
            val buttonClick = Bundle()
            buttonClick.putString("screen_name", "Multiple Select")
            buttonClick.putString("button_name", "Back Icon")
            firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
        } else {
            onBackPressedDispatcher.onBackPressed()
            //Firebase On Click Back Button
            firebaseAnalytics = Firebase.analytics
            val buttonClick = Bundle()
            buttonClick.putString("screen_name", "Notification")
            buttonClick.putString("button_name", "Back Icon")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, buttonClick)
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