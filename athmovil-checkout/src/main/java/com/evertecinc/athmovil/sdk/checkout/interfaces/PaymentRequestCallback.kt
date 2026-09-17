package com.evertecinc.athmovil.sdk.checkout.interfaces

import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentResponseObject

interface PaymentRequestCallback {
    fun onSuccess(response: PaymentResponseObject)
    fun onHttpError(errorBody: String?)
    fun onFailure(error: Throwable)
}

