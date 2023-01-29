package com.example.ecommerce_rifqi.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.RequiresApi
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

    private var totalPrice: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataProductAdapter = CartAdapter(this)

        viewModel = ViewModelProvider(this)[GetProductCartViewModel::class.java]
        viewModelBuy = ViewModelProvider(this)[BuyProductViewModel::class.java]

        getProduct()


        binding.apply {
            rvCart.setHasFixedSize(true)
            rvCart.layoutManager = LinearLayoutManager(applicationContext)
            rvCart.adapter = dataProductAdapter

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

//                val dataStock = ArrayList<HashMap<String, Any>>()
//                for (product in productList) {
//                    if (product.check_button) {
//                        val stock = HashMap<String, Any>()
//                        stock["id_product"] = product.id
//                        stock["stock"] = product.quantity
//                        dataStock.add(stock)
//                    }
//                }
//                val jsonObject = JSONObject()
//                jsonObject.put("data_stock", dataStock)

                viewModel.getCheckedProducts()!!.observe(this@CartActivity){
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
                            val data = HashMap<String, Any>()
                            data["data_stock"] = dataStockList

                            updateStock(data)
                        }
                    }
                }

//                viewModel.getCheckedProducts().observe(this@CartActivity){
//                    val dataStock = it.map { checkedProduct ->
//                        mapOf(
//                            "id_product" to checkedProduct.id,
//                            "stock" to checkedProduct.quantity
//                        )
//                    }
//
//                    val result = mapOf(
//                        "data_stock" to dataStock
//                    )
//
//                    updateStock(result)
//
//                }

//                val dataStockList = ArrayList<HashMap<String, Any>>()
//                for (product in selectedProducts) {
//                    if (product.check_button) {
//                        val dataStock = HashMap<String, Any>()
//                        dataStock["id_product"] = product.id
//                        dataStock["stock"] = product.quantity
//                        dataStockList.add(dataStock)
//                    }
//                }
//                val data = HashMap<String, Any>()
//                data["data_stock"] = dataStockList

            }

            rvCart.post(Runnable {

            })

        }

        dataProductAdapter.setOnItemClick(object : CartAdapter.OnAdapterListener{
            override fun onDelete(data: Product) {
                viewModel.deleteProduct(data.id)
                dataProductAdapter.removeData(data.id)
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
//                viewModel.getTotalItemByCheckButton(1)!!.observe(this@CartActivity){
//                    if (it == null){
//                        binding.tvTotal.text = formatRupiah(0)
//                    } else binding.tvTotal.text = formatRupiah(it)
//                }
                Log.e("Quantity Check Box", data.quantity.toString())

            }
        })
    }


    override fun onStart() {
        super.onStart()
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

    private fun updateStock(productData: HashMap<String, Any>) {
        viewModelUpdateStock = ViewModelProvider(this, ViewModelFactory(this))[UpdateStockViewModel::class.java]

        viewModelUpdateStock.setUpdateStockCart(productData)
        viewModelUpdateStock.updateStockSuccess.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response.success.message)
            }

        }
        viewModelUpdateStock.toast.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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