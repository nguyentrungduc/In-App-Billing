package com.sun.ntduc.iab

import androidx.room.TypeConverter
import com.android.billingclient.api.Purchase
import com.google.gson.Gson

class PurchaseTypeCoverter {
    @TypeConverter
    fun toString(purchase: Purchase): String {
        return Gson().toJson(purchase)
    }

    @TypeConverter
    fun toPurchase(data: String): Purchase {
        return Gson().fromJson(data, Purchase::class.java)
    }
}