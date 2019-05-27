package com.sun.ntduc.iab

import androidx.room.*
import com.android.billingclient.api.Purchase
import com.google.gson.Gson

@Entity(tableName = "purchase_table")
@TypeConverters(PurchaseTypeCoverter::class)
class CachedPurchase(val data: Purchase) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @Ignore
    val purchaseToken = data.purchaseToken
    @Ignore
    val sku = data.sku

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is CachedPurchase -> data == other.data
            is Purchase -> data == other
            else -> false
        }
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

}
