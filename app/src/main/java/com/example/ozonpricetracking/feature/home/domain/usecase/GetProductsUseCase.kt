package com.example.ozonpricetracking.feature.home.domain.usecase

import com.example.ozonpricetracking.domain.ProductRepository
import com.example.ozonpricetracking.domain.model.OzonProductInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<OzonProductInfo>> {
        return repository.getProducts()
    }
}