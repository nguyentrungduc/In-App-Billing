package com.sun.ntduc.iab

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(Sku::class), (CachedPurchase::class)],version = 1,exportSchema = false)
abstract class SkuDatabase : RoomDatabase() {
    companion object {
        private var INSTANCE: SkuDatabase? = null
        fun getDataBase(context: Context): SkuDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, SkuDatabase::class.java, "billing-db")
                    .allowMainThreadQueries().build()
            }
            return INSTANCE as SkuDatabase
        }
    }

    abstract fun skuDao(): SkuDao

    abstract fun purchaseDao(): PurchaseDao
}