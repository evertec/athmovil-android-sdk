package com.evertecinc.athmovil.sdk.checkout.objects

import android.app.Application

class PaymentResultFlag : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private val stateLock = Any()

    @Volatile
    private var paymentRequest: ATHMPayment? = null

    @Volatile
    private var ecommerceAppName: String? = null

    fun getPaymentRequest(): ATHMPayment? = synchronized(stateLock) { paymentRequest }

    fun setPaymentRequest(request: ATHMPayment?) {
        synchronized(stateLock) { paymentRequest = request }
    }

    fun clearPaymentRequest() {
        synchronized(stateLock) { paymentRequest = null }
    }

    fun getEcommerceAppName(): String? = synchronized(stateLock) { ecommerceAppName }

    fun setEcommerceAppName(appName: String?) {
        synchronized(stateLock) { ecommerceAppName = appName }
    }

    companion object {
        @Volatile
        private var instance: PaymentResultFlag? = null

        @JvmStatic
        val applicationInstance: PaymentResultFlag
            get() {
                return instance ?: synchronized(this) {
                    instance ?: PaymentResultFlag().also { instance = it }
                }
            }
    }
}
