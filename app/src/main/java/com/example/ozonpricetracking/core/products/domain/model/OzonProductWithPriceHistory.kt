package com.example.ozonpricetracking.core.products.domain.model

data class OzonProductWithPriceHistory (
    val product: OzonProductInfo,
    val history: List<OzonPriceHistoryInfo>
)