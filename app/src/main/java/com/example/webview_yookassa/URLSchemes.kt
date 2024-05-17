package com.example.webview_yookassa

import android.util.Log
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.Period
import kotlin.concurrent.thread

object URLSchemes {
    private val MIRPAY_SCHEMES = mutableSetOf("mirpay")
    private val SBERPAY_SCHEMES = mutableSetOf("sberpay", "sbolpay", "btripsexpenses")
    private val TINKOFF_SCHEMES = mutableSetOf("tinkoffbank")
    private val SBP_SCHEMES = mutableSetOf<String>()

    private val SBP_BANKS_URL = "https://qr.nspk.ru/proxyapp/c2bmembers.json"
    private var LAST_UPDATE_DATE: LocalDate = LocalDate.now()
    private val PERIOD_DAYS_FOR_UPDATE_LIST: Int = 7

    fun getSberPaySchemes(): MutableSet<String> {
        return SBERPAY_SCHEMES
    }

    fun getAllAvailableSchemes(): MutableSet<String> {
        val AVAILABLE_SCHEMES = mutableSetOf<String>()

        AVAILABLE_SCHEMES.addAll(SBERPAY_SCHEMES)
        AVAILABLE_SCHEMES.addAll(SBP_SCHEMES)
        AVAILABLE_SCHEMES.addAll(TINKOFF_SCHEMES)
        AVAILABLE_SCHEMES.addAll(MIRPAY_SCHEMES)

        return AVAILABLE_SCHEMES
    }

    fun checkLastUpdateDate(): MutableSet<String>? {
        try {
            if (Period.between(LAST_UPDATE_DATE, LocalDate.now()).days > PERIOD_DAYS_FOR_UPDATE_LIST || SBP_SCHEMES.isEmpty()) {
                Log.d("CustomTag","Прошло более $PERIOD_DAYS_FOR_UPDATE_LIST дней с последнего обновления")
                val result = getActualSchemes()
                val responseResult = JSONArray(JSONObject(result).get("dictionary").toString())

                SBP_SCHEMES.clear()

                for (i in 0 until responseResult.length()) {
                    val bank: String = responseResult.getJSONObject(i).get("schema").toString()
                    SBP_SCHEMES.add(bank)
                }
            } else {
                Log.d("CustomTag","Прошло менее $PERIOD_DAYS_FOR_UPDATE_LIST дней с последнего обновления")
            }
            return getAllAvailableSchemes()
        } catch (e: Exception) {
            Log.d("WebviewLogger", "Exception! $e")
            return null
        }
    }

    fun getActualSchemes(): String {
        try {
            var responseBody: String = ""
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(SBP_BANKS_URL)
                .build()

            thread {
                val response = client.newCall(request).execute()
                responseBody = response.body!!.string()
            }.join()

            return responseBody
        } catch (e: Exception) {
            Log.d("CustomTag", "Text")
            throw e
        }
    }
}