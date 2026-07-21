package com.example.ozonpricetracking.feature.createProduct.domain.usecase

import com.example.ozonpricetracking.feature.createProduct.domain.CreateProductRepository
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val repository: CreateProductRepository
) {
    suspend operator fun invoke(url: String): Result<Unit> {
        return repository.addProduct(url)
    }
}