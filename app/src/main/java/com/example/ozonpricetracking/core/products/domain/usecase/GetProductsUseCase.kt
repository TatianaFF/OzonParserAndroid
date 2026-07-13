package com.example.ozonpricetracking.core.products.domain.usecase

import com.example.ozonpricetracking.core.products.domain.ProductRepository
import com.example.ozonpricetracking.core.products.domain.model.OzonProductInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<OzonProductInfo>> {
        return repository.getProducts()
    }
}