package com.example.ecommerce_rifqi.ui.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce_rifqi.model.DataProduct
import com.example.ecommerce_rifqi.model.DetailDataProduct
import com.example.ecommerce_rifqi.model.ResponseDetailProduct
import com.example.ecommerce_rifqi.networking.APIClient
import com.example.ecommerce_rifqi.networking.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetDetailProductViewModel(context: Context): ViewModel() {

    private val api = APIClient.getClient(context)

    val productDetail = MutableLiveData<DetailDataProduct>()

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

}