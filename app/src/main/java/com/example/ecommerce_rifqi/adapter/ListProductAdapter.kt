package com.example.ecommerce_rifqi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ecommerce_rifqi.databinding.AdapterLayoutListBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DataProduct
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ListProductAdapter : PagingDataAdapter<DataProduct, ListProductAdapter.ViewHolder>(DIFF_CALLBACK) {

    lateinit var sharedPref: PreferencesHelper

    private var onItemClick: OnAdapterListenerListProduct? = null

    fun setOnItemClick(onItemClick: OnAdapterListenerListProduct){
        this.onItemClick = onItemClick
    }

    class ViewHolder(val binding: AdapterLayoutListBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterLayoutListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val listProduct = getItem(position)
        holder.binding.apply {
            Glide.with(holder.itemView.context)
                .load(listProduct!!.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPhoto)
            val date = formatDate(listProduct!!.date, holder.itemView.context)

            tvName.text = listProduct.name_product
            tvPrice.text = formatRupiah(listProduct.harga.toInt())
            tvDate.text = date
            ratingBar.rating = listProduct!!.rate.toFloat()

        }

        holder.itemView.setOnClickListener {
            onItemClick?.onClick(listProduct!!)
        }

    }


    private fun formatRupiah(angka: Int): String {
        val formatRupiah =  DecimalFormat("Rp #,###")
        return formatRupiah.format(angka)
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: String, context: Context): String{
        sharedPref = PreferencesHelper(context)
        val language = sharedPref.getString(Constant.PREF_LANG)

        return if (language != null) {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val inputDate = format.parse(date)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale(language))
            dateFormat.format(inputDate!!)
        } else {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val inputDate = format.parse(date)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
            dateFormat.format(inputDate!!)
        }
    }

    interface OnAdapterListenerListProduct {
        fun onClick(data: DataProduct)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataProduct>() {
            override fun areItemsTheSame(oldItem: DataProduct, newItem: DataProduct): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DataProduct, newItem: DataProduct): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}