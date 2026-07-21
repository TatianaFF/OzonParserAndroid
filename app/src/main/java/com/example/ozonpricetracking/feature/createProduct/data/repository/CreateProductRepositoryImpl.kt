package com.example.ozonpricetracking.feature.createProduct.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.ozonpricetracking.feature.createProduct.data.worker.AddProductWorker
import com.example.ozonpricetracking.feature.createProduct.domain.CreateProductRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateProductRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : CreateProductRepository {

    private val workManager = WorkManager.getInstance(context)

    override fun addProductBackground(url: String): Flow<WorkInfo?> {
        val workName = "add_product_${url.hashCode()}"
        val request = OneTimeWorkRequestBuilder<AddProductWorker>()
            .setInputData(Data.Builder().putString(AddProductWorker.KEY_URL, url).build())
            .addTag(AddProductWorker.TAG)
            .build()

        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            request
        )

        return workManager.getWorkInfoByIdFlow(request.id)
    }
}