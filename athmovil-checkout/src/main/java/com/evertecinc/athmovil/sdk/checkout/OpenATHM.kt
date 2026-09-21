package com.evertecinc.athmovil.sdk.checkout

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.TypedValue
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.evertecinc.athmovil.sdk.checkout.exceptions.InvalidPaymentRequestException
import com.evertecinc.athmovil.sdk.checkout.exceptions.JsonEncoderException
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullATHMPaymentObjectException
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullApplicationContextException
import com.evertecinc.athmovil.sdk.checkout.interfaces.AuthorizationRequestCallback
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentRequestCallback
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener
import com.evertecinc.athmovil.sdk.checkout.interfaces.PostServiceCoroutine
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment
import com.evertecinc.athmovil.sdk.checkout.objects.Items
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentResultFlag
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData
import com.evertecinc.athmovil.sdk.checkout.objects.payment.AuthorizationResponse
import com.evertecinc.athmovil.sdk.checkout.objects.payment.FindPaymentRequest
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentErrorResponse
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentRequest
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentResponseObject
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil.BasicData.COM_EVERTEC_ATHMOVIL_ANDROID
import com.evertecinc.athmovil.sdk.checkout.utils.ExceptionUtil
import com.evertecinc.athmovil.sdk.checkout.utils.GetApplicationNameUtil
import com.evertecinc.athmovil.sdk.checkout.utils.JsonUtil
import com.evertecinc.athmovil.sdk.checkout.utils.NewRelicConfig.sendEventToNewRelic
import com.evertecinc.athmovil.sdk.checkout.utils.PaymentCoroutineRunner
import com.evertecinc.athmovil.sdk.checkout.utils.Util
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri

/**
 * Main class for the athmovil-checkout library.
 */
object OpenATHM {

    private val loadingLock = Any()
    @Volatile
    private var loadingDialog: AlertDialog? = null
    private val callbackSchemaRegex = Regex(ConstantUtil.Validation.CALLBACK_SCHEMA_REGEX)

    @JvmStatic
    fun validateData(athmPayment: ATHMPayment, context: Context) {
        Util.setPrefsString(ConstantUtil.BasicData.PUBLIC_TOK, athmPayment.publicToken ?: "", context)
        if (athmPayment.publicToken.equals(ConstantUtil.Tokens.DUMMY, ignoreCase = true)) {
            sendData(athmPayment, context)
        } else {
            paymentServices(athmPayment, context)
        }
    }

    private fun sendData(athmPayment: ATHMPayment, context: Context) {
        val appName = resolveEcommerceAppName(context)
        PaymentResultFlag.applicationInstance.setPaymentRequest(athmPayment)
        PaymentResultFlag.applicationInstance.setEcommerceAppName(appName)
        try {
            validateATHMPayment(athmPayment)
            defineTimeout(athmPayment)
            defineResponse(athmPayment)
        } catch (e: Exception) {
            when (e) {
                is NullATHMPaymentObjectException,
                is NullApplicationContextException,
                is InvalidPaymentRequestException,
                is JsonEncoderException -> {
                    sendEventToNewRelic(
                        ConstantUtil.NW_INIT_PAYMENT_FAILED,
                        e.message,
                        ConstantUtil.TOKEN_FOR_FAILURE,
                        appName,
                        ConstantUtil.BUILD_TYPE
                    )
                    showResults(context, null, athmPayment.callbackSchema ?: "", e)
                }
                else -> throw e
            }
        }
    }

