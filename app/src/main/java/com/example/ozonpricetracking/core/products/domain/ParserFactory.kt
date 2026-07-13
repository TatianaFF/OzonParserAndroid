package com.example.ozonpricetracking.core.products.domain

import com.example.ozonpricetracking.core.products.data.OzonParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParserFactory  @Inject constructor(
    private val ozonParser: OzonParser
){
    fun getParser(url: String): PageParser {
        return when {
            url.contains("ozon.ru", ignoreCase = true) -> ozonParser
            else -> throw IllegalArgumentException("Нет подходящего парсера для URL: $url")
        }
    }
}