package com.evertecinc.athmovil.sdk.checkout.utils

import android.os.Build
import android.util.Log
import com.evertecinc.athmovil.sdk.checkout.BuildConfig
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object NewRelicConfig {

    @JvmField
    val NR_CONSTANT: String = ConstantUtil.ExceptionsLogs.NR_VARIABLE

    @JvmField
    val URL_CONSTANT: String = ConstantUtil.ExceptionsLogs.NR_URL

    @JvmStatic
    fun sendEventToNewRelic(
        eventType: String?,
        paymentReference: String?,
        paymentStatus: String?,
        merchantAppId: String?,
        buildType: String?
    ) {
        val insertKey = NR_CONSTANT.replace(Regex(ConstantUtil.RVARIBALES), "")
        val url = URL_CONSTANT.replace(Regex(ConstantUtil.RVARIBALES), "")
        val safeBuildType = if (buildType.isNullOrEmpty()) "PROD" else buildType

        val event = hashMapOf<String, Any?>(
            "eventType" to eventType,
            "payment_reference" to paymentReference,
            "payment_status" to paymentStatus,
            "merchant_app_id" to merchantAppId,
            "build_type" to safeBuildType,
            "timestamp" to System.currentTimeMillis(),
            "sdk_version" to ConstantUtil.SDK_VERSION,
            "sdk_platform" to ConstantUtil.SDK_PLATFORM,
            "device_os_version" to Build.VERSION.RELEASE,
            "device_os_model" to Build.MODEL
        )

        val jsonPayload = Gson().toJson(event)
        val body = jsonPayload.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("X-Insert-Key", insertKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                logForDebug("New Relic Event API payment_status: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (response.isSuccessful) {
                        logForDebug("Event sent to New Relic successfully")
                    } else {
                        logForDebug("New Relic API response payment_status: ${response.code}")
                    }
                }
            }
        })
    }

    private fun logForDebug(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(ConstantUtil.ExceptionsLogs.LOG_TAG, message)
        }
    }
}

