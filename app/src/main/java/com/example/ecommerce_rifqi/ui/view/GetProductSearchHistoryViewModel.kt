package com.example.ecommerce_rifqi.ui.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce_rifqi.model.DataProduct
import com.example.ecommerce_rifqi.model.ResponseOtherProductSuccess
import com.example.ecommerce_rifqi.networking.APIClient
import com.example.ecommerce_rifqi.networking.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetProductSearchHistoryViewModel(context: Context) : ViewModel() {

    private val api = APIClient.getClient(context)

    val searchHistoryProductList = MutableLiveData<List<DataProduct>>()

    fun setHistoryProductList(id_user: Int){
        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.getProductSearchHistory(id_user).enqueue(object : Callback<ResponseOtherProductSuccess>{
            override fun onResponse(
                call: Call<ResponseOtherProductSuccess>,
                response: Response<ResponseOtherProductSuccess>
            ) {
                if (response.isSuccessful){
                    searchHistoryProductList.postValue(response.body()!!.success.data)
                }
            }

            override fun onFailure(call: Call<ResponseOtherProductSuccess>, t: Throwable) {
                Log.e("Failure", t.message!!)
            }
        })
    }

    fun getHistoryProductList(): LiveData<List<DataProduct>> {
        return searchHistoryProductList
    }

}