package com.example.ecommerce_rifqi.ui.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce_rifqi.helper.Event
import com.example.ecommerce_rifqi.model.*
import com.example.ecommerce_rifqi.networking.APIClient
import com.example.ecommerce_rifqi.networking.APIInterface
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetListProductViewModel(context: Context): ViewModel(){

    private val api = APIClient.getClient(context)

    val productList = MutableLiveData<List<DataProduct>>()

    fun setProductList(search: String){
        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.getListProduct(search).enqueue(object: Callback<ListDataProduct>{
            override fun onResponse(
                call: Call<ListDataProduct>,
                response: Response<ListDataProduct>
            ) {
                if (response.isSuccessful){
                    productList.postValue(response.body()!!.success.data)
                }
            }

            override fun onFailure(call: Call<ListDataProduct>, t: Throwable) {
                Log.e("Failure", t.message!!)
            }
        })

    }

    fun getProductList(): LiveData<List<DataProduct>>{
        return productList
    }

}