    @Throws(JsonEncoderException::class)
    private fun defineResponse(athmPayment: ATHMPayment) {
        when {
            athmPayment.publicToken.equals(ConstantUtil.ReturnedJson.TOKEN_FOR_SUCCESS, ignoreCase = true) -> {
                definePaymentReturnedData(
                    athmPayment,
                    ConstantUtil.ReturnedJson.STATUS_SUCCESS,
                    ConstantUtil.ReturnedJson.REFERENCE_NUMBER,
                    athmPayment.total,
                    athmPayment.tax,
                    athmPayment.subtotal,
                    athmPayment.metadata1 ?: "",
                    athmPayment.metadata2 ?: "",
                    athmPayment.paymentId ?: "",
                    athmPayment.items
                )
            }

            athmPayment.publicToken.equals(ConstantUtil.ReturnedJson.TOKEN_FOR_FAILURE, ignoreCase = true) -> {
                definePaymentReturnedData(
                    athmPayment,
                    ConstantUtil.ReturnedJson.STATUS_CANCELLED,
                    null,
                    athmPayment.total,
                    athmPayment.tax,
                    athmPayment.subtotal,
                    athmPayment.metadata1 ?: "",
                    athmPayment.metadata2 ?: "",
                    athmPayment.paymentId ?: "",
                    athmPayment.items
                )
            }

            else -> {
                val businessInfoJson = JsonUtil.toJson(athmPayment)
                if (businessInfoJson != null) {
                    logForDebug(businessInfoJson)
                    execute(athmPayment.context, businessInfoJson, athmPayment.timeout, athmPayment.buildType)
                } else {
                    logForDebug(ConstantUtil.ExceptionsLogs.ENCODE_JSON_LOG_MESSAGE)
                    throw JsonEncoderException(ConstantUtil.ExceptionsLogs.ENCODE_JSON_LOG_MESSAGE)
                }
            }
        }
    }

    private fun definePaymentReturnedData(
        athmPayment: ATHMPayment,
        status: String,
        referenceNumber: String?,
        total: Double,
        tax: Double,
        subtotal: Double,
        metadata1: String,
        metadata2: String,
        paymentId: String,
        items: ArrayList<Items>
    ) {
        val paymentReturnedData = PaymentReturnedData()
        paymentReturnedData.status = status.uppercase()
        paymentReturnedData.referenceNumber = referenceNumber
        paymentReturnedData.total = total
        paymentReturnedData.tax = tax
        paymentReturnedData.subtotal = subtotal
        paymentReturnedData.metadata1 = metadata1
        paymentReturnedData.metadata2 = metadata2
        paymentReturnedData.dailyTransactionID = ConstantUtil.DummyData.DAILY_TRANSACTION_ID
        paymentReturnedData.name = ConstantUtil.DummyData.NAME
        paymentReturnedData.phoneNumber = ConstantUtil.DummyData.PHONE_NUMBER
        paymentReturnedData.email = ConstantUtil.DummyData.EMAIL
        paymentReturnedData.fee = 0.0
        paymentReturnedData.netAmount = 0.0
        paymentReturnedData.date = Date().toString()
        paymentReturnedData.paymentId = paymentId
        paymentReturnedData.ecommerceId = athmPayment.ecommerceId
        paymentReturnedData.sdkVersion = BuildConfig.SDK_VERSION
        paymentReturnedData.items = items

        val jsonResponse = JsonUtil.returnedJson(paymentReturnedData)
        showResults(athmPayment.context, jsonResponse, athmPayment.callbackSchema ?: "", null)
    }

    private fun defineTimeout(athmPayment: ATHMPayment) {
        athmPayment.timeout = if (athmPayment.timeout <= 0) {
            ConstantUtil.BasicData.MAX_TIMEOUT_SECONDS
        } else {
            athmPayment.timeout.coerceIn(ConstantUtil.BasicData.MIN_TIMEOUT_SECONDS, ConstantUtil.BasicData.MAX_TIMEOUT_SECONDS)
        }
    }

