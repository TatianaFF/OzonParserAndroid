package com.example.ozonpricetracking.core.products.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ozonpricetracking.core.products.domain.ParserFactory
import com.example.ozonpricetracking.core.products.domain.ProductRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.random.Random

@HiltWorker
class PriceUpdateWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ProductRepository,
    private val parserFactory: ParserFactory,
    private val pageLoader: WebViewPageLoader
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val products = repository.getProductsSnapshot()

            if (products.isEmpty()) {
                Timber.w("Фоновое обновление: список продуктов пуст.")
                return Result.success()
            }

            products.forEachIndexed { index, product ->
                try {
                    val currentPrice = parsePriceFromRemote(product.url)

                    if (currentPrice != null && currentPrice > 0) {
                        repository.addPrice(product.id, currentPrice)
                        Timber.d("Успешно обновлена цена для ${product.title}: $currentPrice")
                    } else {
                        Timber.w("Не удалось обновить цену для товара ID: %d, URL: %s", product.id, product.url)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Сбой при обновлении товара ID: %d, URL: %s", product.id, product.url)
                }

                if (index < products.lastIndex) {
                    val randomDelayMs = Random.nextLong(5000, 16000)
                    delay(randomDelayMs)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Критическая ошибка во время фонового обновления цен")
            Result.retry()
        }
    }

    private suspend fun parsePriceFromRemote(url: String): Int? {
        return try {
            val parser = parserFactory.getParser(url)

            val parsedDto = pageLoader.loadAndParse(url) { webView ->
                parser.parsePage(webView)
            }

            val price = parsedDto.price.toIntOrNull()
            if (price == null) {
                Timber.w("Парсер вернул пустую цену или неверный формат. Значение: %s, URL: %s", parsedDto.price, url)
            }
            price
        } catch (e: Exception) {
            Timber.e(e, "Ошибка парсинга цены в бэкграунде для URL: %s", url)
            null
        }
    }
}

