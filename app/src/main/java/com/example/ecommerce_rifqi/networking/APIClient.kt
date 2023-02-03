package com.example.ecommerce_rifqi.networking

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.model.ResponseRefreshToken
import com.example.ecommerce_rifqi.ui.LoginActivity
import com.example.ecommerce_rifqi.utils.AuthAuthenticator
import com.example.ecommerce_rifqi.utils.AuthBadResponse
import com.example.ecommerce_rifqi.utils.HeaderInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class APIClient {

    companion object {
        private fun getRequestHeader(context: Context): OkHttpClient? {
            val sharedPreferences = PreferencesHelper(context)
            val headerInterceptor = HeaderInterceptor(sharedPreferences)
            val authBadResponse = AuthBadResponse(sharedPreferences, context)
            val authAuthenticator = AuthAuthenticator(sharedPreferences)

            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(headerInterceptor)
                .addInterceptor(authBadResponse)
                .authenticator(authAuthenticator)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()

        }

        private var retrofit: Retrofit? = null
        fun getClient(context: Context): Retrofit? {
            retrofit = null
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl("https://portlan.id/training_android/public/")
                        .client(getRequestHeader(context))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit
        }
    }
}

