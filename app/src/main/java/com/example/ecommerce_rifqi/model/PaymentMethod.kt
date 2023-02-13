package com.example.ecommerce_rifqi.model

data class PaymentMethod (
    val id: String? = null,
    val type: String? = null,
    val order: Int? = null,
    val data: List<DataPayment?>? = null
)