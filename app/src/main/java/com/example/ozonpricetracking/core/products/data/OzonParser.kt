package com.example.ozonpricetracking.core.products.data

import android.webkit.WebView
import com.example.ozonpricetracking.core.products.data.dto.ProductParsedDto
import com.example.ozonpricetracking.core.products.domain.PageParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class OzonParser @Inject constructor() : PageParser {

    override suspend fun parsePage (webView: WebView): ProductParsedDto = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            webView.evaluateJavascript(getAllDataScript()) { jsonResult ->
                try {
                    val cleanJson = if (jsonResult != null && jsonResult.startsWith("\"") && jsonResult.endsWith("\"")) {
                        jsonResult.substring(1, jsonResult.length - 1)
                            .replace("\\\"", "\"")
                            .replace("\\\\", "\\")
                    } else {
                        jsonResult ?: "{}"
                    }

                    val parsedData = Json.decodeFromString<ProductParsedDto>(cleanJson)
                    continuation.resume(parsedData)
                } catch (e: Exception) {
                    continuation.resume(ProductParsedDto(error = e.localizedMessage))
                }
            }
        }
    }

    fun getAllDataScript(): String = """
        (function() {
            try {
                let script = document.querySelector('script[type="application/ld+json"]');
                let jsonLd = script ? JSON.parse(script.textContent) : {};
                
                let price = '0';
                
                let widget = document.querySelector('[id*="webMobPriceCompact"]');
                if (widget) {
                    let stateElement = widget;
                    let stateJson = JSON.parse(stateElement.getAttribute('data-state'));
                    let priceText = stateJson.mainPrice?.price?.[0]?.text || '';
                    price = priceText.replace(/[^\d]/g, '');
                }
                
                return JSON.stringify({
                    name: jsonLd.name || '',
                    sku: jsonLd.sku || '',
                    image: jsonLd.image || '',
                    price: price
                });
            } catch(e) {
                return JSON.stringify({ error: e.message });
            }
        })()
    """.trimIndent()
}
