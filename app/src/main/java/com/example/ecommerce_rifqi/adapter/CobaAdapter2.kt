package com.example.ecommerce_rifqi.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.databinding.PaymentMethodBinding
import com.example.ecommerce_rifqi.model.DataPayment
import com.example.ecommerce_rifqi.model.PaymentMethod

class CobaAdapter2(private val listener: CobaAdapter1.OnAdapterListenerPaymentList): RecyclerView.Adapter<CobaAdapter2.ViewHolder>() {

    private val listItemHeader = ArrayList<PaymentMethod>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(users: List<PaymentMethod>) {
        listItemHeader.clear()
        listItemHeader.addAll(users)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: PaymentMethodBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return CobaAdapter2.ViewHolder(PaymentMethodBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val dataPaymentHeader = listItemHeader[position]

            lineDevider.isVisible = dataPaymentHeader.order != 0
            holder.binding.apply {

            }

            tvHeaderPaymentMethod.text = dataPaymentHeader.type.toString()
            val sort = dataPaymentHeader.data  as List<DataPayment>
            val paymentAdapter = CobaAdapter1(sort.sortedBy { it.order })
            paymentAdapter.setOnItemClick(listener)
            rvPaymentMethodList.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
            rvPaymentMethodList.adapter = paymentAdapter

            var show = false
            sectionPaymentMethod.setOnClickListener {
                val isVisible = rvPaymentMethodList.isVisible

                rvPaymentMethodList.isVisible = !isVisible
                Log.e("showStatusRV", isVisible.toString())

                if (isVisible) {
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.ic_arrow_down)
                        .centerCrop()
                        .into(ivDropDownIndicator)
                } else {
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.ic_arrow_up)
                        .centerCrop()
                        .into(ivDropDownIndicator)
                }
            }

        }
    }

    override fun getItemCount(): Int = listItemHeader.size
}