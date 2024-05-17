package com.example.webview_yookassa

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity
import android.graphics.Bitmap

class DeeplinkWebViewClient: WebViewClient() {
    private val schemes: MutableSet<String> = mutableSetOf()

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        schemes.clear()
        schemes.addAll(URLSchemes.checkLastUpdateDate()!!)
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val url = request.url.toString()

        fun checkURLScheme(request: WebResourceRequest, schemes: MutableSet<String>): Boolean {
            var result = false
            val scheme = request.url.scheme.toString()
            for (item: String in schemes) {
                if (scheme.contains(item)) {
                    result = true
                }
            }
            return result
        }

        // Вариант 3, совмещённый: Сберпей - через PackageManager, всё остальное через запуск Activity

        try {
            Log.d("WebviewLogger", schemes.toString())
            if (checkURLScheme(request, schemes)) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                if (checkURLScheme(request, URLSchemes.getSberPaySchemes()) && intent.resolveActivity(view.context.packageManager) == null) return true
                startActivity(view.context, intent, null)
            } else if (URLUtil.isNetworkUrl(url)) {
                return false
            } else {
                return true
            }
        } catch (e: ActivityNotFoundException) {
            Log.w("WebviewLogger", "Application is not found (ActivityNotFoundException). URL: $url")
            // или другая обработка, если не смогли открыть приложение на устройстве
        }

        return true
    }
}


// Вариант 1, через packageManager (нужно объявлять схемы в AndroidManifest.xml)

//        try {
//            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
//            if (intent.resolveActivity(view.context.packageManager) !== null) {
//                startActivity(view.context, intent, null)
//            } else {
//                view.loadUrl(url)
//            }
//        } catch (e: Exception) {
//            Log.d("CustomTag", "IllegalStateException: $e")
//        }

// Вариант 2, через попытку запуска Activity

//        try {
//            if (checkURLScheme(request, schemes)) {
//                val intent = Intent(Intent.ACTION_VIEW).apply {
//                    data = Uri.parse(url)
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//                startActivity(view.context, intent, null)
//            } else {
//                return false
//            }
//        } catch (e: ActivityNotFoundException) {
//            Log.w("DeeplinkChecker", "Application is not found. URL: $url")
//            // или другая обработка, если не смогли открыть приложение на устройстве
//        }