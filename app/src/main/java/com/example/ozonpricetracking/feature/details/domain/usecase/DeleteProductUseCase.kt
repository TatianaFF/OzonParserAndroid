package com.example.ozonpricetracking.feature.details.domain.usecase

import com.example.ozonpricetracking.domain.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: Long) {
        repository.deleteProduct(productId)
    }
}