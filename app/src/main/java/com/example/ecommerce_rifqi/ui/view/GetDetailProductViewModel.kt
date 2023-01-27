package com.example.ecommerce_rifqi.ui.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce_rifqi.data.local.Product
import com.example.ecommerce_rifqi.data.local.ProductDAO
import com.example.ecommerce_rifqi.data.local.ProductDatabase
import com.example.ecommerce_rifqi.model.DetailDataProduct
import com.example.ecommerce_rifqi.model.ResponseDetailProduct
import com.example.ecommerce_rifqi.networking.APIClient
import com.example.ecommerce_rifqi.networking.APIInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetDetailProductViewModel(context: Context): ViewModel() {

    private val api = APIClient.getClient(context)

    val productDetail = MutableLiveData<DetailDataProduct>()

    private var productDAO: ProductDAO?
    private var productDB: ProductDatabase?

    init {
        productDB = ProductDatabase.getDatabase(context)
        productDAO = productDB?.productDAO()
    }

    fun setDetailProduct(id_product: Int, id_user: Int){
        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.getDetailProduct(id_product, id_user).enqueue(object : Callback<ResponseDetailProduct>{
            override fun onResponse(
                call: Call<ResponseDetailProduct>,
                response: Response<ResponseDetailProduct>
            ) {
                if (response.isSuccessful){
                    productDetail.postValue(response.body()!!.success.data)
                }
            }

            override fun onFailure(call: Call<ResponseDetailProduct>, t: Throwable) {
                Log.e("Failure", t.message!!)
            }
        })

    }

    fun getDetailProduct(): LiveData<DetailDataProduct>{
        return productDetail
    }

    fun addToCart(id: Int, name: String, price: String, image: String, quantity: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val product = Product(id, name, price, image, quantity)
            productDAO?.addToCart(product)
        }
    }

    fun checkProduct(id: Int) = productDAO?.checkProduct(id)

}