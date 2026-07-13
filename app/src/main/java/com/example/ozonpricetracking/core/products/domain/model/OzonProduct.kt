package com.example.ozonpricetracking.core.products.domain.model

data class OzonProduct(
    val id: Long,
    val title: String,
    val image: String,
    val createdAt: Long,
    val price: Int
)
