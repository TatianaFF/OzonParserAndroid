package com.example.ozonpricetracking.core.products.domain

import android.webkit.WebView
import com.example.ozonpricetracking.core.products.data.dto.ProductParsedDto

interface PageParser {
    suspend fun parsePage(webView: WebView): ProductParsedDto
}