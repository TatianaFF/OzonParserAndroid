package com.example.ozonpricetracking.domain.model

data class OzonPriceHistoryInfo (
    val id: Long,
    val price: Int,
    val createdAt: Long
)