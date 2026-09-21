package com.evertecinc.athmovil.sdk.checkout.utils

import com.evertecinc.athmovil.sdk.checkout.interfaces.AuthorizationRequestCallback
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentRequestCallback
import com.evertecinc.athmovil.sdk.checkout.interfaces.PostServiceCoroutine
import com.evertecinc.athmovil.sdk.checkout.objects.payment.AuthorizationResponse
import com.evertecinc.athmovil.sdk.checkout.objects.payment.FindPaymentRequest
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentRequest
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentResponseObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentCoroutineRunnerUT {

    private val testDispatcher = StandardTestDispatcher()

    private val postService: PostServiceCoroutine = mock()
    private val paymentCallback: PaymentRequestCallback = mock()
    private val authorizationCallback: AuthorizationRequestCallback = mock()

    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        PaymentCoroutineRunner.setDispatchersForTesting(testDispatcher, testDispatcher)
    }

    @After
    fun tearDown() {
        PaymentCoroutineRunner.resetDispatchers()
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun requestPayment_whenSuccessful_callsOnSuccess() = runTest(testDispatcher) {
        whenever(postService.paymentPost(any(), any())).thenReturn(Response.success(PaymentResponseObject()))

        PaymentCoroutineRunner.requestPayment(postService, "host.test", PaymentRequest(), paymentCallback)
        advanceUntilIdle()

        verify(paymentCallback).onSuccess(any())
        verify(paymentCallback, never()).onHttpError(any())
        verify(paymentCallback, never()).onFailure(any())
    }

    @Test
    fun requestAuthorization_whenThrows_callsOnFailure() = runTest(testDispatcher) {
        val networkError = IllegalStateException("network down")
        whenever(postService.authorizationPost(any(), any())).thenThrow(networkError)

        PaymentCoroutineRunner.requestAuthorization(postService, "host.test", "Bearer token", authorizationCallback)
        advanceUntilIdle()

        verify(authorizationCallback).onFailure(eq(networkError))
        verify(authorizationCallback, never()).onSuccess(any())
    }

    @Test
    fun requestFindPayment_whenHttpError_callsOnHttpError() = runTest(testDispatcher) {
        val errorBody = "bad request".toResponseBody("text/plain".toMediaType())
        whenever(postService.findPaymentPost(any(), any())).thenReturn(Response.error(400, errorBody))

        PaymentCoroutineRunner.requestFindPayment(
            postService,
            "host.test",
            FindPaymentRequest("ecommerce-id", "public-token"),
            authorizationCallback
        )
        advanceUntilIdle()

        verify(authorizationCallback).onHttpError(any())
        verify(authorizationCallback, never()).onSuccess(any<AuthorizationResponse>())
    }
}

