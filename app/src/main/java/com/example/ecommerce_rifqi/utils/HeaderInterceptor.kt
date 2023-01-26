package com.example.ecommerce_rifqi.utils

import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor constructor(private val sharedPref: PreferencesHelper): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            sharedPref.getString(Constant.PREF_ACCESS)
        }

        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("apikey", "TuIBt77u7tZHi8n7WqUC")
            .header("Authorization", "$accessToken")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

}