package com.evertecinc.athmovil.sdk.checkout.utils;
/**
 * Created by Juan Gabriel Zaragoza Bonilla on 4/11/2018.
 */
public class ConstantUtil {
    public final static String ATH_MOVIL_ID = "com.evertec.athmovil.android";
    public final static int ATH_MOVIL_REQUIRED_VERSION_CODE = 164 ;
    public final static String ATH_MOVIL_MARKET_URL = "market://details?id=com.evertec.athmovil.android";
    public final static String APP_BUNDLE_ID_KEY = "bundleID";
    public final static String JSON_DATA_KEY = "jsonData";
    public final static String CART_REFERENCE_ID_KEY = "cartReferenceId";
    public final static String PURCHASE_DURATION_TIME_KEY = "purchaseTimeOut";
    public final static long MAX_TIMEOUT_MILLISECONDS = 600000; //10 min
    public final static long MIN_TIMEOUT_MILLISECONDS = 60000; // 1 min
    public final static double MIN_TOTAL_AMOUNT = 1.00;

    public final static String PURCHASE_JSON_BUSINESS_TOKEN_KEY = "businessToken";
    public final static String PURCHASE_JSON_SUBTOTAL__KEY = "subtotal";
    public final static String PURCHASE_JSON_TAX_KEY = "tax";
    public final static String PURCHASE_JSON_TOTAL_KEY = "total";
    public final static String PURCHASE_JSON_ITEM_NAME_KEY = "name";
    public final static String PURCHASE_JSON_ITEM_DESCRIPTION_KEY = "description";
    public final static String PURCHASE_JSON_ITEM_PRICE_KEY = "price";
    public final static String PURCHASE_JSON_ITEM_QUANTITY_KEY = "quantity";
    public final static String PURCHASE_JSON_ITEM_LIST_KEY = "itemsSelectedList";

    public final static String RETURNED_JSON_KEY = "paymentResult";
    public final static String RETURNED_JSON_COMPLETION_KEY = "completed";
    public final static String RETURNED_JSON_STATUS_KEY = "status";
    public final static String RETURNED_JSON_DAILY_TRANSACTION_ID_KEY = "dailyTransactionId";
    public final static String RETURNED_JSON_TRANSACTION_REFERENCE_KEY = "transactionReference";

    //Strings for dummy responses
    public final static String TOKEN_FOR_SUCCESS = "successTestToken";
    public final static String TOKEN_FOR_FAILURE = "failureTestToken";
    public final static String STATUS_SUCCESS = "Success";
    public final static String STATUS_CANCELED = "Canceled";
    public final static String COMPLETED_TRUE = "true";
    public final static String COMPLETED_FALSE = "false";
    public final static String TRANSACTION_ID = "#0001";
    public final static String TRANSACTION_REFERENCE = "212786207-2d30019";

    //Strings for exceptions and logs
    public final static String LOG_TAG = "athmCheckoutValidation";
    public final static String NULL_JSON_LOG_MESSAGE = "Json creation returning null.";
    public final static String NULL_PURCHASEDATA_LOG_MESSAGE = ">PurchaseData is null.";
    public final static String NULL_CONTEXT_LOG_MESSAGE = "Context is null.";
    public final static String NULL_BUSINESTOKEN_LOG_MESSAGE = "BusinessToken is null or empty.";
    public final static String TOTAL_ERROR_LOG_MESSAGE = "Total can't be null or less than $1.00";
    public final static String CARTREFERENCEID_ERROR_LOG_MESSAGE = "CartReferenceId is used to " +
            "return a response to your app it can't be null or empty.";

}