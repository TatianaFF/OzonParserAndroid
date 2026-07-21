package com.example.ozonpricetracking.feature.createProduct.domain.usecase

import androidx.work.WorkInfo
import com.example.ozonpricetracking.feature.createProduct.domain.CreateProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val repository: CreateProductRepository
) {
    operator fun invoke(url: String): Flow<WorkInfo?> {
        return repository.addProductBackground(url)
    }
}