package com.example.ozonpricetracking.feature.createProduct.domain

interface CreateProductRepository {
    suspend fun addProduct(url: String): Result<Unit>
}