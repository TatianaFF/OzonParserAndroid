package com.example.ozonpricetracking.feature.createProduct.data.repository

import com.example.ozonpricetracking.data.local.ProductDao
import com.example.ozonpricetracking.data.local.entity.History
import com.example.ozonpricetracking.data.local.entity.Product
import com.example.ozonpricetracking.data.remote.ParserFactory
import com.example.ozonpricetracking.data.remote.WebViewPageLoader
import com.example.ozonpricetracking.feature.createProduct.domain.CreateProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateProductRepositoryImpl @Inject constructor(
    private val dao: ProductDao,
    private val parserFactory: ParserFactory,
    private val pageLoader: WebViewPageLoader
) : CreateProductRepository {

    override suspend fun addProduct(url: String): Result<Unit> {
        return try {
            val parser = parserFactory.getParser(url)

            val parsedDto = pageLoader.loadAndParse(url) { webView ->
                parser.parsePage(webView)
            }

            if (parsedDto.price.isBlank() || parsedDto.sku.isBlank() || parsedDto.image.isBlank() || parsedDto.name.isBlank()) {
                return Result.failure(Exception("Ошибка парсинга: данные не полные"))
            }

            val price = parsedDto.price.toIntOrNull()
                ?: return Result.failure(Exception("Не удалось распарсить цену: ${parsedDto.price}"))

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

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
