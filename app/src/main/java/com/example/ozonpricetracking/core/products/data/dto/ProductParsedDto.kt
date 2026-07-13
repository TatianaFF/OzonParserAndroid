package com.example.ozonpricetracking.core.products.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductParsedDto(
    val name: String = "",
    val sku: String = "",
    val image: String = "",
    val price: String = "",
    val error: String? = null
)