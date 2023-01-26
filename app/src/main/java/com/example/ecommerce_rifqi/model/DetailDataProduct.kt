package com.example.ecommerce_rifqi.model

data class DetailDataProduct(
    val date: String,
    val desc: String,
    val harga: String,
    val id: Int,
    val image: String,
    val image_product: List<ImageProduct>,
    val name_product: String,
    val rate: Int,
    val size: String,
    val stock: Int,
    val isFavorite: Boolean,
    val type: String,
    val weight: String
)