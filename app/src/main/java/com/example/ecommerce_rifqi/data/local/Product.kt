package com.example.ecommerce_rifqi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "item_product")
data class Product (
    @PrimaryKey
    var id: Int,
    var name: String = "",
    var price: String = "",
    var image: String = "",
    var quantity: Int){
    constructor() : this(0, "", "", "", 0)
}