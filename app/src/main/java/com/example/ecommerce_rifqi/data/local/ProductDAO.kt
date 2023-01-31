package com.example.ecommerce_rifqi.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToCart(dataProduct: Product)

    @Query("SELECT * FROM item_product")
    fun getDataProduct(): LiveData<List<Product>>

    @Query("SELECT  count(*) FROM item_product where item_product.id = :id")
    fun checkProduct(id: Int): Int

    @Query("DELETE FROM item_product WHERE item_product.id = :id")
    fun deleteProduct(id: Int)

    @Query("UPDATE item_product SET quantity = quantity + 1 WHERE id = :id")
    fun incrementQuantity(id: Int)

    @Query("UPDATE item_product SET quantity = quantity - 1 WHERE id = :id")
    fun decrementQuantity(id: Int)

    @Query("UPDATE item_product SET total_price_item = price * quantity WHERE id = :id")
    fun totalPriceItem(id: Int)

    @Query("UPDATE item_product SET check_button = :buttonCheck WHERE id = :id")
    fun buttonCheck(id: Int, buttonCheck: Boolean)

    @Query("SELECT SUM(total_price_item) FROM item_product WHERE check_button = :checkValue")
    fun getTotalItemByCheckButton(checkValue: Int): LiveData<Int>

    @Query("UPDATE item_product SET check_button = :one")
    fun selectAll(one: Int)

    @Query("UPDATE item_product SET check_button = :zero")
    fun unselectAll(zero: Int)

    @Query("SELECT COUNT(*) FROM item_product")
    fun countData(): Int

    @Query("SELECT id, quantity FROM item_product WHERE check_button = 1")
    fun getCheckedProducts(): LiveData<List<CheckedProduct>>

    @Query("DELETE FROM item_product WHERE check_button = 1")
    fun deleteCheckedProducts()

//    DELETE FROM item_product

    //SELECT SUM(total_item) FROM data_prodcut WHERE check_button = 1



}