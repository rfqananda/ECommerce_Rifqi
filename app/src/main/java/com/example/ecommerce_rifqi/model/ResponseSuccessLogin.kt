package com.example.ecommerce_rifqi.model

data class ResponseSuccessLogin(
    val access_token: String,
    val data_user: DataUser,
    val message: String,
    val refresh_token: String,
    val status: Int
)