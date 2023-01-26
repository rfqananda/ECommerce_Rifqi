package com.example.ecommerce_rifqi.ui.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce_rifqi.helper.Event
import com.example.ecommerce_rifqi.model.ChangePassError
import com.example.ecommerce_rifqi.model.ChangePassSuccess
import com.example.ecommerce_rifqi.model.LoginError
import com.example.ecommerce_rifqi.model.LoginSuccess
import com.example.ecommerce_rifqi.networking.APIClient
import com.example.ecommerce_rifqi.networking.APIInterface
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordViewModel(context: Context): ViewModel() {

    private val api = APIClient.getClient(context)


    private var _changeSuccess = MutableLiveData<Event<ChangePassSuccess>>()
    val changeSuccess: LiveData<Event<ChangePassSuccess>> = _changeSuccess

    private var _toast = MutableLiveData<Event<String>>()
    val toast: LiveData<Event<String>> = _toast


    fun setChangePass(id: Int, pass: String, newPass: String, confirmPass: String){
        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.changePassword(id, pass, newPass, confirmPass).enqueue(
            object : Callback<ChangePassSuccess>{
                override fun onResponse(
                    call: Call<ChangePassSuccess>,
                    response: Response<ChangePassSuccess>
                ) {
                    if (response.isSuccessful){
                        _changeSuccess.value = Event(response.body()!!)
                    } else{
                        val jObjError = JSONObject(response.errorBody()!!.string()).toString()
                        val gson = Gson()
                        val jsonObject = gson.fromJson(jObjError, JsonObject::class.java)
                        val error = gson.fromJson(jsonObject, ChangePassError::class.java)
                        _toast.value = Event(error.error?.message.toString())
                    }

                }

                override fun onFailure(call: Call<ChangePassSuccess>, t: Throwable) {
                    Log.e("Failure", t.message!!)
                }
            }
        )
    }
}