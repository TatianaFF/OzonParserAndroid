package com.example.ozonpricetracking.data.local.dto

data class ProductDto(
    val id: Long,
    val url: String,
    val sku: String,
    val title: String,
    val image: String,
    val createdAt: Long,
    val price: Int
)