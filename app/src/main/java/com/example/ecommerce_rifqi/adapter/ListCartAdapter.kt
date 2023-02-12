package com.example.ecommerce_rifqi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ecommerce_rifqi.data.local.Product
import com.example.ecommerce_rifqi.databinding.AdapterListCartBinding
import java.text.DecimalFormat

class ListCartAdapter(private val context: Context): RecyclerView.Adapter<ListCartAdapter.ViewHolder>() {

    private val listData = ArrayList<Product>()

    private var onItemClick: ListCartAdapter.OnAdapterListener? = null


    fun setOnItemClick(onItemClick: ListCartAdapter.OnAdapterListener){
        this.onItemClick = onItemClick
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Product>) {
        listData.clear()
        listData.addAll(data)
        notifyItemRemoved(data.size)
        notifyDataSetChanged()
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
            tvPrice.text = formatRupiah(productData.price.toInt())
            tvQuantityCart.text = productData.quantity.toString()

            holder.itemView.setOnClickListener {
                onItemClick?.onClick(productData)
            }

            btnDelete.setOnClickListener {
                onItemClick?.onDelete(productData)
            }

            btnPlus.setOnClickListener {
                onItemClick?.onIncrease(productData, position)
            }

            btnMinus.setOnClickListener {
                onItemClick?.onDecrease(productData, position)
            }

            cbCart.isChecked = productData.check_button
            cbCart.setOnCheckedChangeListener { _, isChecked ->
                productData.check_button = isChecked
                onItemClick?.onChecked(productData, isChecked)
            }
        }
    }

    override fun getItemCount(): Int = listData.size

    interface OnAdapterListener {
        fun onClick(data: Product)
        fun onDelete(data: Product)
        fun onIncrease(data: Product, position: Int)
        fun onDecrease(data: Product, position: Int)
        fun onChecked(data: Product, isChecked: Boolean)
    }

    fun removeData(id: Int) {
        val index = listData.indexOfFirst { it.id == id }
        if (index != -1) {
            listData.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    private fun formatRupiah(angka: Int): String {
        val formatRupiah =  DecimalFormat("Rp #,###")
        return formatRupiah.format(angka)
    }


}