package com.example.ozonpricetracking.core.utils

import com.example.ozonpricetracking.domain.model.OzonPriceHistoryInfo
import com.example.ozonpricetracking.domain.model.OzonProductInfo
import com.example.ozonpricetracking.domain.model.OzonProductWithPriceHistory

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

    val history = listOf(
        OzonPriceHistoryInfo(0, 1200, 1767225600000L + 3600000 * 10 + 60000 * 15),
        OzonPriceHistoryInfo(0, 1150, 1768003200000L + 3600000 * 14 + 60000 * 30),
        OzonPriceHistoryInfo(0, 1300, 1768867200000L + 3600000 * 18 + 60000 * 45),
        OzonPriceHistoryInfo(0, 1250, 1769904000000L + 3600000 * 9 + 60000 * 20),
        OzonPriceHistoryInfo(0, 1400, 1770681600000L + 3600000 * 12 + 60000 * 0),
        OzonPriceHistoryInfo(0, 1350, 1771545600000L + 3600000 * 21 + 60000 * 10),
        OzonPriceHistoryInfo(0, 1350, 1772323200000L + 3600000 * 8 + 60000 * 5),
        OzonPriceHistoryInfo(0, 1100, 1773100800000L + 3600000 * 15 + 60000 * 40),
        OzonPriceHistoryInfo(0, 1050, 1773964800000L + 3600000 * 23 + 60000 * 55),
        OzonPriceHistoryInfo(0, 1000, 1775001600000L + 3600000 * 7 + 60000 * 30),
        OzonPriceHistoryInfo(0, 950,  1775779200000L + 3600000 * 11 + 60000 * 15),
        OzonPriceHistoryInfo(0, 900,  1776643200000L + 3600000 * 16 + 60000 * 50),
        OzonPriceHistoryInfo(0, 1000, 1777593600000L + 3600000 * 10 + 60000 * 0),
        OzonPriceHistoryInfo(0, 1100, 1778371200000L + 3600000 * 13 + 60000 * 25),
        OzonPriceHistoryInfo(0, 1250, 1779235200000L + 3600000 * 19 + 60000 * 40),
        OzonPriceHistoryInfo(0, 1350, 1780272000000L + 3600000 * 6 + 60000 * 10),
        OzonPriceHistoryInfo(0, 1500, 1781049600000L + 3600000 * 14 + 60000 * 50),
        OzonPriceHistoryInfo(0, 1450, 1781913600000L + 3600000 * 22 + 60000 * 30)
    )
}
