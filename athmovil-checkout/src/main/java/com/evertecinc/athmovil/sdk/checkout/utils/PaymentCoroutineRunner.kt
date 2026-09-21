package com.evertecinc.athmovil.sdk.checkout.utils

import androidx.annotation.VisibleForTesting
import com.evertecinc.athmovil.sdk.checkout.interfaces.AuthorizationRequestCallback
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentRequestCallback
import com.evertecinc.athmovil.sdk.checkout.interfaces.PostServiceCoroutine
import com.evertecinc.athmovil.sdk.checkout.objects.payment.FindPaymentRequest
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

object PaymentCoroutineRunner {
    private val dispatcherLock = Any()

    @VisibleForTesting
    internal var workerDispatcher: CoroutineDispatcher = Dispatchers.IO
        private set

    @VisibleForTesting
    internal var callbackDispatcher: CoroutineDispatcher = Dispatchers.Main
        private set

    @VisibleForTesting
    @JvmStatic
    fun setDispatchersForTesting(worker: CoroutineDispatcher, callback: CoroutineDispatcher) {
        synchronized(dispatcherLock) {
            workerDispatcher = worker
            callbackDispatcher = callback
        }
    }

    @VisibleForTesting
    @JvmStatic
    fun resetDispatchers() {
        synchronized(dispatcherLock) {
            workerDispatcher = Dispatchers.IO
            callbackDispatcher = Dispatchers.Main
        }
    }

    private fun paymentScope(): CoroutineScope {
        val currentWorkerDispatcher = synchronized(dispatcherLock) { workerDispatcher }
        return CoroutineScope(SupervisorJob() + currentWorkerDispatcher)
    }

    private suspend fun runOnCallbackDispatcher(block: suspend () -> Unit) {
        val currentCallbackDispatcher = synchronized(dispatcherLock) { callbackDispatcher }
        withContext(currentCallbackDispatcher) { block() }
    }

    private fun <T> executeRequest(
        requestCall: suspend () -> Response<T>,
        onSuccess: (T) -> Unit,
        onHttpError: (String?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        paymentScope().launch {
            try {
                val response = requestCall()
                runOnCallbackDispatcher {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        onSuccess(body)
                    } else {
                        onHttpError(response.errorBody()?.string())
                    }
                }
            } catch (error: Throwable) {
                runOnCallbackDispatcher {
                    onFailure(error)
                }
            }
        }
    }

    @JvmStatic
    fun requestPayment(
        postService: PostServiceCoroutine,
        host: String,
        body: PaymentRequest,
        callback: PaymentRequestCallback
    ) {
        executeRequest(
            requestCall = { postService.paymentPost(host, body) },
            onSuccess = callback::onSuccess,
            onHttpError = callback::onHttpError,
            onFailure = callback::onFailure
        )
    }

    @JvmStatic
    fun requestAuthorization(
        postService: PostServiceCoroutine,
        host: String,
        token: String,
        callback: AuthorizationRequestCallback
    ) {
        executeRequest(
            requestCall = { postService.authorizationPost(host, token) },
            onSuccess = callback::onSuccess,
            onHttpError = callback::onHttpError,
            onFailure = callback::onFailure
        )
    }

    @JvmStatic
    fun requestFindPayment(
        postService: PostServiceCoroutine,
        host: String,
        request: FindPaymentRequest,
        callback: AuthorizationRequestCallback
    ) {
        executeRequest(
            requestCall = { postService.findPaymentPost(host, request) },
            onSuccess = callback::onSuccess,
            onHttpError = callback::onHttpError,
            onFailure = callback::onFailure
        )
    }
}

