package com.example.ozonpricetracking.core.products.domain

import com.example.ozonpricetracking.core.products.domain.model.OzonProductInfo
import com.example.ozonpricetracking.core.products.domain.model.OzonProductWithPriceHistory
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProductWithHistory(productId: Long): Result<OzonProductWithPriceHistory>
    fun getProducts(): Flow<List<OzonProductInfo>>
    suspend fun getProductsSnapshot(): List<OzonProductInfo>
    suspend fun addProduct(url: String): Result<Long>
    suspend fun addPrice(productId: Long, price: Int)
    suspend fun deleteProduct(id: Long)
}