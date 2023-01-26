package com.example.ecommerce_rifqi.model

data class DataProduct(
    val id: Int,
    val date: String,
    val desc: String,
    val harga: String,
    val image: String,
    val name_product: String,
    val rate: Int,
    val size: String,
    val stock: Int,
    val type: String,
    val weight: String
)