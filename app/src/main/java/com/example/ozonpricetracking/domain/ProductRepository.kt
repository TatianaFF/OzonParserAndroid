package com.example.ozonpricetracking.domain

import com.example.ozonpricetracking.domain.model.OzonProductInfo
import com.example.ozonpricetracking.domain.model.OzonProductWithPriceHistory
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProductWithHistory(productId: Long): Result<OzonProductWithPriceHistory>
    fun getProducts(): Flow<List<OzonProductInfo>>
    suspend fun getProductsSnapshot(): List<OzonProductInfo>
    suspend fun addPrice(productId: Long, price: Int)
    suspend fun deleteProduct(id: Long)
}