package com.sun.ntduc.iab

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import io.reactivex.Flowable
import org.intellij.lang.annotations.Flow

@Dao
interface SkuDao {

    @Query("SELECT * FROM Sku WHERE type = '${BillingClient.SkuType.SUBS}'")
    fun getSubscriptionSkuDetails(): Flowable<List<Sku>>

    @Query("SELECT * FROM Sku WHERE type = '${BillingClient.SkuType.INAPP}'")
    fun getInappSkuDetails(): Flowable<List<Sku>>

    @Transaction
    fun insertOrUpdate(skuDetails: SkuDetails) = skuDetails.apply {
        val result = getById(sku)
        val bool = if (result == null) true else result.canPurchase
        val originalJson = toString().substring("SkuDetails: ".length)
        val detail = Sku(bool, sku, type, price, title, description, originalJson)
        insert(detail)
    }

    @Transaction
    fun insertOrUpdate(sku: String, canPurchase: Boolean) {
        val result = getById(sku)
        if (result != null) {
            update(sku, canPurchase)
        } else {
            insert(Sku(canPurchase, sku, null, null, null, null, null))
        }
    }

    @Query("SELECT * FROM Sku WHERE sku = :sku")
    fun getById(sku: String): Sku?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sku: Sku)

    @Query("UPDATE Sku SET canPurchase = :canPurchase WHERE sku = :sku")
    fun update(sku: String, canPurchase: Boolean)
}