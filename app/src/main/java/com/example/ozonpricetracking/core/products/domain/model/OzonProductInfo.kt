package com.example.ozonpricetracking.core.products.domain.model

data class OzonProductInfo (
    val id: Long,
    val url: String,
    val sku: String,
    val title: String,
    val image: String,
    val price: Int,
    val createdAt: Long
)