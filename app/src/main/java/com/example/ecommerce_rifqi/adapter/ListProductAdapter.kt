package com.example.ecommerce_rifqi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.databinding.AdapterLayoutListBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DataProduct
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListProductAdapter(val context:Context) : RecyclerView.Adapter<ListProductAdapter.ViewHolder>() {

    lateinit var sharedPref: PreferencesHelper

    private var onItemClick: OnAdapterListenerListProduct? = null

    private val listData = ArrayList<DataProduct>()

    fun setOnItemClick(onItemClick: OnAdapterListenerListProduct){
        this.onItemClick = onItemClick
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<DataProduct>) {
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()
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
        val listProduct = listData[position]


        holder.binding.apply {
            Glide.with(context)
                .load(listProduct.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPhoto)
            val date = formatDate(listProduct.date)

            tvName.text = listProduct.name_product
            tvPrice.text = formatRupiah(listProduct.harga.toInt())
            tvDate.text = date
            ratingBar.rating = listProduct.rate.toFloat()

        }

        holder.itemView.setOnClickListener {
            onItemClick?.onClick(listProduct)
        }

    }

    override fun getItemCount(): Int = listData.size

    private fun formatRupiah(angka: Int): String {
        val formatRupiah =  DecimalFormat("Rp #,###")
        return formatRupiah.format(angka)
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: String): String{
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
}