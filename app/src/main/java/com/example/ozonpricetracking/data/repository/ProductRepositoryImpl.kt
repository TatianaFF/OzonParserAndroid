package com.example.ozonpricetracking.data.repository

import android.content.Context
import com.example.ozonpricetracking.data.local.ProductDao
import com.example.ozonpricetracking.data.local.entity.History
import com.example.ozonpricetracking.data.mapper.toDomain
import com.example.ozonpricetracking.domain.ProductRepository
import com.example.ozonpricetracking.domain.model.OzonProductInfo
import com.example.ozonpricetracking.domain.model.OzonProductWithPriceHistory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dao: ProductDao,
    @param:ApplicationContext private val context: Context
) : ProductRepository {

    override suspend fun getProductWithHistory(productId: Long): Result<OzonProductWithPriceHistory> {
        return try {
            Result.success(dao.getProductWithHistory(productId).toDomain())
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при получении истории продукта с ID: %d", productId)
            Result.failure(e)
        }
    }

    override fun getProducts(): Flow<List<OzonProductInfo>> {
        return dao.getProducts().map { dtoList ->
            dtoList.map { dto ->
                dto.toDomain()
            }

        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getProductsSnapshot(): List<OzonProductInfo> = withContext(Dispatchers.IO) {
        dao.getProductsSnapshot().map { it.toDomain() }
    }

    override suspend fun addPrice(productId: Long, price: Int) {
        dao.insertPrice(History(productId = productId, price = price))
    }

    override suspend fun deleteProduct(id: Long) {
        dao.deleteProduct(id)
    }
}