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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(context: Context) : ViewModel() {

    private val api = APIClient.getClient(context)


    private var _registerSucces = MutableLiveData<Event<RegisterSuccess>>()
    val registerSuccess: LiveData<Event<RegisterSuccess>> = _registerSucces


    private var _toast = MutableLiveData<Event<String>>()
    val toast: LiveData<Event<String>> = _toast

    fun setRegister(
        name: RequestBody,
        email: RequestBody,
        password: RequestBody,
        phone: RequestBody,
        gender: RequestBody,
        image: MultipartBody.Part
    ) {
        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.register(name, email, password, phone, gender, image)
            .enqueue(object : Callback<RegisterSuccess> {
                override fun onResponse(
                    call: Call<RegisterSuccess>,
                    response: Response<RegisterSuccess>
                ) {
                    if (response.isSuccessful) {
                        _registerSucces.value = Event(response.body()!!)
                    } else{
                        val jObjError = JSONObject(response.errorBody()!!.string()).toString()
                        val gson = Gson()
                        val jsonObject = gson.fromJson(jObjError, JsonObject::class.java)
                        val error = gson.fromJson(jsonObject, RegisterError::class.java)
                        _toast.value = Event(error.error?.message.toString())
                    }
                }

                override fun onFailure(call: Call<RegisterSuccess>, t: Throwable) {
                    Log.e("Failure", t.message!!)
                }
            })
    }

//    fun getRegisterStatusMessage(): LiveData<RegisterSuccess> {
//        return registerStatus
//    }

}