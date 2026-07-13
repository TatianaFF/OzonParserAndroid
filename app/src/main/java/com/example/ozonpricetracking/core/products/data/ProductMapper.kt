package com.example.ozonpricetracking.core.products.data

import com.example.ozonpricetracking.core.database.dto.ProductDto
import com.example.ozonpricetracking.core.database.dto.ProductWithPriceHistoryDto
import com.example.ozonpricetracking.core.products.domain.model.OzonPriceHistoryInfo
import com.example.ozonpricetracking.core.products.domain.model.OzonProductInfo
import com.example.ozonpricetracking.core.products.domain.model.OzonProductWithPriceHistory

fun ProductWithPriceHistoryDto.toDomain(): OzonProductWithPriceHistory {
    val sortedHistory = this.history
        .sortedBy { it.createdAt }
        .map { historyEntity ->
            OzonPriceHistoryInfo(
                id = historyEntity.id,
                price = historyEntity.price,
                createdAt = historyEntity.createdAt
            )
        }

    val latestPriceValue = sortedHistory.lastOrNull()?.price ?: 0

    return OzonProductWithPriceHistory(
        product = OzonProductInfo(
            id = this.product.id,
            url = this.product.url,
            sku = this.product.sku,
            title = this.product.title,
            image = this.product.image,
            createdAt = this.product.createdAt,
            price = latestPriceValue
        ),
        history = sortedHistory
    )
}

fun ProductDto.toDomain() : OzonProductInfo {
    return OzonProductInfo(
        id = this.id,
        url = this.url,
        sku = this.sku,
        title = this.title,
        image = this.image,
        createdAt = createdAt,
        price = this.price
    )}