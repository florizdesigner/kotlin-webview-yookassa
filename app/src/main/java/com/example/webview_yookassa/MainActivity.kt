package com.example.webview_yookassa

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webview: WebView = findViewById(R.id.webview)
        webview.settings.apply {
            javaScriptEnabled = true
        }
        webview.setWebViewClient(DeeplinkWebViewClient())
        webview.loadUrl("https://yoomoney.ru/checkout/payments/v2/contract?orderId=2dd96393-000f-5000-a000-1773e73ba99c")
    }
}