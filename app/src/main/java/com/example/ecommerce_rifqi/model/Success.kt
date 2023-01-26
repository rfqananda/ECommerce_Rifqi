package com.example.ecommerce_rifqi.model

data class Success(
    val data: List<DataProduct>,
    val message: String,
    val status: Int
)