    @Throws(NullATHMPaymentObjectException::class, NullApplicationContextException::class, InvalidPaymentRequestException::class)
    private fun validateATHMPayment(athmPayment: ATHMPayment?) {
        val exceptionUtil = ExceptionUtil()
        if (athmPayment == null) {
            logForDebug(ConstantUtil.ExceptionsLogs.NULL_ATHMPAYMENT_LOG_MESSAGE)
            throw NullATHMPaymentObjectException(ConstantUtil.ExceptionsLogs.NULL_ATHMPAYMENT_LOG_MESSAGE)
        }
        if (!exceptionUtil.validateRequest(athmPayment)) {
            logForDebug(exceptionUtil.exceptionMessage)
            throw InvalidPaymentRequestException(exceptionUtil.exceptionMessage ?: ConstantUtil.ExceptionsLogs.PAYMENT_VALIDATION_FAILED)
        }
    }

    private fun execute(context: Context, json: String, timeout: Long, paymentBuildType: String?) {
        val timeoutMillis = timeout * 1000
        var athmVersionCode = 0L

        val normalizedBuildType = ""
        val athmBundleId = COM_EVERTEC_ATHMOVIL_ANDROID + normalizedBuildType

        var intent = context.packageManager.getLaunchIntentForPackage(athmBundleId)
        try {
            val athmInfo = context.packageManager.getPackageInfo(athmBundleId, 0)
            athmVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                athmInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                athmInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            sendEventToNewRelic(
                ConstantUtil.NW_INIT_PAYMENT_FAILED,
                e.toString(),
                ConstantUtil.TOKEN_FOR_FAILURE,
                "N/A",
                ConstantUtil.BUILD_TYPE
            )
            logForDebug(e.message)
        }

        if (intent == null || athmVersionCode <= ConstantUtil.BasicData.ATH_MOVIL_REQUIRED_VERSION_CODE) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = ConstantUtil.BasicData.ATH_MOVIL_MARKET_URL.toUri()
        }

        intent.putExtra(ConstantUtil.BasicData.BUNDLE, context.packageName)
        intent.putExtra(ConstantUtil.BasicData.JSON_DATA_KEY, json)
        intent.putExtra(ConstantUtil.BasicData.PAYMENT_DURATION_TIME_KEY, timeoutMillis)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    private fun showResults(context: Context, json: String?, callbackSchema: String, exception: Exception?) {
        val appId = buildCallbackAction(context, callbackSchema)
        val intent = Intent(appId)

        if (exception?.message != null) {
            val cause = if (exception.message.equals(ConstantUtil.ExceptionsLogs.PAYMENT_VALIDATION_FAILED, ignoreCase = true)) {
                ConstantUtil.ExceptionsLogs.RESPONSE_EXCEPTION_TITLE
            } else {
                ConstantUtil.ExceptionsLogs.REQUEST_EXCEPTION_TITLE
            }
            intent.putExtra(ConstantUtil.ReturnedJson.RETURNED_JSON_KEY, ConstantUtil.ExceptionsLogs.EXCEPTION)
            intent.putExtra(ConstantUtil.ExceptionsLogs.EXCEPTION_CAUSE, cause)
            intent.putExtra(ConstantUtil.ExceptionsLogs.EXCEPTION, exception.message)
        } else {
            intent.putExtra(ConstantUtil.ReturnedJson.RETURNED_JSON_KEY, json)
        }

        context.startActivity(intent)
    }

    private fun logForDebug(message: String?) {
        if (BuildConfig.DEBUG) {
            Log.d(ConstantUtil.ExceptionsLogs.LOG_TAG, message ?: "")
        }
    }

