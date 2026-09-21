package com.evertecinc.athmovil.sdk.checkout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment
import com.evertecinc.athmovil.sdk.checkout.objects.Items
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentResultFlag
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData
import com.evertecinc.athmovil.sdk.checkout.objects.payment.AuthorizationResponse
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil
import com.evertecinc.athmovil.sdk.checkout.utils.Util
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.evertecinc.athmovil.sdk.checkout.utils.NewRelicConfig.sendEventToNewRelic
import com.evertecinc.athmovil.sdk.checkout.utils.Util.getDateFormat
import java.util.ArrayList
import java.util.Date

object PaymentResponse {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var findPaymentHandler: (Context, PaymentResponseListener) -> Unit = { context, listener ->
        OpenATHM.findPaymentServices(listener, context)
    }

    @JvmStatic
    fun validatePaymentResponse(context: Context, listener: PaymentResponseListener, intent: Intent? = null) {
        if (intent != null && hasValidPaymentResult(intent)) {
            val publicToken = Util.getPrefsString(ConstantUtil.BasicData.PUBLIC_TOK, context)
            if (publicToken.equals(ConstantUtil.Tokens.DUMMY, ignoreCase = true)) {
                validateDataResponse(intent, listener)
                return
            }
        }

        val contactFromIntent = extractContactFromIntent(intent)
        val enrichedListener = if (contactFromIntent != null) {
            ContactEnrichedListener(listener, contactFromIntent)
        } else {
            listener
        }

        findPaymentHandler(context, enrichedListener)
    }

    private data class ContactData(val name: String?, val phoneNumber: String?, val email: String?)

    private fun extractContactFromIntent(intent: Intent?): ContactData? {
        val json = intent?.extras?.getString(ConstantUtil.ReturnedJson.RETURNED_JSON_KEY)
            ?: return null
        return try {
            val parsed = Gson().fromJson(json, PaymentReturnedData::class.java)
            val name = parsed?.name
            val phone = parsed?.phoneNumber
            val email = parsed?.email
            if (name.isNullOrBlank() && phone.isNullOrBlank() && email.isNullOrBlank()) null
            else ContactData(name, phone, email)
        } catch (_: JsonSyntaxException) {
            null
        }
    }

