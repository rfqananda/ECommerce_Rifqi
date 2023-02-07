package com.example.ecommerce_rifqi.utils

import android.util.Log
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.ResponseRefreshToken
import com.example.ecommerce_rifqi.networking.APIInterface
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthAuthenticator constructor(
    private val sharedPref: PreferencesHelper
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {

            val accessToken = sharedPref.getString(Constant.PREF_ACCESS)
            val refreshToken = sharedPref.getString(Constant.PREF_REFRESH)
            val userID = sharedPref.getString(Constant.PREF_ID)

            val newToken = getNewToken(accessToken!!, refreshToken!!, userID!!.toInt())

            if (!newToken.isSuccessful || newToken.body() == null || newToken.code() == 401) {
                //response.close()
                sharedPref.clear()
            }

            newToken.body()?.let {
                val newUserToken = it.success.access_token
                val newRefreshToken = it.success.refresh_token

                sharedPref.putNewToken(newUserToken, newRefreshToken)
                Log.d("newToken", it.success.access_token)
                Log.d("newRefreshToken", it.success.refresh_token)
                response.request.newBuilder()
                    .header("Authorization", it.success.access_token)
                    .build()
            }

        }
    }

    private suspend fun getNewToken(
        accessToken: String,
        refreshToken: String,
        userID: Int
    ): retrofit2.Response<ResponseRefreshToken>{

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://portlan.id/training_android/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(APIInterface::class.java)
        return service.refreshToken(userID, accessToken,refreshToken)
    }
}