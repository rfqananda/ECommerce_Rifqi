package com.example.ecommerce_rifqi.ui.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce_rifqi.model.*
import com.example.ecommerce_rifqi.networking.APIClient
import com.example.ecommerce_rifqi.networking.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetListFavoriteProductViewModel(context: Context): ViewModel(){

    private val api = APIClient.getClient(context)

    val favoriteProductList = MutableLiveData<List<DataProduct>>()

    fun setFavoriteProductList(search: String, id_user: Int){
        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.getFavorite(search, id_user).enqueue(object: Callback<ListDataProduct>{
            override fun onResponse(
                call: Call<ListDataProduct>,
                response: Response<ListDataProduct>
            ) {
                if (response.isSuccessful){
                    favoriteProductList.postValue(response.body()!!.success.data)
                }
            }

            override fun onFailure(call: Call<ListDataProduct>, t: Throwable) {
                Log.e("Failure", t.message!!)
            }
        })
    }

    fun getFavoriteProductList(): LiveData<List<DataProduct>>{
        return favoriteProductList
    }

}