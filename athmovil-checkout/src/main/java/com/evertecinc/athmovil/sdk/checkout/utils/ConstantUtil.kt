package com.evertecinc.athmovil.sdk.checkout.utils

/**
 * Kotlin migration of the SDK constants holder.
 */
object ConstantUtil {

    object Tokens {
        const val DUMMY = "dummy"
        const val SUCCESS = "success"
        const val FAILURE = "failure"
    }

    object ResponseStatus {
        const val COMPLETED_PAYMENT = "CompletedPayment"
        const val CANCELLED_PAYMENT = "CancelledPayment"
        const val COMPLETED = "COMPLETED"
        const val CANCELLED = "CANCELLED"
        const val CANCEL = "CANCEL"
        const val FAILED = "FAILED"
        const val EXPIRED = "EXPIRED"
        const val ERROR = "Error"
        const val SERVICE_ERROR = "error"
        const val PAYMENT_WORD = "PAYMENT"
        const val PAYMENT_NOT_FOUND = "PAYMENT NOT FOUND"
        const val AUTHORIZED = "AUTHORIZED"
        const val EXCEPTION = "EXCEPTION"
    }

    object JsonKeys {
        const val ITEMS_SELECTED_LIST = "itemsSelectedList"
    }

    object Validation {
        const val CALLBACK_SCHEMA_REGEX = "^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z0-9_]+)*$"
    }

    object BasicData {
        const val COM_EVERTEC_ATHMOVIL_ANDROID = "com.evertec.athmovil.android"
        const val ATH_MOVIL_REQUIRED_VERSION_CODE = 185
        const val ATH_MOVIL_MARKET_URL = "market://details?id=com.evertec.athmovil.android"
        const val BUNDLE = "bundleID"
        const val JSON_DATA_KEY = "jsonData"
        const val REFERENCE_NUMBER_KEY = "referenceNumber"
        const val PAYMENT_DURATION_TIME_KEY = "purchaseTimeOut"
        const val API_ROUTE_PAYMENT_SERVICE = "api/business-transaction/ecommerce/payment"
        const val API_ROUTE_AUTORIZATION_SERVICE = "api/business-transaction/ecommerce/authorization"
        const val API_ROUTE_FINDPAYMENT_SERVICE = "api/business-transaction/ecommerce/business/findPayment"
        const val MAX_TIMEOUT_SECONDS = 600L
        const val MIN_TIMEOUT_SECONDS = 60L

        const val CHECKOUT_PREFS_KEY = "ATHCheckoutPreferences"
        const val TOKEN_AUTH_KEY = "TokenAuth"
        const val ECOMMERCE_I_D_KEY = "EcommerceId"
        const val PUBLIC_TOK = "publicToken"
        const val BUILD_TYPE_KEY = "buildType"
    }

    object PaymentObject {
        const val PAYMENT_JSON_PUBLIC_TOKEN_KEY = "businessToken"
        const val PAYMENT_JSON_SUBTOTAL__KEY = "subtotal"
        const val PAYMENT_JSON_TAX_KEY = "tax"
        const val PAYMENT_JSON_TOTAL_KEY = "total"
        const val PAYMENT_JSON_SCHEMA_KEY = "callbackSchema"
        const val PAYMENT_JSON_METADATA_1 = "metadata1"
        const val PAYMENT_JSON_METADATA_2 = "metadata2"
        const val PAYMENT_JSON_PAYMENT_I_D_KEY = "paymentId"
        const val PAYMENT_JSON_ITEM_NAME_KEY = "name"
        const val PAYMENT_JSON_ECOMMERCE_I_D = "ecommerceId"
        const val PAYMENT_JSON_PHONE = "phoneLine"
        const val PAYMENT_JSON_ITEM_PRICE_KEY = "price"
        const val PAYMENT_JSON_ITEM_QUANTITY_KEY = "quantity"
        const val PAYMENT_JASON_ITEM_METADATA_KEY = "metadata"
        const val PAYMENT_JSON_ITEM_LIST_KEY = "items"
    }

    object DummyData {
        const val DAILY_TRANSACTION_ID = "0004"
        const val NAME = "None"
        const val PHONE_NUMBER = "8888888888"
        const val EMAIL = "test@test.com"
    }

    object ReturnedJson {
        const val RETURNED_JSON_KEY = "paymentResult"
        const val RETURNED_JSON_STATUS_KEY = "status"
        const val RETURNED_JSON_TOTAL_KEY = "total"
        const val RETURNED_JSON_SUBTOTAL_KEY = "subtotal"
        const val RETURNED_JSON_TAX_KEY = "tax"
        const val RETURNED_JSON_METADATA1_KEY = "metadata1"
        const val RETURNED_JSON_METADATA2_KEY = "metadata2"
        const val RETURNED_JSON_PAYMENT_I_D_KEY = "paymentId"
        const val RETURNED_JSON_SDK_VERSION_KEY = "sdkVersion"
        const val RETURNED_JSON_ITEM_DESCRIPTION_KEY = "description"