    private fun paymentServices(payment: ATHMPayment, context: Context) {
        val appName = GetApplicationNameUtil.getApplicationName(context)
        PaymentResultFlag.applicationInstance.setEcommerceAppName(appName)
        Util.setPrefsString(ConstantUtil.ECOMMERCE_APP_NAME, appName, context)
        Util.setPrefsString(ConstantUtil.BasicData.BUILD_TYPE_KEY, payment.buildType.orEmpty(), context)

        val url = getUrlEnvironment(payment.buildType ?: "")
        val postServiceCoroutine = createCoroutineService(url)

        showLoading(context)

        val paymentRequest = setObjectPaymentRequest(payment)
        PaymentCoroutineRunner.requestPayment(postServiceCoroutine, url, paymentRequest, object : PaymentRequestCallback {
            override fun onSuccess(response: PaymentResponseObject) {
                hideLoading()
                processSuccessfulResponse(response, payment, context, appName)
            }

            override fun onHttpError(errorBody: String?) {
                hideLoading()
                processErrorResponse(errorBody, context, appName)
            }

            override fun onFailure(error: Throwable) {
                hideLoading()
                logForDebug(error.message)
                sendEventToNewRelic(
                    ConstantUtil.NW_INIT_PAYMENT_FAILED,
                    error.message,
                    ConstantUtil.TOKEN_FOR_FAILURE,
                    appName,
                    ConstantUtil.BUILD_TYPE
                )
                getAlert(
                    context,
                    context.getString(R.string.payment_error_alert_title),
                    context.getString(R.string.payment_error_alert_message)
                )
            }
        })
    }

    private fun processSuccessfulResponse(
        response: PaymentResponseObject?,
        payment: ATHMPayment,
        context: Context,
        appName: String
    ) {
        val data = response?.data
        if (data == null) {
            getAlert(
                context,
                context.getString(R.string.payment_error_alert_title),
                context.getString(R.string.payment_error_alert_message)
            )
            return
        }

        logForDebug(JsonUtil.toJsonAnyObject(response))
        val ecommerce = data.ecommerceId
        val authToken = data.auth_token

        if (!ecommerce.isNullOrBlank() && !authToken.isNullOrBlank()) {
            Util.setPrefsString(ConstantUtil.BasicData.TOKEN_AUTH_KEY, authToken, context)
            Util.setPrefsString(ConstantUtil.BasicData.ECOMMERCE_I_D_KEY, ecommerce, context)
            payment.ecommerceId = ecommerce
            sendData(payment, context)
            sendEventToNewRelic(
                ConstantUtil.NW_INIT_PAYMENT_SUCCESS,
                ecommerce,
                ConstantUtil.TOKEN_FOR_SUCCESS,
                appName,
                ConstantUtil.BUILD_TYPE
            )
        }
    }

    private fun processErrorResponse(errorBody: String?, context: Context, appName: String) {
        var mError: PaymentErrorResponse? = null
        try {
            if (!errorBody.isNullOrBlank()) {
                val gson = GsonBuilder().create()
                mError = gson.fromJson(errorBody, PaymentErrorResponse::class.java)
            }
        } catch (e: JsonSyntaxException) {
            logForDebug(e.message)
        }

        verifiedError(context, mError)
        sendEventToNewRelic(
            ConstantUtil.NW_INIT_PAYMENT_FAILED,
            mError?.message ?: ConstantUtil.ExceptionsLogs.PAYMENT_VALIDATION_FAILED,
            ConstantUtil.TOKEN_FOR_FAILURE,
            appName,
            ConstantUtil.BUILD_TYPE
        )
    }

    internal fun getUrlEnvironment(buildType: String): String {
        return ConstantUtil.AWS_URL_PAYMENT_PRO
    }

    private fun verifiedError(context: Context, mError: PaymentErrorResponse?) {
        logForDebug(mError?.message)
        val (titleRes, messageRes) = when {
            mError?.errorcode == "BCUS_0092" ->
                Pair(R.string.business_token_expired_title, R.string.business_token_expired_message)
            else ->
                Pair(R.string.payment_error_alert_title, R.string.payment_error_alert_message)
        }
        getAlert(context, context.getString(titleRes), context.getString(messageRes))
    }

