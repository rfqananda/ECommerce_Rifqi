package com.example.ecommerce_rifqi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ecommerce_rifqi.data.local.Product
import com.example.ecommerce_rifqi.databinding.AdapterListCartBinding
import com.example.ecommerce_rifqi.ui.view.BuyProductViewModel

class CartAdapter(private val context: Context): RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val listData = ArrayList<Product>()

    private var onItemClick: CartAdapter.OnAdapterListener? = null

    private lateinit var viewModelBuy: BuyProductViewModel


    fun setOnItemClick(onItemClick: CartAdapter.OnAdapterListener){
        this.onItemClick = onItemClick
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Product>) {
        listData.clear()
        listData.addAll(data)
        notifyItemRemoved(data.size)
    }

    class ViewHolder(val binding: AdapterListCartBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterListCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val productData = listData[position]
        holder.binding.apply {
            Glide.with(context)
                .load(productData.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(ivCart)

            tvNameCart.text = productData.name
            tvPrice.text = productData.price
            tvQuantityCart.text = productData.quantity.toString()

            btnDelete.setOnClickListener {
                onItemClick?.onDelete(productData)
            }

            btnPlus.setOnClickListener {
                onItemClick?.onIncrease(productData, tvQuantityCart)
            }

            btnMinus.setOnClickListener {
                onItemClick?.onDecrease(productData)
            }
        }
    }

    override fun getItemCount(): Int = listData.size

    interface OnAdapterListener {
        fun onDelete(data: Product)
        fun onIncrease(data: Product, tv: TextView)
        fun onDecrease(data: Product)
    }

    fun updateData(data: Product) {
        val index = listData.indexOfFirst { it.id == data.id }
        if (index != -1) {
            listData[index] = data
            notifyItemChanged(index)
        }
    }

    fun removeData(id: Int) {
        val index = listData.indexOfFirst { it.id == id }
        if (index != -1) {
            listData.removeAt(index)
            notifyItemRemoved(index)
        }
    }

}