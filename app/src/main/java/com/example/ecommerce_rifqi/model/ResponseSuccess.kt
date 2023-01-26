package com.example.ecommerce_rifqi.model

data class ResponseSuccess(
    val message: String,
    val status: Int,
    val path: String,
    val access_token: String,
    val refresh_token: String
)