package com.example.ozonpricetracking.domain.model

data class OzonProductInfo (
    val id: Long,
    val url: String,
    val sku: String,
    val title: String,
    val image: String,
    val price: Int,
    val createdAt: Long
)