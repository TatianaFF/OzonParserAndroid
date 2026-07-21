package com.example.ozonpricetracking.data.remote

import android.webkit.WebView
import com.example.ozonpricetracking.data.remote.dto.ProductParsedDto

interface PageParser {
    suspend fun parsePage(webView: WebView): ProductParsedDto
}