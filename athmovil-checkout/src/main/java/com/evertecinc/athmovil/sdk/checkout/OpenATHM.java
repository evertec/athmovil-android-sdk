package com.evertecinc.athmovil.sdk.checkout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.evertecinc.athmovil.sdk.checkout.exceptions.InvalidPublicTokenException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.InvalidPaymentTotalAmountException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullApplicationContextException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullATHMPaymentObjectException;
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.evertecinc.athmovil.sdk.checkout.utils.JsonUtil;

import java.util.ArrayList;

/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 * Main class for the athmovil-checkout library.
 */
public class OpenATHM {

    //TODO: For Evertec Test Only
    private static String buildType = "";
    /**
     * Method that the developer will use to pass the payment data to the library.
     * @param ATHMPayment - Object containing the payment data
     * @throws NullATHMPaymentObjectException - if "ATHMPayment" is null
     * @throws NullApplicationContextException - if null is passed to "setContext" argument
     * @throws InvalidPublicTokenException - if null or empty string is passed to
     *      "setPublicToken" argument
     * @throws InvalidPaymentTotalAmountException - if null or value < 1.00  is passed to
     *      "setTotal" argument
     */
    public static void validateData(ATHMPayment ATHMPayment)
            throws NullATHMPaymentObjectException, NullApplicationContextException,
            InvalidPublicTokenException, InvalidPaymentTotalAmountException {

        buildType = ATHMPayment.getBuildType();
        validateATHMPayment(ATHMPayment);
        defineTimeout(ATHMPayment);
        defineResponse(ATHMPayment);
    }

