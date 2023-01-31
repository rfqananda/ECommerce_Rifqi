package com.example.ecommerce_rifqi.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.CartAdapter
import com.example.ecommerce_rifqi.data.local.CheckedProduct
import com.example.ecommerce_rifqi.data.local.Product
import com.example.ecommerce_rifqi.databinding.ActivityCartBinding
import com.example.ecommerce_rifqi.ui.view.BuyProductViewModel
import com.example.ecommerce_rifqi.ui.view.GetProductCartViewModel
import com.example.ecommerce_rifqi.ui.view.UpdateStockViewModel
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import java.text.DecimalFormat


class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    private lateinit var dataProductAdapter: CartAdapter

    private lateinit var viewModel: GetProductCartViewModel

    private lateinit var viewModelBuy: BuyProductViewModel

    private lateinit var viewModelUpdateStock: UpdateStockViewModel

    private var recyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataProductAdapter = CartAdapter(this)

        viewModel = ViewModelProvider(this)[GetProductCartViewModel::class.java]
        viewModelBuy = ViewModelProvider(this)[BuyProductViewModel::class.java]

        recyclerViewState = savedInstanceState?.getParcelable("recycler_view_state")

        binding.apply {
            rvCart.setHasFixedSize(true)
            rvCart.layoutManager = LinearLayoutManager(applicationContext)
            rvCart.adapter = dataProductAdapter
            rvCart.layoutManager?.onRestoreInstanceState(recyclerViewState)

            viewModelBuy.totalPrice.observe(this@CartActivity){
                tvTotal.text = it.toString()
            }

            cbCartActivity.setOnCheckedChangeListener { _, isChecked ->
                for(i in 0 until rvCart.childCount){
                    val child = rvCart.getChildAt(i)
                    val recyclerViewCB = child.findViewById<CheckBox>(R.id.cb_cart)

                    if (isChecked){
                        viewModel.selectAll(1)
                        recyclerViewCB.isChecked = isChecked

                    } else{
                        viewModel.unselectAll(0)
                        recyclerViewCB.isChecked = isChecked
                    }

                }
            }

            btnBuy.setOnClickListener {

                viewModel.getCheckedProducts()!!.observe(this@CartActivity){
                    val data = HashMap<String, Any>()

                    if (it != null){
                        val list = mapListChecked(it)
                        if (list.isNotEmpty()){
                            val dataStockList = ArrayList<HashMap<String, Any>>()
                            for (product in list) {
                                val dataStock = HashMap<String, Any>()
                                dataStock["id_product"] = product.id
                                dataStock["stock"] = product.quantity
                                dataStockList.add(dataStock)
                            }
                            data["data_stock"] = dataStockList
                        }
                    }

                    val dataStockItems = arrayListOf<CheckedProduct>()
                    val listOfProductId = arrayListOf<String>()
                    for (i in it.indices) {
                        dataStockItems.add(CheckedProduct(it[i].id, it[i].quantity))
                        listOfProductId.add(it[i].id.toString())
                    }

                    updateStock(data, listOfProductId)

                }
                viewModel.deleteCheckedProducts()
            }

            btnBack.setOnClickListener {
                onBackPressed()
            }

        }

        dataProductAdapter.setOnItemClick(object : CartAdapter.OnAdapterListener{
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

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onIncrease(data: Product, position: Int) {
                viewModel.incrementQuantity(data.id)
                viewModel.totalPriceItem(data.id)
                val updatedData = data.copy(quantity = data.quantity + 1)
                dataProductAdapter.updateData(updatedData)

                binding.rvCart.layoutManager!!.scrollToPosition(position)

                Log.e("Quantity btnPlus", data.quantity.toString())
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDecrease(data: Product, position: Int) {
                viewModel.decrementQuantity(data.id)
                viewModel.totalPriceItem(data.id)
                val updatedData = data.copy(quantity = data.quantity - 1)
                dataProductAdapter.updateData(updatedData)

                binding.rvCart.layoutManager!!.scrollToPosition(position)

                Log.e("Quantity btnMinus", data.quantity.toString())
            }

            override fun onChecked(data: Product, isChecked: Boolean) {
                viewModel.buttonCheck(data.id, isChecked)
                Log.e("Quantity Check Box", data.quantity.toString())

                if (binding.cbCartActivity.isChecked != isChecked){
                    !binding.cbCartActivity.isChecked
                }
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
        viewModel.getTotalItemByCheckButton(1)!!.observe(this@CartActivity){
            if (it == null){
                binding.tvTotal.text = formatRupiah(0)
            } else binding.tvTotal.text = formatRupiah(it)
        }
    }

    private fun getProduct(){

        viewModel.getProduct()?.observe(this){
            if (it != null){
                isDataEmpty(true)
                val list = mapList(it)
                if (list.isNotEmpty()){
                    isDataEmpty(false)
                    dataProductAdapter.setData(list)
                }
            }
        }
    }

    private fun mapList(product: List<Product>): ArrayList<Product>{
        val listProduct = ArrayList<Product>()
        for (data in product){
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

    private fun mapListChecked(product: List<CheckedProduct>): ArrayList<CheckedProduct>{
        val listProduct = ArrayList<CheckedProduct>()
        for (data in product){
            val dataMapped = CheckedProduct(
                data.id,
                data.quantity
            )
            listProduct.add(dataMapped)
        }
        return listProduct
    }

    private fun updateStock(productData: HashMap<String, Any>, listProductID: ArrayList<String>) {
        viewModelUpdateStock = ViewModelProvider(this, ViewModelFactory(this))[UpdateStockViewModel::class.java]

        viewModelUpdateStock.setUpdateStockCart(productData)
        viewModelUpdateStock.updateStockSuccess.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response.success.message)

                val intent = Intent(this, RatingActivity::class.java)
                intent.putExtra("list_id", listProductID)
                startActivity(intent)
            }

        }
        viewModelUpdateStock.toast.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatRupiah(angka: Int): String {
        val formatRupiah =  DecimalFormat("Rp #,###")
        return formatRupiah.format(angka)
    }

    private fun isDataEmpty(isEmpty: Boolean){
        binding.apply {
            if (isEmpty){
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