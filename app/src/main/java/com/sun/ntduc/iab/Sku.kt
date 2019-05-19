package com.sun.ntduc.iab

data class Sku(

    val id :  String?,
    val canPurchase: Boolean, /* Not in SkuDetails; it's the augmentation */
    val type: String?,
    val price: String?,
    val title: String?,
    val description: String?,
    val originalJson: String?

)