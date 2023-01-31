package com.example.ecommerce_rifqi.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.databinding.BottomSheetLayoutBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DataStock
import com.example.ecommerce_rifqi.model.DataStockItem
import com.example.ecommerce_rifqi.model.DetailDataProduct
import com.example.ecommerce_rifqi.ui.view.BuyProductViewModel
import com.example.ecommerce_rifqi.ui.view.GetDetailProductViewModel
import com.example.ecommerce_rifqi.ui.view.UpdateStockViewModel
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DecimalFormat
import kotlin.properties.Delegates

class BottomSheetFragment(val dataProduct: DetailDataProduct): BottomSheetDialogFragment() {

    private var _binding : BottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BuyProductViewModel

    private lateinit var viewModelUpdateStock: UpdateStockViewModel

    private lateinit var dataStock: DataStock

    private lateinit var dataStockItem: DataStockItem

    private var quantity by Delegates.notNull<Int>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = BottomSheetLayoutBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity())[BuyProductViewModel::class.java]


        val image = dataProduct.image
        val price = dataProduct.harga
        val stock = dataProduct.stock

        binding.apply {
            Glide.with(requireContext())
                .load(image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivProduct)
            tvPriceProduct.text = formatRupiah(price!!.toInt())
            tvStockProduct.text = "Stock: $stock"



            viewModel.setPrice(price.toInt())

            viewModel.totalPrice.observe(requireActivity()){ it ->
                val totalPrice = formatRupiah(it)
                btnBuy.text = "Buy Now - $totalPrice"
            }

            viewModel.quantity.observe(requireActivity()){
                tvQuantity.text = it.toString()
                quantity = it
            }

            btnPlus.setOnClickListener {
                viewModel.increaseQuantity(dataProduct.stock)
            }

            btnMinus.setOnClickListener {
                viewModel.decreaseQuantity()
            }

            val productID = dataProduct.id

            btnBuy.setOnClickListener {
                updateStock(productID.toString(), quantity)
                val intent = Intent(requireActivity(), RatingActivity::class.java)
                intent.putExtra("id", productID)
                startActivity(intent)
            }

        }

        binding.apply {



//            var valueText = 1
//            totalValue.text = valueText.toString()
//            val totalPrice = price.toInt()
//
//            btnPlus.setOnClickListener {
//                valueText += 1
//                totalValue.text = valueText.toString()
//                totalPrice * valueText
//            }
//
//            btnMinus.setOnClickListener {
//                valueText -= 1
//                totalValue.text = valueText.toString()
//                totalPrice * valueText
//            }
//
//            btnBuy.text = "Buy Now - ${formatRupiah(totalPrice)}"

        }

    }

    private fun formatRupiah(number: Int): String {
        val formatRupiah =  DecimalFormat("Rp #,###")
        return formatRupiah.format(number)
    }

    private fun updateStock(productID: String?, stock: Int) {
        viewModelUpdateStock = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[UpdateStockViewModel::class.java]

        viewModelUpdateStock.setUpdateStock(productID, stock)
        viewModelUpdateStock.updateStockSuccess.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response.success.message)
            }

        }
        viewModelUpdateStock.toast.observe(requireActivity()) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}