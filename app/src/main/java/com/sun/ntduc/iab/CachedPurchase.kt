package com.sun.ntduc.iab

import androidx.room.*

@Entity(tableName = "purchase_table")
@TypeConverters(PurchaseTypeCoverter::class)
class CachedPurchase (

    @PrimaryKey
    val purchase : String = ""

    )
