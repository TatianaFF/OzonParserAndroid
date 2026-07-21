package com.example.ozonpricetracking.data.remote

import android.net.Uri
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ParserFactory @Inject constructor(
    private val parsers: Map<String, @JvmSuppressWildcards Provider<PageParser>>
) {
    fun getParser(url: String): PageParser {
        val domain = getDomain(url)
        return parsers[domain]?.get()
            ?: throw IllegalArgumentException("Нет подходящего парсера для домена: $domain (URL: $url)")
    }

    private fun getDomain(url: String): String {
        return try {
            val uri = Uri.parse(url)
            val host = uri.host ?: ""
            if (host.startsWith("www.")) host.substring(4) else host
        } catch (e: Exception) {
            ""
        }
    }
}
