package com.example.ecommerce_rifqi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.data.local.Notification
import com.example.ecommerce_rifqi.databinding.AdapterNotificationBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.*

class ListNotificationAdapter(private val isMultipleSelect: Boolean) :
    RecyclerView.Adapter<ListNotificationAdapter.ViewHolder>() {

    lateinit var sharedPref: PreferencesHelper

    private val listData = ArrayList<Notification>()


    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Notification>) {
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()
    }

    private var onItemClick: ListNotificationAdapter.OnAdapterListenerListProductFavorite? = null

    fun setOnItemClick(onItemClick: ListNotificationAdapter.OnAdapterListenerListProductFavorite) {
        this.onItemClick = onItemClick
    }

    class ViewHolder(val binding: AdapterNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listProduct = listData[position]

        holder.binding.apply {
            if (isMultipleSelect) {
                cbNotification.visibility = View.VISIBLE
                cbNotification.isChecked = listProduct.isChecked
            } else {
                cbNotification.visibility = View.GONE
            }

            val date = formatDate(listProduct.date, holder.itemView.context)

            tvTitle.text = listProduct.title
            tvMessage.text = listProduct.message
            tvDate.text = date

            cardViewNotification.setCardBackgroundColor(
                if (listProduct.isRead) Color.WHITE else ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.blue_notification
                )
            )

            cardViewNotification.setOnClickListener {
                if (!isMultipleSelect) {
                    onItemClick?.onClick(listProduct)
                } else holder.binding.cardViewNotification.isClickable = false

            }

            cbNotification.isChecked = listProduct.isChecked
            cbNotification.setOnCheckedChangeListener { _, isChecked ->
                listProduct.isChecked = isChecked
                onItemClick?.onChecked(listProduct, isChecked)
            }
        }
    }

    override fun getItemCount(): Int = listData.size

    fun removeData(id: Int) {
        val index = listData.indexOfFirst { it.id == id }
        if (index != -1) {
            listData.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    interface OnAdapterListenerListProductFavorite {
        fun onClick(data: Notification)
        fun onChecked(data: Notification, isChecked: Boolean)
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: String, context: Context): String {
        sharedPref = PreferencesHelper(context)
        val language = sharedPref.getString(Constant.PREF_LANG)

        return if (language != null) {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val inputDate = format.parse(date)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale(language))
            dateFormat.format(inputDate!!)
        } else {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val inputDate = format.parse(date)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
            dateFormat.format(inputDate!!)
        }
    }
}