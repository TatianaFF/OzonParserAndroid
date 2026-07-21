package com.example.ozonpricetracking.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.ozonpricetracking.data.local.dto.ProductDto
import com.example.ozonpricetracking.data.local.dto.ProductWithPriceHistoryDto
import com.example.ozonpricetracking.data.local.entity.History
import com.example.ozonpricetracking.data.local.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("""
        SELECT p.id, p.url, p.sku, p.title, p.image, p.createdAt, h.price 
        FROM product p
        JOIN history h ON p.id = h.productId
        WHERE h.id = (
            SELECT id 
            FROM history 
            WHERE productId = p.id 
            ORDER BY createdAt DESC 
            LIMIT 1
        ) 
        ORDER BY p.createdAt DESC
    """)
    fun getProducts(): Flow<List<ProductDto>>

    @Query("SELECT p.id, p.url, p.sku, p.title, p.image, p.createdAt, h.price FROM product p JOIN history h ON p.id = h.productId WHERE h.id = (SELECT id FROM history WHERE productId = p.id ORDER BY createdAt DESC LIMIT 1) ORDER BY p.createdAt DESC")
    suspend fun getProductsSnapshot(): List<ProductDto>

    @Transaction
    @Query("SELECT * FROM product WHERE id = :productId")
    suspend fun getProductWithHistory(productId: Long) : ProductWithPriceHistoryDto

    @Transaction
    suspend fun upsertProduct(product: Product): Long {
        val existingId = getIdBySku(product.sku)
        return if (existingId != null) {
            updateProduct(product.copy(id = existingId))
            existingId
        } else {
            insertProduct(product)
        }
    }

    @Query("SELECT id FROM product WHERE sku = :sku LIMIT 1")
    suspend fun getIdBySku(sku: String): Long?

    @Update
    suspend fun updateProduct(product: Product)

    @Insert
    suspend fun insertProduct(product: Product): Long

    @Insert
    suspend fun insertPrice(history: History)

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun deleteProduct(id: Long)
}