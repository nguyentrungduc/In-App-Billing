package com.sun.ntduc.iab

import androidx.room.*
import com.android.billingclient.api.Purchase
import com.google.gson.Gson

@Entity(tableName = "purchase_table")
@TypeConverters(PurchaseTypeCoverter::class)
class CachedPurchase (

    @PrimaryKey
    val purchase : String = ""

    )
