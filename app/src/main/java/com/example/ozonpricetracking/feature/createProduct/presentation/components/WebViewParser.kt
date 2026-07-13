//package com.example.ozonpricetracking.feature.createProduct.presentation.components
//
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.view.ViewGroup
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.size
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import com.example.ozonpricetracking.core.products.data.OzonParser
//import com.example.ozonpricetracking.feature.createProduct.presentation.ProductData
//import org.json.JSONObject
//
//@Composable
//fun WebViewParser(
//    url: String,
//    onComplete: (ProductData) -> Unit
//) {
//    val context = LocalContext.current
//
//    DisposableEffect(Unit) {
//        val handler = Handler(Looper.getMainLooper())
//
//        val webView = WebView(context).apply {
//            layoutParams = ViewGroup.LayoutParams(0, 0)
//            settings.apply {
//                javaScriptEnabled = true
////                domStorageEnabled = true
////                loadWithOverviewMode = true
////                useWideViewPort = true
////                setSupportZoom(true)
////                builtInZoomControls = true
////                displayZoomControls = false
////                cacheMode = WebSettings.LOAD_NO_CACHE
//            }
//            webViewClient = object : WebViewClient() {
//                override fun onPageCommitVisible(view: WebView?, _url: String?) {
//                    handler.postDelayed({
////                        evaluateJavascript(OzonParser.getHtmlScript()) { result ->
////                           var t = result
////                        }
//
//                        evaluateJavascript(OzonParser.getAllDataScript()) { result ->
//
//                            val withoutQuotes = result.trim('"')
//                            val unescaped = withoutQuotes
//                                .replace("\\\"", "\"")
//                                .replace("\\\\", "\\")
//
//                            val json = JSONObject(unescaped)
//
//                            Log.e("DATA", json.toString())
//
//                            onComplete(
//                                ProductData(
//                                    name = json.optString("name", ""),
//                                    sku = json.optString("sku", ""),
//                                    image = json.optString("image", ""),
//                                    price = json.optString("price", "0").toInt(),
//                                    url = url
//                                )
//                            )
//                        }
//                    }, 3000)
//
//                }
//            }
//            loadUrl(url)
//        }
//
//        onDispose {
//            webView.destroy()
//        }
//    }
//
//    Box(modifier = Modifier.size(0.dp))
//}