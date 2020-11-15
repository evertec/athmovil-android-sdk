package com.evertecinc.athmovil.sdk.checkout.utils;
/**
 * Created by Juan Gabriel Zaragoza Bonilla on 4/11/2018.
 */

public class ConstantUtil {
    public final static String COM_EVERTEC_ATHMOVIL_ANDROID = "com.evertec.athmovil.android";
    public final static int ATH_MOVIL_REQUIRED_VERSION_CODE = 185;
    public final static String ATH_MOVIL_MARKET_URL = "market://details?id=com.evertec.athmovil.android";
    public final static String BUNDLE = "bundleID";
    public final static String JSON_DATA_KEY = "jsonData";
    public final static String REFERENCE_NUMBER_KEY = "referenceNumber";
    public final static String PAYMENT_DURATION_TIME_KEY = "purchaseTimeOut";
    public final static long MAX_TIMEOUT_SECONDS = 600000; //10 min
    public final static long MIN_TIMEOUT_SECONDS = 60000; // 1 min
    public final static double MIN_TOTAL_AMOUNT = 1.00;

    public final static String PAYMENT_JSON_PUBLIC_TOKEN_KEY = "businessToken";
    public final static String PAYMENT_JSON_SUBTOTAL__KEY = "subtotal";
    public final static String PAYMENT_JSON_TAX_KEY = "tax";
    public final static String PAYMENT_JSON_TOTAL_KEY = "total";
    public final static String PAYMENT_JSON_SCHEMA_KEY = "callbackSchema";
    public final static String PAYMENT_JSON_METADATA_1 = "metadata1";
    public final static String PAYMENT_JSON_METADATA_2 = "metadata2";
    public final static String PAYMENT_JSON_ITEM_NAME_KEY = "name";

    public final static String PAYMENT_JSON_ITEM_DESCRIPTION_KEY = "description";
    public final static String PAYMENT_JSON_ITEM_PRICE_KEY = "price";
    public final static String PAYMENT_JSON_ITEM_QUANTITY_KEY = "quantity";
    public final static String PAYMENT_JASON_ITEM_METADATA_KEY = "metadata";
    public final static String PAYMENT_JSON_ITEM_LIST_KEY = "items";

    public final static String RETURNED_JSON_KEY = "paymentResult";
    public final static String RETURNED_JSON_STATUS_KEY = "status";
    public final static String RETURNED_JSON_TOTAL_KEY = "total";
    public final static String RETURNED_JSON_SUBTOTAL_KEY = "subtotal";
    public final static String RETURNED_JSON_TAX_KEY = "tax";
    public final static String RETURNED_JSON_METADATA1_KEY = "metadata1";
    public final static String RETURNED_JSON_METADATA2_KEY = "metadata2";
    public final static String RETURNED_JSON_ITEM_DESCRIPTION_KEY = "desc";

    public final static String TOKEN_FOR_SUCCESS = "success";
    public final static String TOKEN_FOR_FAILURE = "failure";
    public final static String STATUS_SUCCESS = "CompletedPayment";
    public final static String STATUS_CANCELED = "CancelledPayment";
    public final static String STATUS_EXPIRED = "ExpiredPayment";
    public final static String REFERENCE_NUMBER = "212786207-2d30019";

    //Strings for exceptions and logs
    public final static String LOG_TAG = "athmCheckoutValidation";
    public final static String NULL_JSON_LOG_MESSAGE = "Json creation returning null.";
    public final static String NULL_ATHMPAYMENT_LOG_MESSAGE = "ATHMPayment is null.";
    public final static String NULL_CONTEXT_LOG_MESSAGE = "Context is null.";
    public final static String NULL_PUBLICTOKEN_LOG_MESSAGE = "BusinessToken is null or empty.";
    public final static String TOTAL_ERROR_LOG_MESSAGE = "Total data type value is invalid.";
    public final static String CARTREFERENCEID_ERROR_LOG_MESSAGE = "CartReferenceId is used to " +
            "return a response to your app it can't be null or empty.";
    public final static String SUBTOTAL_ERROR_LOG_MESSAGE = "Subtotal data type value is invalid.";
    public final static String ITEM_TOTAL_ERROR_LOG_MESSAGE = "Item's price data type value is invalid.";
    public final static String ITEM_QUANTITY_ERROR_LOG_MESSAGE = "Item's quantity data type value is invalid.";
    public final static String ITEM_NAME_ERROR_LOG_MESSAGE = "Item's name value is required.";
    public final static String NULL_METADATA_LOG_MESSAGE = "The metadata data type value is invalid.";
    public final static String NULL_ITEM_METADATA_LOG_MESSAGE = "Item's metadata value is invalid.";
    public final static String ENCODE_JSON_LOG_MESSAGE = "An error occurred while encoding JSON.";
    public final static String DECODE_JSON_LOG_MESSAGE = "An error occurred while decoding JSON.";

    public final static String SCHEMA_ERROR_MESSAGE = "Url scheme value is invalid.";
    public final static String RESPONSE_EXCEPTION_TITLE = "Error in response";
    public final static String RESPONSE_NULL_EXCEPTION = "Empty response.";
    public final static String TAX_NULL_LOG_MESSAGE = "Tax data type value is invalid.";
}