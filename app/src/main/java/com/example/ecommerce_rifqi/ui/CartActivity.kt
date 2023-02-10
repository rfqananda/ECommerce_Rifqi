package com.example.ecommerce_rifqi.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.ListCartAdapter
import com.example.ecommerce_rifqi.data.local.CheckedProduct
import com.example.ecommerce_rifqi.data.local.Product
import com.example.ecommerce_rifqi.databinding.ActivityCartBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.DataStock
import com.example.ecommerce_rifqi.model.DataStockItem
import com.example.ecommerce_rifqi.ui.view.BuyProductViewModel
import com.example.ecommerce_rifqi.ui.view.GetProductCartViewModel
import com.example.ecommerce_rifqi.ui.view.UpdateStockViewModel
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    private lateinit var dataProductAdapter: ListCartAdapter

    private lateinit var viewModel: GetProductCartViewModel

    private lateinit var viewModelBuy: BuyProductViewModel

    private lateinit var viewModelUpdateStock: UpdateStockViewModel

    private var isFirstTime = true

    lateinit var sharedPref: PreferencesHelper

    private var recyclerViewState: Parcelable? = null

    private lateinit var loading: LoadingDialog

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loading = LoadingDialog(this)

        dataProductAdapter = ListCartAdapter(this)

        sharedPref = PreferencesHelper(this)

        viewModel = ViewModelProvider(this)[GetProductCartViewModel::class.java]
        viewModelBuy = ViewModelProvider(this)[BuyProductViewModel::class.java]

        recyclerViewState = savedInstanceState?.getParcelable("recycler_view_state")


        binding.apply {
            rvCart.setHasFixedSize(true)
            rvCart.layoutManager = LinearLayoutManager(applicationContext)
            rvCart.adapter = dataProductAdapter
            rvCart.layoutManager?.onRestoreInstanceState(recyclerViewState)

            viewModelBuy.totalPrice.observe(this@CartActivity) {
                tvTotal.text = it.toString()
            }

            cbCartActivity.setOnClickListener {
                lifecycleScope.launch {
                    if (cbCartActivity.isChecked) {
                        viewModel.selectAll(1)
                        for (i in 0 until rvCart.childCount) {
                            val child = rvCart.getChildAt(i)
                            val recyclerViewCB = child.findViewById<CheckBox>(R.id.cb_cart)
                            recyclerViewCB.isChecked = cbCartActivity.isChecked
                        }
                    } else {
                        viewModel.selectAll(0)
                        for (i in 0 until rvCart.childCount) {
                            val child = rvCart.getChildAt(i)
                            val recyclerViewCB = child.findViewById<CheckBox>(R.id.cb_cart)
                            recyclerViewCB.isChecked = cbCartActivity.isChecked
                        }
                    }
                }

            }

            lifecycleScope.launch {
                viewModel.getProduct()?.observe(this@CartActivity) { result ->
                    val filterData = result.filter { it.check_button }
                    cbCartActivity.isChecked = result.size == filterData.size
                }
            }

            btnBuy.setOnClickListener {
                loading.startLoading()
                var isChecked = false
                for (i in 0 until rvCart.childCount) {
                    val child = rvCart.getChildAt(i)
                    val rvCheckBox = child.findViewById<CheckBox>(R.id.cb_cart)
                    if (rvCheckBox.isChecked) {
                        isChecked = true
                        break
                    }
                }

                if (!isChecked) {
                    loading.isDismiss()
                    showMessage("Belum Ada Satupun yang Diceklis")
                } else {
                    isFirstTime = true
                    getCheckedProducts()
                }

            }

            btnBack.setOnClickListener {
                onBackPressed()
            }

        }

        dataProductAdapter.setOnItemClick(object : ListCartAdapter.OnAdapterListener {
            override fun onClick(data: Product) {
                val productID = data.id

                val intent = Intent(this@CartActivity, DetailActivity::class.java)
                intent.putExtra("id", productID)
                startActivity(intent)
            }

            override fun onDelete(data: Product) {
                val alertDialog = AlertDialog.Builder(this@CartActivity)
                alertDialog.apply {
                    setTitle("Delete Product?")
                    setMessage("Are you sure you want to delete ${data.name}?")
                    setNegativeButton("Cancel") { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }
                    setPositiveButton("Delete") { dialogInterface, i ->
                        viewModel.deleteProduct(data.id)
                        dataProductAdapter.removeData(data.id)
                    }
                }.show()
            }

            override fun onIncrease(data: Product, position: Int) {
                viewModel.incrementQuantity(data.id)
                viewModel.totalPriceItem(data.id)

                Log.e("Quantity btnPlus", data.quantity.toString())
            }


            override fun onDecrease(data: Product, position: Int) {
                if (data.quantity > 1){
                    viewModel.decrementQuantity(data.id)
                    viewModel.totalPriceItem(data.id)

                    Log.e("Quantity btnMinus", data.quantity.toString())
                }
            }

            override fun onChecked(data: Product, isChecked: Boolean) {
                viewModel.buttonCheck(data.id, isChecked)
                Log.e("Quantity Check Box", data.quantity.toString())

//                if (binding.cbCartActivity.isChecked != isChecked) {
//                    !binding.cbCartActivity.isChecked
//                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recyclerViewState = binding.rvCart.layoutManager?.onSaveInstanceState()
        outState.putParcelable("recycler_view_state", recyclerViewState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        recyclerViewState = savedInstanceState?.getParcelable("recycler_view_state")

        binding.rvCart.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }


    override fun onStart() {
        super.onStart()
        getProduct()
        viewModel.getTotalItemByCheckButton(1)!!.observe(this@CartActivity) {
            if (it == null) {
                binding.tvTotal.text = formatRupiah(0)
            } else binding.tvTotal.text = formatRupiah(it)
        }
    }

    private fun getProduct() {

        viewModel.getProduct()?.observe(this) {
            if (it != null) {
                isDataEmpty(true)
                val list = mapList(it)
                if (list.isNotEmpty()) {
                    isDataEmpty(false)
                    dataProductAdapter.setData(list)
                }
            }
        }
    }

    private fun mapList(product: List<Product>): ArrayList<Product> {
        val listProduct = ArrayList<Product>()
        for (data in product) {
            val dataMapped = Product(
                data.id,
                data.name,
                data.price,
                data.image,
                data.quantity,
                data.total_price_item,
                data.check_button
            )
            listProduct.add(dataMapped)
        }
        return listProduct
    }

    private fun getCheckedProducts() {
        val userID = sharedPref.getString(Constant.PREF_ID)
        viewModel.getCheckedProducts()!!.observe(this@CartActivity) { result ->
            if (isFirstTime) {
                val dataStockItems = arrayListOf<DataStockItem>()
                val listOfProductId = arrayListOf<String>()

                for (j in result.indices) {
                    dataStockItems.add(
                        DataStockItem(
                            result[j].id.toString(),
                            result[j].quantity
                        )
                    )
                    listOfProductId.add(result[j].id.toString())
                }

                updateStock(DataStock(userID!!, dataStockItems), listOfProductId)

                isFirstTime = false
            }
        }
        loading.isDismiss()
    }




    private fun buySuccess() {
        viewModel.buySuccess()
    }

    private fun updateStock(dataStock: DataStock, listProductID: ArrayList<String>) {
        viewModelUpdateStock = ViewModelProvider(this, ViewModelFactory(this))[UpdateStockViewModel::class.java]

        viewModelUpdateStock.setUpdateStock(dataStock)

        viewModelUpdateStock.updateStockSuccess.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                loading.isDismiss()
                val intent = Intent(this, RatingActivity::class.java)
                intent.putExtra("list_id", listProductID)
                startActivity(intent)
                showMessage(response.success.message)
                finish()
                buySuccess()
            }

        }
        viewModelUpdateStock.toast.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                loading.isDismiss()
                showMessage(response)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatRupiah(angka: Int): String {
        val formatRupiah = DecimalFormat("Rp #,###")
        return formatRupiah.format(angka)
    }

    private fun isDataEmpty(isEmpty: Boolean) {
        binding.apply {
            if (isEmpty) {
                emptyData.visibility = View.VISIBLE
                rvCart.visibility = View.GONE
                layoutCheck.visibility = View.GONE
                bottomAppbar.visibility = View.GONE
            } else {
                emptyData.visibility = View.GONE
                rvCart.visibility = View.VISIBLE
                layoutCheck.visibility = View.VISIBLE
                bottomAppbar.visibility = View.VISIBLE
            }
        }
    }


}