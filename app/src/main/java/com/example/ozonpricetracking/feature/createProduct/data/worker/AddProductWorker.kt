package com.example.ozonpricetracking.feature.createProduct.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.ozonpricetracking.data.local.ProductDao
import com.example.ozonpricetracking.data.local.entity.History
import com.example.ozonpricetracking.data.local.entity.Product
import com.example.ozonpricetracking.data.remote.ParserFactory
import com.example.ozonpricetracking.data.remote.WebViewPageLoader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class AddProductWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: ProductDao,
    private val parserFactory: ParserFactory,
    private val pageLoader: WebViewPageLoader
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val url = inputData.getString(KEY_URL) ?: return Result.failure()

        return try {
            val parser = parserFactory.getParser(url)

            val parsedDto = pageLoader.loadAndParse(url) { webView ->
                parser.parsePage(webView)
            }

            if (parsedDto.price.isBlank() || parsedDto.sku.isBlank() || parsedDto.image.isBlank() || parsedDto.name.isBlank()) {
                val errorMsg = "Ошибка парсинга: данные не полные"
                return Result.failure(createErrorData(errorMsg))
            }

            val price = parsedDto.price.toIntOrNull()
            if (price == null) {
                val errorMsg = "Не удалось распарсить цену: ${parsedDto.price}"
                return Result.failure(createErrorData(errorMsg))
            }

            withContext(Dispatchers.IO) {
                val productId = dao.upsertProduct(
                    Product(
                        url = url,
                        sku = parsedDto.sku,
                        title = parsedDto.name,
                        image = parsedDto.image
                    )
                )
                dao.insertPrice(History(productId = productId, price = price))
            }

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при фоновом добавлении продукта. URL: %s", url)
            Result.failure(createErrorData(e.localizedMessage ?: "Неизвестная ошибка"))
        }
    }

    private fun createErrorData(message: String): Data {
        return Data.Builder()
            .putString(KEY_ERROR, message)
            .build()
    }

    companion object {
        const val KEY_URL = "key_url"
        const val KEY_ERROR = "key_error"
        const val TAG = "AddProductWorker"
    }
}