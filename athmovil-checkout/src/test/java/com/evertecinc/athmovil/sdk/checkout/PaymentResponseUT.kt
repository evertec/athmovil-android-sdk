package com.evertecinc.athmovil.sdk.checkout

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData
import com.evertecinc.athmovil.sdk.checkout.objects.payment.AuthorizationResponse
import com.evertecinc.athmovil.sdk.checkout.objects.payment.AuthorizationObject
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil
import com.evertecinc.athmovil.sdk.checkout.utils.Util
import com.google.gson.Gson
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.only
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class PaymentResponseUT {

    @Mock
    private lateinit var listener: PaymentResponseListener
    
    private val gson = Gson()
    private lateinit var closeable: AutoCloseable

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun releaseMocks() {
        PaymentResponse.resetFindPaymentFallbackForTesting()
        closeable.close()
    }

    @Test
    fun whenValidatingPaymentResponse_GivenCancelledPaymentResponse_ThenReturnOnCancelledPaymentData() {
        val result = gson.fromJson(setCancelledPaymentResponse(), PaymentReturnedData::class.java)
        PaymentResponse.validatePaymentResponse(result, listener, null)
        verify(listener, only()).onCancelledPayment(Util.getDateFormat(result.date), result)
    }

    @Test
    fun whenValidatingPaymentResponse_GivenExpiredPaymentResponse_ThenReturnOnExpiredPaymentData() {
        val result = gson.fromJson(setExpiredPaymentResponse(), PaymentReturnedData::class.java)
        PaymentResponse.validatePaymentResponse(result, listener, null)
        verify(listener, only()).onExpiredPayment(Util.getDateFormat(result.date), result)
    }

    @Test
    fun whenValidatingPaymentResponse_GivenCompletedPaymentResponse_ThenReturnOnCompletedPaymentData() {
        val result = gson.fromJson(setCompletedPaymentResponse(), PaymentReturnedData::class.java)
        PaymentResponse.validatePaymentResponse(result, listener, null)
        verify(listener, only()).onCompletedPayment(Util.getDateFormat(result.date), result)
    }

    @Test
    fun whenValidatingPaymentResponse_GivenBadPaymentResponse_ThenReturnOnErrorData() {
        PaymentResponse.decodeJSON(setExceptionError(), listener)
        verify(listener, only()).onPaymentException(
            ConstantUtil.ExceptionsLogs.RESPONSE_EXCEPTION_TITLE,
            ConstantUtil.ExceptionsLogs.DECODE_JSON_LOG_MESSAGE
        )
    }

    @Test
    fun whenValidatingServiceResponse_GivenCompletedAuthorization_ThenReturnOnCompletedPaymentData() {
        val response = AuthorizationResponse()
        response.status = "success"

        val data = AuthorizationObject()
        data.ecommerceStatus = "COMPLETED"
        data.referenceNumber = "ref-123"
        response.data = data

        PaymentResponse.validatePaymentResponse(listener, response)
        val resultCaptor = argumentCaptor<PaymentReturnedData>()
        verify(listener, only()).onCompletedPayment(any(), resultCaptor.capture())
        assertEquals(BuildConfig.SDK_VERSION, resultCaptor.firstValue.sdkVersion)
        assertFalse(resultCaptor.firstValue.fromFindPayment)
    }

    @Test
    fun whenValidatingServiceResponse_GivenErrorAuthorization_ThenReturnOnFailedPaymentData() {
        val response = AuthorizationResponse()
        response.status = "error"

        PaymentResponse.validatePaymentResponse(listener, response)
        verify(listener, only()).onFailedPayment(any(), any())
    }

    @Test
    fun whenValidatingServiceResponse_GivenNullServiceResponse_ThenReturnOnFailedPaymentData() {
        PaymentResponse.validatePaymentResponse(listener, null)
        val resultCaptor = argumentCaptor<PaymentReturnedData>()
        verify(listener, only()).onFailedPayment(any(), resultCaptor.capture())
        assertEquals(BuildConfig.SDK_VERSION, resultCaptor.firstValue.sdkVersion)
        assertFalse(resultCaptor.firstValue.fromFindPayment)
    }

    @Test
    fun whenValidatingFindPaymentResponse_GivenCompletedAuthorization_ThenMarkResultFromFindPayment() {
        val response = AuthorizationResponse()
        response.status = "success"

        val data = AuthorizationObject()
        data.ecommerceStatus = "COMPLETED"
        data.referenceNumber = "ref-123"
        response.data = data

        PaymentResponse.validateFindPaymentResponse(listener, response)
        val resultCaptor = argumentCaptor<PaymentReturnedData>()
        verify(listener, only()).onCompletedPayment(any(), resultCaptor.capture())
        assertTrue(resultCaptor.firstValue.fromFindPayment)
    }

    @Test
    fun hasValidPaymentResult_givenBlankValue_returnsFalse() {
        val intent = mock<Intent>()
        val extras = mock<Bundle>()
        whenever(intent.extras).thenReturn(extras)
        whenever(extras.getString(ConstantUtil.ReturnedJson.RETURNED_JSON_KEY)).thenReturn("   ")

        assertFalse(PaymentResponse.hasValidPaymentResult(intent))
    }

    @Test
    fun hasValidPaymentResult_givenNonBlankValue_returnsTrue() {
        val intent = mock<Intent>()
        val extras = mock<Bundle>()
        whenever(intent.extras).thenReturn(extras)
        whenever(extras.getString(ConstantUtil.ReturnedJson.RETURNED_JSON_KEY)).thenReturn("{\"status\":\"CompletedPayment\"}")

        assertTrue(PaymentResponse.hasValidPaymentResult(intent))
    }

    @Test
    fun validatePaymentResponse_givenNoIntent_callsFindPaymentHandler() {
        val context = mockContextWithPublicToken("real-token")
        var findPaymentCalled = false

        PaymentResponse.findPaymentHandler = { _, _ -> findPaymentCalled = true }

        PaymentResponse.validatePaymentResponse(context, listener, null)

        assertTrue(findPaymentCalled)
        verifyNoInteractions(listener)
    }

    @Test
    fun validatePaymentResponse_givenValidIntent_callsFindPayment() {
        val context = mockContextWithPublicToken("real-token")
        val extras = mock<Bundle>()
        val intent = mock<Intent>()
        whenever(intent.extras).thenReturn(extras)
        whenever(extras.getString(ConstantUtil.ReturnedJson.RETURNED_JSON_KEY))
            .thenReturn("{\"status\":\"ExpiredPayment\"}")

        var findPaymentCalled = false
        PaymentResponse.findPaymentHandler = { _, _ -> findPaymentCalled = true }

        PaymentResponse.validatePaymentResponse(context, listener, intent)

        // Con token real (no dummy), debería consumir findPayment
        assertTrue(findPaymentCalled)
        verifyNoInteractions(listener)
    }

    @Test
    fun validatePaymentResponse_givenValidIntentWithDummyToken_processesIntent() {
        val context = mockContextWithPublicToken(ConstantUtil.Tokens.DUMMY)
        val extras = mock<Bundle>()
        val intent = mock<Intent>()
        whenever(intent.extras).thenReturn(extras)
        whenever(extras.getString(ConstantUtil.ReturnedJson.RETURNED_JSON_KEY))
            .thenReturn("{\"status\":\"ExpiredPayment\"}")

        PaymentResponse.validatePaymentResponse(context, listener, intent)

        // Con token dummy, debería procesar el intent
        verify(listener).onExpiredPayment(any(), any())
    }

    @Test
    fun createFindPaymentCallback_givenResponseCONFIRM_callsAuthorizationHandler() {
        val context = mock<Context>()
        var authorizationHandlerCalled = false
        val authorizationHandler: (Context, PaymentResponseListener) -> Unit = { _, _ ->
            authorizationHandlerCalled = true
        }

        val response = AuthorizationResponse()
        response.status = "success"
        val data = AuthorizationObject()
        data.ecommerceStatus = "CONFIRM"
        response.data = data

        val callback = PaymentResponse.createFindPaymentCallback(context, listener, authorizationHandler)
        callback.onSuccess(response)

        assertTrue(authorizationHandlerCalled)
        verifyNoInteractions(listener)
    }

    @Test
    fun createFindPaymentCallback_givenResponseAUTHORIZED_processesCallback() {
        val context = mock<Context>()
        val authorizationHandler: (Context, PaymentResponseListener) -> Unit = { _, _ -> }

        val response = AuthorizationResponse()
        response.status = "success"
        val data = AuthorizationObject()
        data.ecommerceStatus = "Authorized"
        response.data = data

        val callback = PaymentResponse.createFindPaymentCallback(context, listener, authorizationHandler)
        callback.onSuccess(response)

        val resultCaptor = argumentCaptor<PaymentReturnedData>()
        verify(listener).onCompletedPayment(any(), resultCaptor.capture())
        assertTrue(resultCaptor.firstValue.fromFindPayment)
    }

    @Test
    fun createFindPaymentCallback_givenResponseCANCEL_processesAsCancel() {
        val context = mock<Context>()
        val authorizationHandler: (Context, PaymentResponseListener) -> Unit = { _, _ -> }

        val response = AuthorizationResponse()
        response.status = "success"
        val data = AuthorizationObject()
        data.ecommerceStatus = "CANCEL"
        response.data = data

        val callback = PaymentResponse.createFindPaymentCallback(context, listener, authorizationHandler)
        callback.onSuccess(response)

        verify(listener).onCancelledPayment(any(), any())
    }

    @Test
    fun createFindPaymentCallback_givenResponseEXPIRED_processesAsExpired() {
        val context = mock<Context>()
        val authorizationHandler: (Context, PaymentResponseListener) -> Unit = { _, _ -> }

        val response = AuthorizationResponse()
        response.status = "success"
        val data = AuthorizationObject()
        data.ecommerceStatus = "EXPIRED"
        response.data = data

        val callback = PaymentResponse.createFindPaymentCallback(context, listener, authorizationHandler)
        callback.onSuccess(response)

        verify(listener).onExpiredPayment(any(), any())
    }

    @Test
    fun createFindPaymentCallback_givenHttpError_processesAsError() {
        val context = mock<Context>()
        val authorizationHandler: (Context, PaymentResponseListener) -> Unit = { _, _ -> }

        val callback = PaymentResponse.createFindPaymentCallback(context, listener, authorizationHandler)
        callback.onHttpError("Error body")

        verify(listener).onFailedPayment(any(), any())
    }

    @Test
    fun createFindPaymentCallback_givenFailure_processesAsError() {
        val context = mock<Context>()
        val authorizationHandler: (Context, PaymentResponseListener) -> Unit = { _, _ -> }

        val callback = PaymentResponse.createFindPaymentCallback(context, listener, authorizationHandler)
        callback.onFailure(Exception("Network error"))

        verify(listener).onFailedPayment(any(), any())
    }

    private fun mockContextWithPublicToken(publicToken: String): Context {
        val context = mock<Context>()
        val prefs = mock<SharedPreferences>()
        whenever(context.getSharedPreferences(eq(ConstantUtil.BasicData.CHECKOUT_PREFS_KEY), eq(Context.MODE_PRIVATE)))
            .thenReturn(prefs)
        whenever(prefs.getString(eq(ConstantUtil.BasicData.PUBLIC_TOK), eq(null))).thenReturn(publicToken)
        return context
    }

    private fun setCancelledPaymentResponse(): String {
        return "{\"status\":\"CancelledPayment\",\"total\":1.12,\"tax\":0,\"subtotal\":0," +
                "\"name\":\"Test\",\"phoneNumber\":7871234567,\"email\":\"test@test.com\"," +
                "\"date\":\"Wed Mar 14 15:30:00 EET 2018\",\"dailyTransactionID\":0000," +
                "\"metadata1\":\"Milk\",\"metadata2\":\"Shake 2\",\"items\":[]}"
    }

    private fun setExpiredPaymentResponse(): String {
        return "{\"status\":\"ExpiredPayment\",\"total\":1.12,\"tax\":0,\"subtotal\":0," +
                "\"name\":\"Test\",\"phoneNumber\":7871234567,\"email\":\"test@test.com\"," +
                "\"date\":\"Wed Mar 14 15:30:00 EET 2018\",\"dailyTransactionID\":0000," +
                "\"metadata1\":\"Milk\",\"metadata2\":\"Shake 2\",\"items\":[]}"
    }

    private fun setCompletedPaymentResponse(): String {
        return "{\"status\":\"CompletedPayment\",\"total\":1.12,\"tax\":0,\"subtotal\":0," +
                "\"name\":\"Test\",\"phoneNumber\":7871234567,\"email\":\"test@test.com\"," +
                "\"date\":\"Wed Mar 14 15:30:00 EET 2018\",\"dailyTransactionID\":0000," +
                "\"metadata1\":\"Milk\",\"metadata2\":\"Shake 2\",\"items\":[]}"
    }

    private fun setExceptionError(): String {
        return "{[]}"
    }
}
