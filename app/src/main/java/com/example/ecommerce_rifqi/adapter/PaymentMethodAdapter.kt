package com.example.ecommerce_rifqi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce_rifqi.databinding.AdapterBodyPaymentBinding
import com.example.ecommerce_rifqi.databinding.AdapterHeaderPaymentBinding
import com.example.ecommerce_rifqi.model.DataPayment
import com.example.ecommerce_rifqi.model.PaymentMethod

class PaymentMethodAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val HEADER = 0
        private const val CONTENT = 1
    }

    private val paymentMethodList = mutableListOf<PaymentMethod>()

    fun setData(paymentMethodList: List<PaymentMethod>) {
        this.paymentMethodList.clear()
        this.paymentMethodList.addAll(paymentMethodList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == HEADER) {
            HeaderViewHolder(AdapterHeaderPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else ContentViewHolder(AdapterBodyPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                var currentPosition = 0
                for (paymentMethod in paymentMethodList) {
                    if (position == currentPosition) {
                        holder.bind(paymentMethod.type)
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
                        holder.bind(paymentMethod.data?.get(position - currentPosition - 1))
                        break
                    }
                    currentPosition += size + 1
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return paymentMethodList.size + paymentMethodList.sumBy { it.data?.size ?: 0 }
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

    class HeaderViewHolder(private val binding: AdapterHeaderPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: String?) {
            binding.tvHeader.text = header
        }
    }

    class ContentViewHolder(private val binding: AdapterBodyPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(content: DataPayment?) {
            binding.tvName.text = content?.name
        }
    }

}
