package com.example.ecommerce_rifqi.ui.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce_rifqi.model.DataProduct
import com.example.ecommerce_rifqi.model.ResponseOtherProductSuccess
import com.example.ecommerce_rifqi.model.Success
import com.example.ecommerce_rifqi.networking.APIClient
import com.example.ecommerce_rifqi.networking.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetOtherProductsViewModel(context: Context) : ViewModel() {

    private val api = APIClient.getClient(context)

    val otherProductList = MutableLiveData<List<DataProduct>>()

    fun setOtherProductList(id_user: Int){
        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.getOtherProducts(id_user).enqueue(object : Callback<ResponseOtherProductSuccess>{

            override fun onResponse(
                call: Call<ResponseOtherProductSuccess>,
                response: Response<ResponseOtherProductSuccess>
            ) {
                if (response.isSuccessful){
                    otherProductList.postValue(response.body()!!.success.data)
                }
            }

            override fun onFailure(call: Call<ResponseOtherProductSuccess>, t: Throwable) {
                Log.e("Failure", t.message!!)

            }
        })
    }

    fun getOtherProductList(): LiveData<List<DataProduct>> {
        return otherProductList
    }
}