package com.example.ecommerce_rifqi.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import kotlin.properties.Delegates

class BottomSheetFragment(
    val dataProduct: DetailDataProduct?,
    val paymentName: String?,
    val paymentImage: Int?
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BuyProductViewModel

    private lateinit var viewModelUpdateStock: UpdateStockViewModel

    private var quantity by Delegates.notNull<Int>()

    lateinit var sharedPref: PreferencesHelper

    private var totalPriceItem: String = ""

    private lateinit var firebaseAnalytics: FirebaseAnalytics


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

        val image = dataProduct?.image
        val price = dataProduct?.harga
        val stock = dataProduct?.stock

        binding.apply {
            Glide.with(requireContext())
                .load(image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivProduct)
            if (price != null) {
                tvPriceProduct.text = formatRupiah(price.toInt())
            }
            tvStockProduct.text = "${resources.getString(R.string.txt_stock)}: $stock"

            if (price != null) {
                viewModel.setPrice(price.toInt())
            }

            viewModel.totalPrice.observe(viewLifecycleOwner) { result ->
                val totalPrice = formatRupiah(result)
                totalPriceItem = totalPrice
                btnBuy.text = "${resources.getString(R.string.txt_buy_now)} - $totalPrice"
            }

            viewModel.quantity.observe(requireActivity()) {
                tvQuantity.text = it.toString()
                quantity = it
            }

            btnPlus.setOnClickListener {
                viewModel.increaseQuantity(dataProduct?.stock)

                //On Click Button +
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Detail Product")
                buttonClick.putString("button_name", "+")
                buttonClick.putInt("total", quantity)
                buttonClick.putInt("product_id", dataProduct!!.id)
                buttonClick.putString("product_name", dataProduct.name_product)
                firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
            }

            btnMinus.setOnClickListener {
                viewModel.decreaseQuantity()

                //On Click Button -
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Detail Product")
                buttonClick.putString("button_name", "-")
                buttonClick.putInt("total", quantity)
                buttonClick.putInt("product_id", dataProduct!!.id)
                buttonClick.putString("product_name", dataProduct.name_product)
                firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
            }

            val productID = dataProduct?.id

            btnBuy.setOnClickListener {
                if (btnPayment.isVisible) {
                    val userID = sharedPref.getString(Constant.PREF_ID)
                    updateStock(userID!!, productID.toString(), quantity)

                    //On Click Button Buy Now
                    firebaseAnalytics = Firebase.analytics
                    val buttonClick = Bundle()
                    buttonClick.putString("screen_name", "Detail Product")
                    buttonClick.putString("button_name", "Buy Now – $totalPriceItem")
                    buttonClick.putInt("product_id", dataProduct!!.id)
                    buttonClick.putString("product_name", dataProduct.name_product)
                    buttonClick.putInt("product_price", dataProduct.harga.toInt())
                    buttonClick.putInt("product_total", quantity)
                    buttonClick.putString("product_totalprice", totalPriceItem)
                    buttonClick.putString("payment_method", tvPayment.text.toString())
                    firebaseAnalytics.logEvent(Constant.button_click, buttonClick)

                } else {
                    //On Click Button Buy Now For Method Payment
                    firebaseAnalytics = Firebase.analytics
                    val buttonClick = Bundle()
                    buttonClick.putString("screen_name", "Detail Product")
                    buttonClick.putString("button_name", "Buy Now – $totalPriceItem")
                    firebaseAnalytics.logEvent(Constant.button_click, buttonClick)

                    val intent = Intent(requireContext(), PaymentActivity::class.java)
                    intent.putExtra("productID", productID)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }

            btnPayment.setOnClickListener {
                val intent = Intent(requireContext(), PaymentActivity::class.java)
                intent.putExtra("productID", productID)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

                //On Click Icon Bank
                firebaseAnalytics = Firebase.analytics
                val buttonClick = Bundle()
                buttonClick.putString("screen_name", "Detail Product")
                buttonClick.putString("button_name", "BCA Virtual Account")
                firebaseAnalytics.logEvent(Constant.button_click, buttonClick)
            }
        }
    }

    private fun formatRupiah(number: Int): String {
        val formatRupiah = DecimalFormat("Rp #,###")
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
                    intent.putExtra("name", paymentName)
                    intent.putExtra("image", paymentImage)
                    intent.putExtra("total", totalPriceItem)
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

    override fun onStart() {
        super.onStart()
        if (paymentImage != null && paymentName != null) {
            binding.btnPayment.visibility = View.VISIBLE
            binding.ivPayment.setImageResource(paymentImage)
            binding.tvPayment.text = paymentName
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}