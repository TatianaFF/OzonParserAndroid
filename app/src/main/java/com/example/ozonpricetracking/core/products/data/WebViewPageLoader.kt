package com.example.ozonpricetracking.core.products.data

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WebViewPageLoader(private val context: Context) {
    suspend fun <T> loadAndParse(url: String, delayMs: Long = 3000, block: suspend (WebView) -> T): T =
        withContext(Dispatchers.Main) {
            var webView: WebView? = null
            try {
                webView = WebView(context).apply {
                    settings.javaScriptEnabled = true
                }

                loadUrlAndAwaitCommit(webView, url)
                delay(delayMs)

                block(webView)
            } finally {
                webView?.destroy()
            }
        }

    private suspend fun loadUrlAndAwaitCommit(webView: WebView, url: String) =
        suspendCancellableCoroutine { continuation ->
            webView.webViewClient = object : WebViewClient() {
                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    if (continuation.isActive) continuation.resume(Unit)
                }

                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(
                            IOException("Ошибка загрузки страницы WebView: $description (код $errorCode)")
                        )
                    }
                }
            }
            webView.loadUrl(url)
        }
}