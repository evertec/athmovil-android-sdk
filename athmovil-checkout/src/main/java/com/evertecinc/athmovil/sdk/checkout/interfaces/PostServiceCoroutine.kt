package com.evertecinc.athmovil.sdk.checkout.interfaces

import com.evertecinc.athmovil.sdk.checkout.objects.payment.AuthorizationResponse
import com.evertecinc.athmovil.sdk.checkout.objects.payment.FindPaymentRequest
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentRequest
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentResponseObject
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PostServiceCoroutine {
    @POST(ConstantUtil.BasicData.API_ROUTE_PAYMENT_SERVICE)
    suspend fun paymentPost(
        @Header("Host") host: String,
        @Body body: PaymentRequest
    ): Response<PaymentResponseObject>

    @POST(ConstantUtil.BasicData.API_ROUTE_AUTORIZATION_SERVICE)
    suspend fun authorizationPost(
        @Header("Host") host: String,
        @Header("Authorization") token: String
    ): Response<AuthorizationResponse>

    @POST(ConstantUtil.BasicData.API_ROUTE_FINDPAYMENT_SERVICE)
    suspend fun findPaymentPost(
        @Header("Host") host: String,
        @Body body: FindPaymentRequest
    ): Response<AuthorizationResponse>
}

