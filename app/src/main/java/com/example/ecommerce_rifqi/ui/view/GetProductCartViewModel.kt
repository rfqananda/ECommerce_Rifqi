package com.example.ecommerce_rifqi.ui.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ecommerce_rifqi.adapter.CartAdapter
import com.example.ecommerce_rifqi.data.local.CheckedProduct
import com.example.ecommerce_rifqi.data.local.Product
import com.example.ecommerce_rifqi.data.local.ProductDAO
import com.example.ecommerce_rifqi.data.local.ProductDatabase
import com.example.ecommerce_rifqi.helper.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetProductCartViewModel(application: Application): AndroidViewModel(application) {
    private var productDAO: ProductDAO?
    private var productDB: ProductDatabase?
    var checkedProducts: MutableLiveData<List<CheckedProduct>> = MutableLiveData()

    init {
        productDB = ProductDatabase.getDatabase(application)
        productDAO = productDB?.productDAO()
//        productDAO?.getCheckedProducts()?.observeForever {
//            checkedProducts.value = it.map { product ->
//                CheckedProduct(
//                    id = product.id,
//                    quantity = product.quantity
//                )
//            }
//        }
    }

    private var _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private var _totalData = MutableLiveData<Int>()
    val totalData: LiveData<Int> = _totalData

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

    fun decrementQuantity(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            productDAO?.decrementQuantity(id)
        }
    }

    fun totalPriceItem(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            productDAO?.totalPriceItem(id)
        }
    }

    fun buttonCheck(id: Int, btnCheck: Boolean){
        CoroutineScope(Dispatchers.IO).launch {
            productDAO?.buttonCheck(id, btnCheck)
        }
    }

    fun getTotalItemByCheckButton(checkValue: Int): LiveData<Int>? {
        return productDAO?.getTotalItemByCheckButton(checkValue)
    }

    fun selectAll(checkValue: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            productDAO?.selectAll(checkValue)
        }
    }

    fun unselectAll(checkValue: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            productDAO?.unselectAll(checkValue)
        }
    }

    fun getProductCount() {
        CoroutineScope(Dispatchers.IO).launch {
            _totalData.postValue(productDAO?.countData())
        }
    }

    fun getCheckedProducts(): LiveData<List<CheckedProduct>>? {
        return productDAO?.getCheckedProducts()
    }

    fun deleteCheckedProducts(){
        CoroutineScope(Dispatchers.IO).launch {
            productDAO?.deleteCheckedProducts()
        }
    }

}