package com.example.ozonpricetracking.feature.dkma.presentation.components

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun HtmlTextViewer(htmlContent: String, modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()

    // Подстраиваем цвета текста под тему приложения
    val textColor = if (isDark) "#FFFFFF" else "#000000"
    val linkColor = if (isDark) "#64B5F6" else "#1565C0"

    val styledHtml = """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                body { 
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; 
                    color: $textColor; 
                    line-height: 1.6;
                    font-size: 16px;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                    max-width: 100%;
                    padding: 0px;
                }
                a { 
                    color: $linkColor;
                    word-break: break-all;
                }
                li { 
                    margin-bottom: 8px;
                    word-wrap: break-word;
                }
                p {
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }
                img {
                    max-width: 100%;
                    height: auto;
                }
                table {
                    max-width: 100%;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }
                td, th {
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }
                pre, code {
                    white-space: pre-wrap;
                    word-wrap: break-word;
                }
            </style>
        </head>
        <body>
            $htmlContent
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Отключаем фон WebView, чтобы просвечивал фон Compose-экрана
                setBackgroundColor(0)
                // Принудительно устанавливаем ширину
                setInitialScale(1)
                settings.let {
                    it.loadWithOverviewMode = true
                    it.useWideViewPort = true
                }
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, styledHtml, "text/html", "utf-8", null)
        }
    )
}