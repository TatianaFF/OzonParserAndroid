package com.example.ozonpricetracking.data.local.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.example.ozonpricetracking.data.local.entity.History
import com.example.ozonpricetracking.data.local.entity.Product

data class ProductWithPriceHistoryDto(
    @Embedded
    val product: Product,

    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val history: List<History>
)