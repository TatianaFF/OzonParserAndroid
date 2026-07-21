package com.example.ozonpricetracking.feature.details.domain.usecase

import com.example.ozonpricetracking.domain.ProductRepository
import com.example.ozonpricetracking.domain.model.OzonProductWithPriceHistory
import javax.inject.Inject

class GetProductWithPriceHistory @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: Long): Result<OzonProductWithPriceHistory> {
        return repository.getProductWithHistory(productId)
    }
}