    private fun retrofitInstance(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHttpClient())
            .build()
    }

    private fun createCoroutineService(url: String): PostServiceCoroutine {
        val retrofit = retrofitInstance("https://$url")
        return retrofit.create(PostServiceCoroutine::class.java)
    }

    private fun setObjectPaymentRequest(payment: ATHMPayment): PaymentRequest {
        val objRequest = PaymentRequest()
        objRequest.publicToken = OpenATHMCore.safeString(payment.publicToken)
        objRequest.env = OpenATHMCore.safeString(payment.buildType)
        objRequest.timeout = payment.timeout
        objRequest.total = OpenATHMCore.safeDouble(payment.total)
        objRequest.tax = OpenATHMCore.safeDouble(payment.tax)
        objRequest.subtotal = OpenATHMCore.safeDouble(payment.subtotal)
        objRequest.metadata1 = OpenATHMCore.safeString(payment.metadata1)
        objRequest.metadata2 = OpenATHMCore.safeString(payment.metadata2)
        objRequest.phoneNumber = OpenATHMCore.safeString(payment.phoneNumber)
        objRequest.ecommerceId = OpenATHMCore.safeString(payment.ecommerceId)
        objRequest.items = payment.items

        val requestJson = JsonUtil.toJsonAnyObject(objRequest)
        logForDebug(requestJson)
        return objRequest
    }

    internal fun findPaymentServices(interATH: PaymentResponseListener, context: Context) {
        showLoading(context)
        val payment = PaymentResultFlag.applicationInstance.getPaymentRequest()

        val ecommerceID = Util.getPrefsString(ConstantUtil.BasicData.ECOMMERCE_I_D_KEY, context)
        val publicToken = payment?.publicToken?.takeIf { it.isNotBlank() }
            ?: Util.getPrefsString(ConstantUtil.BasicData.PUBLIC_TOK, context).takeIf { it.isNotBlank() }

        if (!OpenATHMCore.hasFindPaymentData(ecommerceID) || publicToken.isNullOrBlank()) {
            hideLoadingAndSetDefaultError(interATH)
            return
        }

        // Keep dynamic host resolution even if callbacks run twice and payment request was cleared.
        val buildType = payment?.buildType
            ?: Util.getPrefsString(ConstantUtil.BasicData.BUILD_TYPE_KEY, context)
        val url = getUrlEnvironment(buildType)
        val postServiceCoroutine = createCoroutineService(url)
        val findPaymentObject = FindPaymentRequest(ecommerceID, publicToken)

        // Handler de authorization desde findPayment
        val authorizationFromFindPaymentHandler: (Context, PaymentResponseListener) -> Unit = { _, listener ->
            consumeAuthorizationFromFindPayment(listener, context)
        }

        val findPaymentCallback = PaymentResponse.createFindPaymentCallback(
            context,
            interATH,
            authorizationFromFindPaymentHandler
        )

        PaymentCoroutineRunner.requestFindPayment(
            postServiceCoroutine,
            url,
            findPaymentObject,
            object : AuthorizationRequestCallback {
                override fun onSuccess(response: AuthorizationResponse) {
                    val status = response.data?.ecommerceStatus?.uppercase()
                    // Keep the loader only when we need to chain authorization.
                    if (!status.equals("CONFIRM", ignoreCase = true)) {
                        hideLoading()
                    }
                    findPaymentCallback.onSuccess(response)
                }

                override fun onHttpError(errorBody: String?) {
                    hideLoading()
                    findPaymentCallback.onHttpError(errorBody)
                }

                override fun onFailure(error: Throwable) {
                    hideLoading()
                    findPaymentCallback.onFailure(error)
                }
            }
        )
    }

    private fun consumeAuthorizationFromFindPayment(interATH: PaymentResponseListener, context: Context) {
        val payment = PaymentResultFlag.applicationInstance.getPaymentRequest()
        if (payment == null) {
            hideLoadingAndSetDefaultError(interATH)
            return
        }

        val token = Util.getPrefsString(ConstantUtil.BasicData.TOKEN_AUTH_KEY, context)
        if (token.isBlank() || payment.publicToken.isNullOrBlank()) {
            hideLoadingAndSetDefaultError(interATH)
            return
        }

        val url = getUrlEnvironment(payment.buildType ?: "")
        val postServiceCoroutine = createCoroutineService(url)

        PaymentCoroutineRunner.requestAuthorization(
            postServiceCoroutine,
            url,
            OpenATHMCore.bearerToken(token),
            object : AuthorizationRequestCallback {
                override fun onSuccess(response: AuthorizationResponse) {
                    hideLoading()
                    logForDebug(JsonUtil.toJsonAnyObject(response))
                    PaymentResponse.validatePaymentResponse(interATH, response)
                }

                override fun onHttpError(errorBody: String?) {
                    hideLoading()
                    val authError = AuthorizationResponse()
                    authError.status = ConstantUtil.ResponseStatus.SERVICE_ERROR
                    PaymentResponse.validatePaymentResponse(interATH, authError)
                }

                override fun onFailure(error: Throwable) {
                    hideLoading()
                    logForDebug(error.message)
                    sendEventToNewRelic(
                        ConstantUtil.NW_RESPONSE_FAILED_PAYMENT,
                        error.message,
                        ConstantUtil.TOKEN_FOR_FAILURE,
                            resolveEcommerceAppName(context),
                        ConstantUtil.BUILD_TYPE
                    )
                    val authError = AuthorizationResponse()
                    authError.status = ConstantUtil.ResponseStatus.SERVICE_ERROR
                    PaymentResponse.validatePaymentResponse(interATH, authError)
                }
            }
        )
    }

    private fun setDefaultError(interATH: PaymentResponseListener) {
        PaymentResultFlag.applicationInstance.clearPaymentRequest()
        val aut = AuthorizationResponse()
        aut.status = ConstantUtil.ResponseStatus.ERROR
        PaymentResponse.validatePaymentResponse(interATH, aut)
    }

    private fun hideLoadingAndSetDefaultError(listener: PaymentResponseListener) {
        hideLoading()
        setDefaultError(listener)
    }

    private fun getAlert(context: Context, title: String, message: String) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(android.R.string.ok)) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun getHttpClient(): OkHttpClient {
        return try {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
            builder.addInterceptor(getInterceptorConfiguration())
            builder.build()
        } catch (error: Exception) {
            logForDebug(error.message)
            OkHttpClient.Builder().build()
        }
    }

    private fun getInterceptorConfiguration() = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private fun showLoading(context: Context) {
        synchronized(loadingLock) {
            hideLoading()
            val density = context.resources.displayMetrics.density
            val padding = (20 * density).toInt()
            val spacing = (12 * density).toInt()

            val container = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(padding, padding, padding, padding)
            }

            val progressBar = ProgressBar(context).apply {
                isIndeterminate = true
            }

            val loadingText = TextView(context).apply {
                text = context.getString(R.string.loading)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setPadding(spacing, 0, 0, 0)
            }

            container.addView(progressBar)
            container.addView(loadingText)

            val builder = AlertDialog.Builder(context)
            builder.setView(container)
            builder.setCancelable(false)
            loadingDialog = builder.create()
            loadingDialog?.show()
        }
    }

    private fun hideLoading() {
        synchronized(loadingLock) {
            if (loadingDialog?.isShowing == true) {
                loadingDialog?.dismiss()
            }
            loadingDialog = null
        }
    }

    private fun buildCallbackAction(context: Context, callbackSchema: String): String {
        val safeSchema = callbackSchema.trim()
        if (safeSchema.isEmpty()) {
            return context.packageName
        }

        return if (safeSchema.matches(callbackSchemaRegex)) {
            context.packageName + "." + safeSchema
        } else {
            logForDebug(ConstantUtil.ExceptionsLogs.SCHEMA_ERROR_MESSAGE)
            context.packageName
        }
    }

    private fun resolveEcommerceAppName(context: Context): String {
        val storedName = Util.getPrefsString(ConstantUtil.ECOMMERCE_APP_NAME, context)
        if (storedName.isNotBlank()) {
            return storedName
        }

        return PaymentResultFlag.applicationInstance.getEcommerceAppName()
            ?.takeIf { it.isNotBlank() }
            ?: GetApplicationNameUtil.getApplicationName(context)
    }
}


