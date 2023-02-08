package com.example.ecommerce_rifqi.ui.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce_rifqi.model.LoginSuccess
import com.example.ecommerce_rifqi.model.LoginError
import com.example.ecommerce_rifqi.networking.APIClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.ecommerce_rifqi.helper.Event
import com.example.ecommerce_rifqi.networking.APIInterface

class LoginViewModel(context: Context) : ViewModel(){

    private val api = APIClient.getClient(context)


    private var _loginSucces = MutableLiveData<Event<LoginSuccess>>()
    val loginSuccess: LiveData<Event<LoginSuccess>> = _loginSucces

    private var _loginError = MutableLiveData<Event<LoginError>>()
    val loginError: LiveData<Event<LoginError>> = _loginError



    fun setLogin(email: String, password:String, token: String){
        val apiInterface = api?.create(APIInterface::class.java)
        apiInterface!!.login(email, password, token).enqueue(object : Callback<LoginSuccess>{
            override fun onResponse(call: Call<LoginSuccess>, response: Response<LoginSuccess>) {

                if (response.isSuccessful){
                    _loginSucces.value = Event(response.body()!!)
                }else{
                    val jObjError = JSONObject(response.errorBody()!!.string()).toString()
                    val gson = Gson()
                    val jsonObject = gson.fromJson(jObjError, JsonObject::class.java)
                    val error = gson.fromJson(jsonObject, LoginError::class.java)
                    _loginError.value = Event(error)
                    Log.d("LoginViewModel", error.toString())
                }
            }

            override fun onFailure(call: Call<LoginSuccess>, t: Throwable) {
                Log.e("Failure", t.message!!)
            }
        })
    }

//    fun getLoginMessage(): LiveData<Success>{
//        return loginResponse
//    }

//    fun getError(): LiveData<LoginError>{
//        return loginError
//    }
}