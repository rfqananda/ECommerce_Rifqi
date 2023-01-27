package com.example.ecommerce_rifqi.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ProductDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToCart(dataProduct: Product)

    @Query("SELECT * FROM item_product")
    fun getDataProduct(): LiveData<List<Product>>

    @Query("SELECT  count(*) FROM item_product where item_product.id = :id")
    fun checkProduct(id: Int): String

    @Query("SELECT * FROM item_product WHERE id = :id")
    fun getProduct(id: Int): Product?

    @Query("DELETE FROM item_product WHERE item_product.id = :id")
    fun deleteProduct(id: Int) : Int

    @Query("UPDATE item_product SET quantity = quantity + 1 WHERE id = :id")
    fun incrementQuantity(id: Int)



    @Transaction
    fun refreshData(id: Int){
        incrementQuantity(id)
        getDataProduct()
    }
}