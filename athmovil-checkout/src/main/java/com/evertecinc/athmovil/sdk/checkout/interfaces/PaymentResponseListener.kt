package com.evertecinc.athmovil.sdk.checkout.interfaces

import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData
import java.util.Date

interface PaymentResponseListener {

    fun onCompletedPayment(date: Date, result: PaymentReturnedData)

    fun onCancelledPayment(date: Date, result: PaymentReturnedData)

    fun onExpiredPayment(date: Date, result: PaymentReturnedData)

    fun onFailedPayment(date: Date, result: PaymentReturnedData)

    fun onPaymentException(error: String, description: String)

}
