package com.example.ozonpricetracking.feature.createProduct.domain

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface CreateProductRepository {
    fun addProductBackground(url: String): Flow<WorkInfo?>
}