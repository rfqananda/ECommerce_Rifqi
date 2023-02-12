package com.example.ecommerce_rifqi.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private var quantity by Delegates.notNull<Int>()

    lateinit var sharedPref: PreferencesHelper

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

        sharedPref = PreferencesHelper(requireContext())

        val image = dataProduct.image
        val price = dataProduct.harga
        val stock = dataProduct.stock

        binding.apply {
            Glide.with(requireContext())
                .load(image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivProduct)
            tvPriceProduct.text = formatRupiah(price.toInt())
            tvStockProduct.text = "${resources.getString(R.string.txt_stock)}: $stock"



            viewModel.setPrice(price.toInt())

            viewModel.totalPrice.observe(requireActivity()){ result ->
                val totalPrice = formatRupiah(result)
                btnBuy.text = "${resources.getString(R.string.txt_buy_now)} - $totalPrice"
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
                val userID = sharedPref.getString(Constant.PREF_ID)
                updateStock(userID!!, productID.toString(), quantity)
            }
        }
    }

    private fun formatRupiah(number: Int): String {
        val formatRupiah =  DecimalFormat("Rp #,###")
        return formatRupiah.format(number)
    }

    private fun updateStock(userID: String, productID: String?, stock: Int) {
        viewModelUpdateStock = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[UpdateStockViewModel::class.java]

        val dataStockItem = DataStockItem(id_product = productID.toString(), stock = stock)
        val listData = listOf(dataStockItem)

        viewModelUpdateStock.setUpdateStock(DataStock(userID, listData))
        viewModelUpdateStock.updateStockSuccess.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response.success.message)
                val intent = Intent(requireActivity(), RatingActivity::class.java)
                if (productID != null) {
                    intent.putExtra("id", productID.toInt())
                }
                startActivity(intent)
            }
        }
        viewModelUpdateStock.toast.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}