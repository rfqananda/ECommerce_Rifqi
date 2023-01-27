package com.example.ecommerce_rifqi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Product::class],
    version = 1
)

abstract class ProductDatabase : RoomDatabase() {

    companion object {
        var INSTANCE: ProductDatabase? = null

        fun getDatabase(context: Context): ProductDatabase? {
            if (INSTANCE == null) {
                synchronized(ProductDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ProductDatabase::class.java,
                        "pokemon_database"
                    ).build()
                }
            }
            return INSTANCE
        }
    }

    abstract fun productDAO(): ProductDAO
}