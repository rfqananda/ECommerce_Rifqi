package com.example.ecommerce_rifqi.ui.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerce_rifqi.data.local.Product
import com.example.ecommerce_rifqi.data.local.ProductDAO
import com.example.ecommerce_rifqi.data.local.ProductDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetProductCartViewModel(application: Application): AndroidViewModel(application) {
    private var productDAO: ProductDAO?
    private var productDB: ProductDatabase?

    init {
        productDB = ProductDatabase.getDatabase(application)
        productDAO = productDB?.productDAO()
    }

    private var _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    fun getProduct(): LiveData<List<Product>>?{
        return productDAO?.getDataProduct()
    }

    fun deleteProduct(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            productDAO?.deleteProduct(id)
        }
    }

    fun incrementQuantity(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            productDAO?.incrementQuantity(id)
        }
    }

    fun setIncrementQuantity(){

    }

    fun refreshData(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            productDAO?.refreshData(id)
        }
    }
}