    private class ContactEnrichedListener(
        private val delegate: PaymentResponseListener,
        private val contact: ContactData
    ) : PaymentResponseListener {

        private fun getData(result: PaymentReturnedData): PaymentReturnedData {
            if (result.name.isNullOrBlank()) result.name = contact.name
            if (result.phoneNumber.isNullOrBlank()) result.phoneNumber = contact.phoneNumber
            if (result.email.isNullOrBlank()) result.email = contact.email
            return result
        }

        override fun onCompletedPayment(date: Date, result: PaymentReturnedData) =
            delegate.onCompletedPayment(date, getData(result))

        override fun onCancelledPayment(date: Date, result: PaymentReturnedData) =
            delegate.onCancelledPayment(date, getData(result))

        override fun onExpiredPayment(date: Date, result: PaymentReturnedData) =
            delegate.onExpiredPayment(date, getData(result))

        override fun onFailedPayment(date: Date, result: PaymentReturnedData) =
            delegate.onFailedPayment(date, getData(result))

        override fun onPaymentException(error: String, description: String) =
            delegate.onPaymentException(error, description)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun hasValidPaymentResult(intent: Intent): Boolean {
        return !intent.extras
            ?.getString(ConstantUtil.ReturnedJson.RETURNED_JSON_KEY)
            .isNullOrBlank()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun resetFindPaymentFallbackForTesting() {
        findPaymentHandler = { context, listener ->
            OpenATHM.findPaymentServices(listener, context)
        }
    }

    internal fun createFindPaymentCallback(
        context: Context,
        listener: PaymentResponseListener,
        authorizationHandler: (Context, PaymentResponseListener) -> Unit
    ): com.evertecinc.athmovil.sdk.checkout.interfaces.AuthorizationRequestCallback {
        return object : com.evertecinc.athmovil.sdk.checkout.interfaces.AuthorizationRequestCallback {
            override fun onSuccess(response: AuthorizationResponse) {
                val status = response.data?.ecommerceStatus?.uppercase()
                if (status.equals("CONFIRM", ignoreCase = true)) {
                    authorizationHandler(context, listener)
                } else {
                    validateFindPaymentResponse(listener, response)
                }
            }

            override fun onHttpError(errorBody: String?) {
                val errorResponse = AuthorizationResponse()
                errorResponse.status = ConstantUtil.ResponseStatus.SERVICE_ERROR
                validateFindPaymentResponse(listener, errorResponse)
            }

            override fun onFailure(error: Throwable) {
                val errorResponse = AuthorizationResponse()
                errorResponse.status = ConstantUtil.ResponseStatus.SERVICE_ERROR
                validateFindPaymentResponse(listener, errorResponse)
            }
        }
    }

    private fun notifyException(listener: PaymentResponseListener, message: String) {
        PaymentResultFlag.applicationInstance.clearPaymentRequest()
        listener.onPaymentException(ConstantUtil.ExceptionsLogs.RESPONSE_EXCEPTION_TITLE, message)
    }

    private fun setDefaultError(listener: PaymentResponseListener) {
        notifyException(listener, ConstantUtil.ExceptionsLogs.RESPONSE_NULL_EXCEPTION)
    }

    private fun setDecodeJsonError(listener: PaymentResponseListener) {
        notifyException(listener, ConstantUtil.ExceptionsLogs.DECODE_JSON_LOG_MESSAGE)
    }

    private fun validateDataResponse(intent: Intent, listener: PaymentResponseListener) {
        val extras = intent.extras
        val jsonResponseValue = extras?.getString(ConstantUtil.ReturnedJson.RETURNED_JSON_KEY)
        if (jsonResponseValue.isNullOrBlank()) {
            setDefaultError(listener)
            return
        }

        if (jsonResponseValue.equals(ConstantUtil.ExceptionsLogs.EXCEPTION, ignoreCase = true)) {
            handleExceptionResponse(extras, listener)
            return
        }

        val result = checkIfDummy(jsonResponseValue, listener)
        validatePaymentResponse(result, listener, null)
    }

    private fun handleExceptionResponse(extras: Bundle?, listener: PaymentResponseListener) {
        PaymentResultFlag.applicationInstance.clearPaymentRequest()
        val exceptionCause = extras?.getString(ConstantUtil.ExceptionsLogs.EXCEPTION_CAUSE)
        val exceptionMessage = extras?.getString(ConstantUtil.ExceptionsLogs.EXCEPTION)

        if (exceptionCause.isNullOrBlank() || exceptionMessage.isNullOrBlank()) {
            setDecodeJsonError(listener)
            return
        }

        if (exceptionCause.equals(ConstantUtil.ExceptionsLogs.RESPONSE_EXCEPTION_TITLE, ignoreCase = true)) {
            listener.onPaymentException(
                ConstantUtil.ExceptionsLogs.RESPONSE_EXCEPTION_TITLE,
                ConstantUtil.ExceptionsLogs.PAYMENT_VALIDATION_FAILED
            )
            return
        }

        listener.onPaymentException(exceptionCause, exceptionMessage)
    }

    private fun checkIfDummy(response: String, listener: PaymentResponseListener): PaymentReturnedData? {
        return if (!response.equals(ConstantUtil.Tokens.DUMMY, ignoreCase = true) &&
            !response.equals(ConstantUtil.ReturnedJson.STATUS_CANCELLED, ignoreCase = true)
        ) {
            decodeJSON(response, listener)
        } else {
            null
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun decodeJSON(response: String, listener: PaymentResponseListener): PaymentReturnedData? {
        return try {
            Gson().fromJson(response, PaymentReturnedData::class.java)
        } catch (_: JsonSyntaxException) {
            setDecodeJsonError(listener)
            null
        }
    }

    private fun setRequestData(paymentRequest: ATHMPayment?, result: PaymentReturnedData?): PaymentReturnedData {
        val safeResult = result ?: PaymentReturnedData()
        safeResult.sdkVersion = BuildConfig.SDK_VERSION
        val safeRequest = paymentRequest ?: return safeResult

        safeResult.total    = safeRequest.total
        safeResult.subtotal = safeRequest.subtotal
        safeResult.tax      = safeRequest.tax
        safeResult.metadata1 = safeRequest.metadata1?.takeIf { it.isNotEmpty() } ?: ""
        safeResult.metadata2 = safeRequest.metadata2?.takeIf { it.isNotEmpty() } ?: ""
        safeResult.ecommerceId = safeRequest.ecommerceId?.takeIf { it.isNotBlank() }
        safeResult.items = safeRequest.items
        return safeResult
    }

    private fun attachEcommerceId(
        result: PaymentReturnedData,
        paymentRequest: ATHMPayment?,
        responseService: AuthorizationResponse?
    ) {
        if (!result.ecommerceId.isNullOrBlank()) return
        result.ecommerceId = when {
            !responseService?.data?.ecommerceId.isNullOrBlank() -> responseService.data?.ecommerceId
            !paymentRequest?.ecommerceId.isNullOrBlank() -> paymentRequest.ecommerceId
            else -> null
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun validatePaymentResponse(result: PaymentReturnedData?, listener: PaymentResponseListener, responseService: AuthorizationResponse?) {
        val paymentRequest = PaymentResultFlag.applicationInstance.getPaymentRequest()
        PaymentResultFlag.applicationInstance.clearPaymentRequest()

        val safeResult = if (result == null || result.total == 0.0) {
            setRequestData(paymentRequest, result)
        } else {
            result
        }

        safeResult.sdkVersion = BuildConfig.SDK_VERSION
        safeResult.fromFindPayment = false

        val status = getStatus(safeResult, responseService)
        if (responseService != null) {
            updateResultFromService(safeResult, responseService, status, false)
        }

        attachEcommerceId(safeResult, paymentRequest, responseService)

        notifyListenerByStatus(status, safeResult, listener)
    }

    internal fun validatePaymentResponse(listener: PaymentResponseListener, responseService: AuthorizationResponse?) {
        validateServiceResponse(listener, responseService, fromFindPayment = false)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun validateFindPaymentResponse(listener: PaymentResponseListener, responseService: AuthorizationResponse?) {
        validateServiceResponse(listener, responseService, fromFindPayment = true)
    }

    private fun validateServiceResponse(
        listener: PaymentResponseListener,
        responseService: AuthorizationResponse?,
        fromFindPayment: Boolean
    ) {
        val paymentRequest = PaymentResultFlag.applicationInstance.getPaymentRequest()
        PaymentResultFlag.applicationInstance.clearPaymentRequest()
        val result = PaymentReturnedData()
        result.sdkVersion = BuildConfig.SDK_VERSION
        result.fromFindPayment = fromFindPayment

        val status = getStatus(null, responseService)
        if (responseService != null) {
            updateResultFromService(result, responseService, status, true)
        }

        attachEcommerceId(result, paymentRequest, responseService)

        notifyListenerByStatus(status, result, listener)
    }

    private fun getStatus(result: PaymentReturnedData?, responseService: AuthorizationResponse?): String {
        if (responseService != null) {
            return if (responseService.status.equals(ConstantUtil.ResponseStatus.SERVICE_ERROR, ignoreCase = true)) {
                handleErrorStatus(responseService)
            } else {
                handleSuccessStatus(responseService)
            }
        }

        return result?.status
            ?.replace(ConstantUtil.ResponseStatus.PAYMENT_WORD, "")
            ?: ConstantUtil.ResponseStatus.PAYMENT_NOT_FOUND
    }

    private fun handleSuccessStatus(responseService: AuthorizationResponse): String {
        val data = responseService.data
        return when (data?.ecommerceStatus?.uppercase()) {
            ConstantUtil.ResponseStatus.COMPLETED -> {
                sendEventToNewRelic(
                    ConstantUtil.NW_RESPONSE_SUCCESS_PAYMENT,
                    data.ecommerceId,
                    ConstantUtil.ResponseStatus.COMPLETED,
                    PaymentResultFlag.applicationInstance.getEcommerceAppName(),
                    ConstantUtil.BUILD_TYPE
                )
                ConstantUtil.ResponseStatus.COMPLETED
            }
            ConstantUtil.ResponseStatus.AUTHORIZED -> ConstantUtil.ResponseStatus.COMPLETED
            ConstantUtil.ResponseStatus.EXPIRED -> ConstantUtil.ResponseStatus.EXPIRED
            ConstantUtil.ResponseStatus.CANCEL -> ConstantUtil.ResponseStatus.CANCELLED
            ConstantUtil.ResponseStatus.CANCELLED -> ConstantUtil.ResponseStatus.CANCELLED
            ConstantUtil.ResponseStatus.EXCEPTION -> ConstantUtil.ResponseStatus.FAILED
            else -> ConstantUtil.ResponseStatus.FAILED
        }
    }

    private fun handleErrorStatus(responseService: AuthorizationResponse): String {
        val schemeForNR = PaymentResultFlag.applicationInstance.getEcommerceAppName() ?: "N/A"
        sendEventToNewRelic(
            ConstantUtil.NW_RESPONSE_FAILED_PAYMENT,
            responseService.data?.ecommerceId ?: "N/A",
            responseService.status,
            schemeForNR,
            ConstantUtil.BUILD_TYPE
        )
        return ConstantUtil.ResponseStatus.FAILED
    }

    private fun updateResultFromService(
        result: PaymentReturnedData,
        responseService: AuthorizationResponse,
        status: String,
        withOutResult: Boolean
    ) {
        val data = responseService.data ?: return

        // Campos de contacto siempre disponibles si el servicio los devuelve
        if (!data.name.isNullOrBlank()) result.name = data.name
        if (!data.phoneNumber.isNullOrBlank()) result.phoneNumber = data.phoneNumber
        if (!data.email.isNullOrBlank()) result.email = data.email

        if (status != ConstantUtil.ResponseStatus.COMPLETED) return

        result.referenceNumber = data.referenceNumber.orEmpty()
        result.dailyTransactionID = data.dailyTransactionID.orEmpty()
        result.netAmount = data.netAmount ?: 0.0
        result.fee = data.fee ?: 0.0
        result.metadata1 = data.metadata1.orEmpty()
        result.metadata2 = data.metadata2.orEmpty()
        if (!data.ecommerceId.isNullOrBlank()) {
            result.ecommerceId = data.ecommerceId
        }

        if (withOutResult) {
            result.total = data.total ?: 0.0
            result.subtotal = data.subtotal ?: 0.0
            result.tax = data.tax ?: 0.0
            if (data.items is ArrayList<*>) {
                @Suppress("UNCHECKED_CAST")
                result.items = data.items as ArrayList<Items>
            }
        }
    }


    private fun notifyListenerByStatus(status: String, result: PaymentReturnedData, listener: PaymentResponseListener) {
        val date = getDateFormat(result.date)
        when (status) {
            ConstantUtil.ResponseStatus.COMPLETED -> listener.onCompletedPayment(date, result)
            ConstantUtil.ResponseStatus.EXPIRED -> {
                listener.onExpiredPayment(date, result)
                sendEventToNewRelic(
                    ConstantUtil.NW_RESPONSE_FAILED_PAYMENT,
                    ConstantUtil.NW_RESPONSE_EXPIRED_PAYMENT,
                    status,
                    PaymentResultFlag.applicationInstance.getEcommerceAppName(),
                    ConstantUtil.BUILD_TYPE
                )
            }
            ConstantUtil.ResponseStatus.CANCELLED -> {
                listener.onCancelledPayment(date, result)
                sendEventToNewRelic(
                    ConstantUtil.NW_RESPONSE_FAILED_PAYMENT,
                    ConstantUtil.NW_RESPONSE_CANCELLED_PAYMENT,
                    status,
                    PaymentResultFlag.applicationInstance.getEcommerceAppName(),
                    ConstantUtil.BUILD_TYPE
                )
            }
            else -> listener.onFailedPayment(date, result)
        }
    }
}