    /**
     * Validating token received to define the action to take.
     * @param ATHMPayment - Object containing the payment data
     */
    private static void defineResponse(ATHMPayment ATHMPayment) {
        if (ATHMPayment.getPublicToken().equalsIgnoreCase(ConstantUtil.TOKEN_FOR_SUCCESS)) {

            definePaymentReturnedData(ATHMPayment, ConstantUtil.STATUS_SUCCESS, ConstantUtil.REFERENCE_NUMBER,
                    ATHMPayment.getTotal(), ATHMPayment.getTax(), ATHMPayment.getSubtotal(), ATHMPayment.getMetadata1(),
                    ATHMPayment.getMetadata2(), ATHMPayment.getItems());

        } else if (ATHMPayment.getPublicToken().equalsIgnoreCase(ConstantUtil.TOKEN_FOR_FAILURE)){

            definePaymentReturnedData(ATHMPayment,ConstantUtil.STATUS_CANCELED, null,
                    ATHMPayment.getTotal(), ATHMPayment.getTax(), ATHMPayment.getSubtotal(), ATHMPayment.getMetadata1(),
                    ATHMPayment.getMetadata2(), ATHMPayment.getItems());
        } else {

            String businessInfoJson = JsonUtil.toJson(ATHMPayment);
            if (businessInfoJson != null) {
                logForDebug(businessInfoJson);
                execute(ATHMPayment.getContext(), businessInfoJson, ATHMPayment.getTimeout());
            } else {
                logForDebug(ConstantUtil.NULL_JSON_LOG_MESSAGE);
            }
        }
    }
    /**
     * Helper method to create json with dummy data.
     * @param ATHMPayment - contains the data received
     * @param status - dummy payment status (success, canceled, timeout...)
     */
    private static void definePaymentReturnedData(ATHMPayment ATHMPayment, String status, String referenceNumber,
          double total,  double tax, double subtotal, String metadata1, String metadata2, ArrayList<Items> items)
    {
        PaymentReturnedData paymentReturnedData = new PaymentReturnedData();
        paymentReturnedData.setStatus(status);
        paymentReturnedData.setReferenceNumber(referenceNumber);
        paymentReturnedData.setTotal(total);
        paymentReturnedData.setTax(tax);
        paymentReturnedData.setSubtotal(subtotal);
        paymentReturnedData.setMetadata1(metadata1);
        paymentReturnedData.setMetadata2(metadata2);
        paymentReturnedData.setItemsSelectedList(items);

        final String jsonResponse = JsonUtil.returnedJson(paymentReturnedData);
        executeForDebug(ATHMPayment.getContext(),jsonResponse, ATHMPayment.getCallbackSchema());
    }
    /**
     * Validating that the entered time is >= 1 minute or  <= 10 minutes
     * @param ATHMPayment - Object containing the data to validatePaymentResponse
     */
    private static void defineTimeout(ATHMPayment ATHMPayment) {
        if(ATHMPayment.getTimeout() <= 0){
            ATHMPayment.setTimeout(ConstantUtil.MAX_TIMEOUT_SECONDS);
        }else if (ATHMPayment.getTimeout() < ConstantUtil.MIN_TIMEOUT_SECONDS){
            ATHMPayment.setTimeout(ConstantUtil.MIN_TIMEOUT_SECONDS);
        }else if (ATHMPayment.getTimeout() > ConstantUtil.MAX_TIMEOUT_SECONDS){
            ATHMPayment.setTimeout(ConstantUtil.MAX_TIMEOUT_SECONDS);
        }
    }
    /**
     * Validating the information received in ATHMPayment Object
     * @param ATHMPayment - Object containing the data to validatePaymentResponse
     * @throws NullATHMPaymentObjectException - if "ATHMPayment" is null
     * @throws NullApplicationContextException - if null is passed to "setContext" argument
     * @throws InvalidPublicTokenException - if null or empty string is passed to
     *      "setPublicToken" argument
     * @throws InvalidPaymentTotalAmountException - if null or value < 1.00 is passed to
     *      "setTotal" argument
     */
    private static void validateATHMPayment(ATHMPayment ATHMPayment)
            throws NullATHMPaymentObjectException, NullApplicationContextException,
            InvalidPublicTokenException, InvalidPaymentTotalAmountException{

        if (ATHMPayment == null){
            logForDebug(ConstantUtil.NULL_ATHMPAYMENT_LOG_MESSAGE);
            throw new NullATHMPaymentObjectException(ConstantUtil.NULL_ATHMPAYMENT_LOG_MESSAGE);
        }
        if (ATHMPayment.getContext() == null) {
            logForDebug(ConstantUtil.NULL_CONTEXT_LOG_MESSAGE);
            throw new NullApplicationContextException(ConstantUtil.NULL_CONTEXT_LOG_MESSAGE);
        }
        if (ATHMPayment.getPublicToken() == null || ATHMPayment.getPublicToken().isEmpty()){
            logForDebug(ConstantUtil.NULL_PUBLICTOKEN_LOG_MESSAGE);
            throw new InvalidPublicTokenException(ConstantUtil.NULL_PUBLICTOKEN_LOG_MESSAGE);
        }
        if (ATHMPayment.getTotal() < ConstantUtil.MIN_TOTAL_AMOUNT) {
            logForDebug(ConstantUtil.TOTAL_ERROR_LOG_MESSAGE);
            throw new InvalidPaymentTotalAmountException(ConstantUtil.TOTAL_ERROR_LOG_MESSAGE);
        }

    }
    /**
     * Method that looks for the ATH Movil app on the device and get the version code. If app is not
     * installed or the version code is not the required code then it would launch the play store.
     * If the app is found and the version code is correct then the ATH Movil app would be launch.
     * @param context - required context to access package information.
     * @param json - string containing the payment data to be send to ATH Movil.
     * @param timeout - time in seconds that the ATH Movil app would have to complete payment.
     */
     private static void execute( Context context, String json, long timeout){
        PackageInfo athmInfo;
        int athmVersionCode = 0;
        String athmBundleId = ConstantUtil.ATH_MOVIL_ID + buildType;

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(athmBundleId);
        try {
            athmInfo = context.getPackageManager().getPackageInfo(athmBundleId ,0);
            athmVersionCode = athmInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            logForDebug(e.getMessage());
        }
        if(intent == null || athmVersionCode <= ConstantUtil.ATH_MOVIL_REQUIRED_VERSION_CODE){
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(ConstantUtil.ATH_MOVIL_MARKET_URL));
        }

        intent.putExtra(ConstantUtil.APP_BUNDLE_ID_KEY,context.getPackageName());
        intent.putExtra(ConstantUtil.JSON_DATA_KEY, json);
        intent.putExtra(ConstantUtil.PAYMENT_DURATION_TIME_KEY, timeout);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
    /**
     * Simulation of the ATH Movil app response for when the tests tokens are used.
     * @param context - application context used to start activity.
     * @param json - string with the dummy response to simulate json to receive.
     */
    private static void executeForDebug(Context context,  String json, String callbackSchema){
        String appId = context.getPackageName() + "." + callbackSchema;
        Intent intent = new Intent(appId);
        intent.putExtra(ConstantUtil.RETURNED_JSON_KEY,json);
        context.startActivity(intent);
    }
    /**
     * Helper method for log errors on console only when in debug mode.
     * @param message - error message received as string.
     */
    private static void logForDebug(String message){
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug")){
            Log.d(ConstantUtil.LOG_TAG, message);
        }
    }
}