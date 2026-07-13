package com.example.ozonpricetracking.core.products.data

import android.content.Context
import com.example.ozonpricetracking.core.database.ProductDao
import com.example.ozonpricetracking.core.database.entity.History
import com.example.ozonpricetracking.core.database.entity.Product
import com.example.ozonpricetracking.core.products.domain.ParserFactory
import com.example.ozonpricetracking.core.products.domain.ProductRepository
import com.example.ozonpricetracking.core.products.domain.model.OzonProductInfo
import com.example.ozonpricetracking.core.products.domain.model.OzonProductWithPriceHistory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dao: ProductDao,
    private val parserFactory: ParserFactory,
    private val pageLoader: WebViewPageLoader,
    @param:ApplicationContext private val context: Context
) : ProductRepository {

    override suspend fun getProductWithHistory(productId: Long): Result<OzonProductWithPriceHistory> {
        return try {
            Result.success(dao.getProductWithHistory(productId).toDomain())
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при получении истории продукта с ID: %d", productId)
            Result.failure(e)
        }
    }

    override fun getProducts(): Flow<List<OzonProductInfo>> {
        return dao.getProducts().map { dtoList ->
            dtoList.map { dto ->
                dto.toDomain()
            }

        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getProductsSnapshot(): List<OzonProductInfo> = withContext(Dispatchers.IO) {
        dao.getProductsSnapshot().map { it.toDomain() }
    }

    override suspend fun addProduct(url: String): Result<Long> {
        val parser = parserFactory.getParser(url)

        val parsedDto = try {
            pageLoader.loadAndParse(url) { webView ->
                parser.parsePage(webView)
            }
        } catch (e: Exception) {
            Timber.e(e, "Критический сбой при парсинге страницы. URL: %s", url)
            return Result.failure(e)
        }

        if (parsedDto.price.isBlank() || parsedDto.sku.isBlank() || parsedDto.image.isBlank() || parsedDto.name.isBlank())
            return Result.failure(Exception("PARSING_ERROR"))

        val price = parsedDto.price.toIntOrNull()
        if (price == null) {
            Timber.e("Не удалось распарсить цену. Пришло значение: %s, URL: %s", parsedDto.price, url)
            return Result.failure(Exception("INVALID_PRICE_FORMAT"))
        }

        return try {
            withContext(Dispatchers.IO) {
                val productId = dao.upsertProduct(
                    Product(id = 0, url = url, sku = parsedDto.sku, title = parsedDto.name, image = parsedDto.image)
                )
                dao.insertPrice(History(productId = productId, price = price))
                Result.success(productId)
            }
        } catch (e: Exception) {
            Timber.e(e, "Ошибка записи в БД при добавлении продукта. SKU: %s, URL: %s", parsedDto.sku, url)
            Result.failure(e)
        }
    }

    override suspend fun addPrice(productId: Long, price: Int) {
        dao.insertPrice(History(productId = productId, price = price))
    }

    override suspend fun deleteProduct(id: Long) {
        dao.deleteProduct(id)
    }
}