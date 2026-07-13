package com.example.ozonpricetracking.core.products.domain.model

data class OzonPriceHistoryInfo (
    val id: Long,
    val price: Int,
    val createdAt: Long
)