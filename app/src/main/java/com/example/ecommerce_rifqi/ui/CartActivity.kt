package com.example.ecommerce_rifqi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_rifqi.adapter.CartAdapter
import com.example.ecommerce_rifqi.data.local.Product
import com.example.ecommerce_rifqi.databinding.ActivityCartBinding
import com.example.ecommerce_rifqi.ui.view.BuyProductViewModel
import com.example.ecommerce_rifqi.ui.view.GetProductCartViewModel

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    private lateinit var dataProductAdapter: CartAdapter

    private lateinit var viewModel: GetProductCartViewModel

    private lateinit var viewModelBuy: BuyProductViewModel

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

        }

        dataProductAdapter.setOnItemClick(object : CartAdapter.OnAdapterListener{
            override fun onDelete(data: Product) {
                viewModel.deleteProduct(data.id)
                dataProductAdapter.notifyDataSetChanged()
            }

            override fun onIncrease(data: Product, tv: TextView) {
                binding.rvCart.adapter?.notifyDataSetChanged()
                showMessage("Coba")
                viewModel.incrementQuantity(data.id)

            }

//            override fun onIncrease(data: Product) {
//                viewModel.incrementQuantity(data.id)
//            }

            override fun onDecrease(data: Product) {
                viewModelBuy.decreaseQuantity()
            }
        })
    }

    private fun getProduct(){
        viewModel.getProduct()?.observe(this){
            if (it != null){
                val list = mapList(it)
                if (list.isNotEmpty()){
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
                data.quantity
            )
            listProduct.add(dataMapped)
        }
        return listProduct
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}