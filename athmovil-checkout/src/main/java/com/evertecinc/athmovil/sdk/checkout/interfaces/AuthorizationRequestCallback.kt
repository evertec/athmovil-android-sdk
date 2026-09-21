package com.evertecinc.athmovil.sdk.checkout.interfaces

import com.evertecinc.athmovil.sdk.checkout.objects.payment.AuthorizationResponse

interface AuthorizationRequestCallback {
    fun onSuccess(response: AuthorizationResponse)
    fun onHttpError(errorBody: String?)
    fun onFailure(error: Throwable)
}

