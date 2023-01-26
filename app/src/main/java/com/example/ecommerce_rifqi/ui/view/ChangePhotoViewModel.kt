package com.example.ecommerce_rifqi.ui.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce_rifqi.helper.Event
import com.example.ecommerce_rifqi.model.ChangeImageError
import com.example.ecommerce_rifqi.model.ChangeImageSuccess
import com.example.ecommerce_rifqi.model.RegisterError
import com.example.ecommerce_rifqi.model.RegisterSuccess
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

class ChangePhotoViewModel(context: Context): ViewModel() {

    private val api = APIClient.getClient(context)

    private var _changeImageSuccess = MutableLiveData<Event<ChangeImageSuccess>>()
    val changeImageSuccess: LiveData<Event<ChangeImageSuccess>> = _changeImageSuccess

    private var _toast = MutableLiveData<Event<String>>()
    val toast: LiveData<Event<String>> = _toast

    fun setChangeImage(id: RequestBody, image: MultipartBody.Part){
        val apiInterface = api?.create(APIInterface::class.java)

        apiInterface!!.changeImage(id, image).enqueue(object : Callback<ChangeImageSuccess>{
            override fun onResponse(
                call: Call<ChangeImageSuccess>,
                response: Response<ChangeImageSuccess>
            ) {
                if (response.isSuccessful){
                    _changeImageSuccess.value = Event(response.body()!!)
                } else{
                    val jObjError = JSONObject(response.errorBody()!!.string()).toString()
                    val gson = Gson()
                    val jsonObject = gson.fromJson(jObjError, JsonObject::class.java)
                    val error = gson.fromJson(jsonObject, ChangeImageError::class.java)
                    _toast.value = Event(error.error?.message.toString())
                }
            }

            override fun onFailure(call: Call<ChangeImageSuccess>, t: Throwable) {
                Log.e("Failure", t.message!!)
            }
        })
    }
}