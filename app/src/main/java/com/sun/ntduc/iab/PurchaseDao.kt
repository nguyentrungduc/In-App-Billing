package com.sun.ntduc.iab

import androidx.room.*
import com.android.billingclient.api.Purchase
import io.reactivex.Flowable

@Dao
interface PurchaseDao {

    @Query("SELECT * FROM purchase_table")
    fun getPurchases(): Flowable<List<CachedPurchase>>

    @Insert
    fun insert(purchase: CachedPurchase)

    @Delete
    fun delete(vararg purchases: CachedPurchase)

    @Query("DELETE FROM purchase_table")
    fun deleteAll()

//    @Query("DELETE FROM purchase_table WHERE data = :purchase")
//    fun delete(purchase: Purchase)
}