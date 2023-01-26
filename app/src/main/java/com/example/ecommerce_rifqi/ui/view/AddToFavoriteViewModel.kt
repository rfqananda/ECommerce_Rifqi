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

class AddToFavoriteViewModel(context: Context): ViewModel() {
    private val api = APIClient.getClient(context)

    private var _addFavSuccess = MutableLiveData<Event<ResponseSuccess>>()
    val addFavSuccess: LiveData<Event<ResponseSuccess>> = _addFavSuccess

    private var _toast = MutableLiveData<Event<String>>()
    val toast: LiveData<Event<String>> = _toast

    fun setAddToFav(productID: Int, userID: Int){
        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.addToFav(productID, userID).enqueue(object : Callback<AddToFavoriteSuccess>{
            override fun onResponse(
                call: Call<AddToFavoriteSuccess>,
                response: Response<AddToFavoriteSuccess>
            ) {
                if (response.isSuccessful){
                    _addFavSuccess.value =  Event(response.body()!!.success)
                } else{
                    val jObjError = JSONObject(response.errorBody()!!.string()).toString()
                    val gson = Gson()
                    val jsonObject = gson.fromJson(jObjError, JsonObject::class.java)
                    val error = gson.fromJson(jsonObject, FavoriteError::class.java)
                    _toast.value = Event(error.error.message)
                }
            }

            override fun onFailure(call: Call<AddToFavoriteSuccess>, t: Throwable) {
                Log.e("Failure", t.message!!)
            }
        })

    }
}