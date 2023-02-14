package com.example.ecommerce_rifqi.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.databinding.AdapterBodyPaymentBinding
import com.example.ecommerce_rifqi.databinding.AdapterHeaderPaymentBinding
import com.example.ecommerce_rifqi.model.DataPayment
import com.example.ecommerce_rifqi.model.PaymentMethod

class PaymentMethodAdapter(private val listener: OnPaymentMethodClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val HEADER = 0
        private const val CONTENT = 1
    }

    private val paymentMethodList = mutableListOf<PaymentMethod>()
    var expandedPosition: Int = -1


    fun setData(paymentMethodList: List<PaymentMethod>) {
        this.paymentMethodList.clear()
        this.paymentMethodList.addAll(paymentMethodList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER) {
            HeaderViewHolder(AdapterHeaderPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else ContentViewHolder(AdapterBodyPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                var currentPosition = 0
                for (paymentMethod in paymentMethodList) {
                    if (position == currentPosition) {
                        holder.bind(paymentMethod.type, position, expandedPosition)
                        break
                    }
                    currentPosition += paymentMethod.data?.size?.let { it + 1 } ?: 1
                }
            }
            is ContentViewHolder -> {
                var currentPosition = 0
                for (paymentMethod in paymentMethodList) {
                    val size = paymentMethod.data?.size ?: 0
                    if (position > currentPosition && position <= currentPosition + size) {

//                        if (expandedPosition == -1 || currentPosition <= expandedPosition && position > expandedPosition + size) {
//                            holder.itemView.visibility = View.GONE
//                            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
//                        } else {
//                            holder.bind(paymentMethod.data?.get(position - currentPosition - 1))
//                            holder.itemView.visibility = View.VISIBLE
//                            holder.itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                        }
//                        break
                        holder.bind(paymentMethod.data?.sortedBy { it?.order }?.get(position - currentPosition - 1))
                        break
                    }
                    currentPosition += size + 1
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return paymentMethodList.size + paymentMethodList.sumOf { it.data?.size ?: 0 }
//        var count = paymentMethodList.size
//        for (i in paymentMethodList.indices) {
//            if (expandedPosition == -1) {
//                count += paymentMethodList[i].data?.size ?: 0
//            }
//        }
//        return count
    }

    override fun getItemViewType(position: Int): Int {
        var currentPosition = 0
        for (paymentMethod in paymentMethodList) {
            if (position == currentPosition) {
                return HEADER
            }
            val size = paymentMethod.data?.size ?: 0
            if (position > currentPosition && position <= currentPosition + size) {
                return CONTENT
            }
            currentPosition += size + 1
        }
        return super.getItemViewType(position)
    }

    inner class HeaderViewHolder(private val binding: AdapterHeaderPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(header: String?, position: Int, expandedPosition: Int) {
            binding.tvHeader.text = header
            binding.btnHeader.setOnClickListener {
                if (expandedPosition == position){
                    this@PaymentMethodAdapter.expandedPosition = -1
                } else{
                    this@PaymentMethodAdapter.expandedPosition = position
                }
                notifyDataSetChanged()
            }

            if (position == expandedPosition) {
                binding.btnHeader.text = "Hide"
            } else {
                binding.btnHeader.text = "Show"
            }
        }
    }

    inner class ContentViewHolder(private val binding: AdapterBodyPaymentBinding, private val listener: OnPaymentMethodClickListener?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(content: DataPayment?) {
            binding.tvName.text = content?.name

            val drawableId = when (content?.id) {
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
            binding.ivPhoto.setImageResource(drawableId)

            if (content?.status == false){
                binding.disable.visibility = View.VISIBLE
            } else {
                binding.disable.visibility = View.GONE
                itemView.setOnClickListener {
                    Log.d("DataPaymentIntent", "Data Payment = $drawableId")
                    if (content != null) {
                        listener?.onPaymentMethodClick(content, drawableId)
                    }
                }
            }
        }
    }

    interface OnPaymentMethodClickListener {
        fun onPaymentMethodClick(data: DataPayment, drawable: Int)
    }
}
