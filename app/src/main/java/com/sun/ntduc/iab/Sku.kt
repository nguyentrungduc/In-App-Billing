package com.sun.ntduc.iab

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sku(
    val canPurchase: Boolean,
    @PrimaryKey val sku: String,
    val type: String?,
    val price: String?,
    val title: String?,
    val description: String?,
    val originalJson: String?
)