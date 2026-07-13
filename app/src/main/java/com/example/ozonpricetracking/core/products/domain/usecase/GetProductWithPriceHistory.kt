package com.example.ozonpricetracking.core.products.domain.usecase

import com.example.ozonpricetracking.core.products.domain.ProductRepository
import com.example.ozonpricetracking.core.products.domain.model.OzonProductWithPriceHistory
import javax.inject.Inject

class GetProductWithPriceHistory @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: Long): Result<OzonProductWithPriceHistory> {
        return repository.getProductWithHistory(productId)
    }
}