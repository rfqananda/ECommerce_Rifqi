package com.example.ecommerce_rifqi.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.databinding.PaymentMethodListBinding
import com.example.ecommerce_rifqi.model.DataPayment

class CobaAdapter1(listProduct: List<DataPayment>): RecyclerView.Adapter<CobaAdapter1.ViewHolder>() {

    private var listProduct: List<DataPayment> = ArrayList()
    init {
        this.listProduct = listProduct
    }

    private var onItemClick: OnAdapterListenerPaymentList? = null

    fun setOnItemClick(onItemClick:  OnAdapterListenerPaymentList){
        this.onItemClick = onItemClick
    }

    class ViewHolder(val binding: PaymentMethodListBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return CobaAdapter1.ViewHolder(
            PaymentMethodListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataPayment = listProduct[position]

        holder.binding.apply {

            val drawableId = when (dataPayment.id) {
                "va_bca" -> R.drawable.ic_bca
                "va_mandiri" -> R.drawable.ic_mandiri
                "va_bri" -> R.drawable.ic_bri
                "va_bni" -> R.drawable.ic_bni
                "va_btn" -> R.drawable.ic_btn
                "va_danamon" -> R.drawable.ic_danamon
                "ewallet_gopay" -> R.drawable.ic_gopay
                "ewallet_ovo" -> R.drawable.ic_ovo
                "ewallet_dana" -> R.drawable.ic_dana
                else -> R.drawable.ic_launcher_background
            }

            Glide.with(holder.itemView)
                .load(drawableId)
                .fitCenter()
                .into(ivPaymentMethod)

            line.isVisible = dataPayment.order != 0
            tvPaymentMethod.text = dataPayment.name

            if (dataPayment.status == false){
                disable.visibility = View.VISIBLE
            } else {
                disable.visibility = View.GONE
                holder.itemView.setOnClickListener {
                    Log.e("TombolPayment", dataPayment.name.toString())
                    onItemClick?.onClick(dataPayment, drawableId)
                }
            }
        }

    }

    override fun getItemCount(): Int = listProduct.size

    interface OnAdapterListenerPaymentList {
        fun onClick(data: DataPayment, drawable: Int)
    }
}