package com.example.ecommerce_rifqi.di

import android.content.Context
import com.example.ecommerce_rifqi.networking.APIClient
import com.example.ecommerce_rifqi.networking.APIInterface
import com.example.ecommerce_rifqi.paging.ProductRepository

object Injection {
    fun provideRepository(context: Context): ProductRepository {
        val api = APIClient.getClient(context)
        val apiInterface = api!!.create(APIInterface::class.java)
        return ProductRepository(apiInterface)
    }
}