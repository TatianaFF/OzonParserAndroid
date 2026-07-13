package com.example.ozonpricetracking.core.products.domain.usecase

import com.example.ozonpricetracking.core.products.domain.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: Long) {
        repository.deleteProduct(productId)
    }
}