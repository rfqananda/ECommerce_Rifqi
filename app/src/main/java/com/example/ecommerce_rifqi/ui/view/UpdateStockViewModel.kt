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

class UpdateStockViewModel(context: Context): ViewModel() {

    private val api = APIClient.getClient(context)

    private var _updateStockSuccess = MutableLiveData<Event<UpdateStockSuccess>>()
    val updateStockSuccess: LiveData<Event<UpdateStockSuccess>> = _updateStockSuccess

    private var _toast = MutableLiveData<Event<String>>()
    val toast: LiveData<Event<String>> = _toast

    fun setUpdateStock(productID: String?, stock: Int){
        val requestBody = DataStock(listOf(DataStockItem(productID.toString(), stock)))

        Log.e("Buy", "$productID, $stock")

        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.updateStock(requestBody).enqueue(object : Callback<UpdateStockSuccess>{
            override fun onResponse(
                call: Call<UpdateStockSuccess>,
                response: Response<UpdateStockSuccess>
            ) {
                if (response.isSuccessful){
                    _updateStockSuccess.value = Event(response.body()!!)
                } else{
                    val jObjError = JSONObject(response.errorBody()!!.string()).toString()
                    val gson = Gson()
                    val jsonObject = gson.fromJson(jObjError, JsonObject::class.java)
                    val error = gson.fromJson(jsonObject, UpdateStockError::class.java)
                    _toast.value = Event(error.error?.message.toString())
                }
            }

            override fun onFailure(call: Call<UpdateStockSuccess>, t: Throwable) {
                Log.e("Failure", t.message!!)
            }
        })
    }

    fun setUpdateStockCart(productData: HashMap<String, Any>){
        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.updateStockCart(productData).enqueue(object: Callback<UpdateStockSuccess>{
            override fun onResponse(
                call: Call<UpdateStockSuccess>,
                response: Response<UpdateStockSuccess>
            ) {
                if (response.isSuccessful){
                    _updateStockSuccess.value = Event(response.body()!!)
                } else{
                    val jObjError = JSONObject(response.errorBody()!!.string()).toString()
                    val gson = Gson()
                    val jsonObject = gson.fromJson(jObjError, JsonObject::class.java)
                    val error = gson.fromJson(jsonObject, UpdateStockError::class.java)
                    _toast.value = Event(error.error?.message.toString())
                }
            }

            override fun onFailure(call: Call<UpdateStockSuccess>, t: Throwable) {
                Log.e("Failure", t.message!!)

            }
        })
    }
}