        const val TOKEN_FOR_SUCCESS = Tokens.SUCCESS
        const val TOKEN_FOR_FAILURE = Tokens.FAILURE
        const val STATUS_SUCCESS = ResponseStatus.COMPLETED_PAYMENT
        const val STATUS_CANCELLED = ResponseStatus.CANCELLED_PAYMENT
        const val REFERENCE_NUMBER = "212786207-2d30019"
    }

    const val TOKEN_FOR_SUCCESS = ReturnedJson.TOKEN_FOR_SUCCESS
    const val TOKEN_FOR_FAILURE = ReturnedJson.TOKEN_FOR_FAILURE
    const val STATUS_SUCCESS = ReturnedJson.STATUS_SUCCESS
    const val STATUS_CANCELLED = ReturnedJson.STATUS_CANCELLED

    const val RVARIBALES = "[!@$#]"
    const val REFERENCE_NUMBER = ReturnedJson.REFERENCE_NUMBER

    object ExceptionsLogs {
        const val LOG_TAG = "athmCheckoutValidation"
        const val NULL_ATHMPAYMENT_LOG_MESSAGE = "ATHMPayment is null."
        const val NULL_CONTEXT_LOG_MESSAGE = "Context is null."
        const val PAYMENT_VALIDATION_FAILED = "Error getting response from webservice"
        const val NULL_PUBLICTOKEN_LOG_MESSAGE = "BusinessToken is null or empty."
        const val TOTAL_ERROR_LOG_MESSAGE = "Total data type value is invalid."
        const val SUBTOTAL_ERROR_LOG_MESSAGE = "Subtotal data type value is invalid."
        const val ITEM_TOTAL_ERROR_LOG_MESSAGE = "Item's price data type value is invalid."
        const val ITEM_QUANTITY_ERROR_LOG_MESSAGE = "Item's quantity data type value is invalid."
        const val ITEM_NAME_ERROR_LOG_MESSAGE = "Item's name value is invalid."
        const val ENCODE_JSON_LOG_MESSAGE = "An error occurred while encoding JSON."
        const val DECODE_JSON_LOG_MESSAGE = "An error occurred while decoding JSON."

        const val NR_VARIABLE = "c@0d!4fa@39!f\$fb@68c8\$\$8!9331a@@0!15#ba!ed0d5bF@FF#!FNR\$AL"
        const val NR_URL = "\$h!!t\$t@@p##s:/@!/!i@ns\$@!ig\$h#t!s\$-c!o!l@l#e@@ctor!.\$n\$e@w##@re#@lic.com/v\$1/!a\$@c\$c#o#@un\$ts/@3!!41\$085!4/@e!\$\$v\$e@n\$ts"
        const val ITEM_DESC_ERROR_LOG_MESSAGE = "Item's description value is invalid."
        const val NULL_METADATA_LOG_MESSAGE = "The metadata data type value is invalid."
        const val NULL_ITEM_METADATA_LOG_MESSAGE = "Item's metadata value is invalid."
        const val SCHEMA_ERROR_MESSAGE = "Url scheme value is invalid."
        const val RESPONSE_EXCEPTION_TITLE = "Error in response"
        const val REQUEST_EXCEPTION_TITLE = "Error in request"
        const val RESPONSE_NULL_EXCEPTION = "Empty response."
        const val TAX_NULL_LOG_MESSAGE = "Tax data type value is invalid."
        const val DATE_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy"
        const val EXCEPTION = "exception"
        const val EXCEPTION_CAUSE = "exceptionCause"
    }

    const val AWS_URL_PAYMENT_PRO = "payments.athmovil.com"

    const val NW_INIT_PAYMENT_SUCCESS = "ATHMSuccessPaymentInitEvent"
    const val NW_INIT_PAYMENT_FAILED = "ATHMFailedPaymentInitEvent"
    const val NW_RESPONSE_SUCCESS_PAYMENT = "ATHMSuccessPaymentEvent"
    const val NW_RESPONSE_FAILED_PAYMENT = "ATHMFailedPaymentEvent"
    const val NW_RESPONSE_EXPIRED_PAYMENT = "EXPIRED"
    const val NW_RESPONSE_CANCELLED_PAYMENT = "CANCELLED"

    const val SDK_VERSION = "7.0.0"
    const val SDK_PLATFORM = "Android_Native"
    const val BUILD_TYPE = "QA"
    const val ATHM_APP_NOT_FOUND = "Unknown Application"
    const val ECOMMERCE_APP_NAME = "DEFAULT_ECOMMERCE_APP"
}

