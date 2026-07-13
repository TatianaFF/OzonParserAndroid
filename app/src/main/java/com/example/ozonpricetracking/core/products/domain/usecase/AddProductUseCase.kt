package com.example.ozonpricetracking.core.products.domain.usecase

import com.example.ozonpricetracking.core.products.domain.ProductRepository
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(url: String): Result<Long> {
        return repository.addProduct(url)
    }
}