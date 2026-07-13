package com.example.ozonpricetracking.core.database.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.example.ozonpricetracking.core.database.entity.History
import com.example.ozonpricetracking.core.database.entity.Product

data class ProductWithPriceHistoryDto(
    @Embedded
    val product: Product,

    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val history: List<History>
)