package com.sun.ntduc.iab

import androidx.room.TypeConverter
import com.android.billingclient.api.Purchase

class PurchaseTypeCoverter {
    @TypeConverter
    fun toString(purchase: Purchase): String = purchase.originalJson + '|' + purchase.signature

    @TypeConverter
    fun toPurchase(data: String): Purchase = data.split('|').let {
        Purchase(it[0], it[1])
    }
}