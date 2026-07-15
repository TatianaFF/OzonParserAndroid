package com.example.ozonpricetracking.core.utils

import com.example.ozonpricetracking.R
import com.example.ozonpricetracking.core.products.domain.model.OzonPriceHistoryInfo
import com.example.ozonpricetracking.core.products.domain.model.OzonProductInfo
import com.example.ozonpricetracking.core.products.domain.model.OzonProductWithPriceHistory

object PreviewData {
    val products = listOf(
        OzonProductInfo(
            id = 1,
            url = "https://ozon.ru/product/1",
            sku = "123456",
            title = "Смартфон Apple iPhone 15 Pro 256 ГБ",
            image = "",
            price = 120000,
            createdAt = System.currentTimeMillis()
        ),
        OzonProductInfo(
            id = 2,
            url = "https://ozon.ru/product/2",
            sku = "654321",
            title = "Наушники Sony WH-1000XM5",
            image = "",
            price = 35000,
            createdAt = System.currentTimeMillis()
        ),
        OzonProductInfo(
            id = 3,
            url = "https://ozon.ru/product/3",
            sku = "112233",
            title = "Умные часы Samsung Galaxy Watch 6",
            image = "",
            price = 25000,
            createdAt = System.currentTimeMillis()
        )
    )

    val productWithHistory = OzonProductWithPriceHistory(
        product = products[0],
        history = listOf(
            OzonPriceHistoryInfo(1, 120000, System.currentTimeMillis() - 86400000 * 30),
            OzonPriceHistoryInfo(2, 115000, System.currentTimeMillis() - 86400000 * 20),
            OzonPriceHistoryInfo(3, 118000, System.currentTimeMillis() - 86400000 * 10),
            OzonPriceHistoryInfo(4, 120000, System.currentTimeMillis())
        )
    )
}
