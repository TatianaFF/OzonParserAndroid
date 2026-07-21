package com.example.ozonpricetracking.domain.model

data class OzonProductWithPriceHistory (
    val product: OzonProductInfo,
    val history: List<OzonPriceHistoryInfo>
)