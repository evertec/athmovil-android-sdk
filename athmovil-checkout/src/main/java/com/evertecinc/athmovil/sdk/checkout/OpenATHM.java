package com.evertecinc.athmovil.sdk.checkout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.evertecinc.athmovil.sdk.checkout.exceptions.InvalidBusinessTokenException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.InvalidPurchaseTotalAmountException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullApplicationContextException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullCartReferenceIdException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullPurchaseDataObjectException;
import com.evertecinc.athmovil.sdk.checkout.objects.PurchaseData;
import com.evertecinc.athmovil.sdk.checkout.objects.PurchaseReturnedData;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.evertecinc.athmovil.sdk.checkout.utils.JsonUtil;
/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 * Main class for the athmovil-checkout library.
 */
public class OpenATHM {
    /**
     * Method that the developer will use to pass the purchase data to the library.
     * @param purchaseData - Object containing the purchase data
     * @throws NullPurchaseDataObjectException - if "purchaseData" is null
     * @throws NullApplicationContextException - if null is passed to "setContext" argument
     * @throws InvalidBusinessTokenException - if null or empty string is passed to
     *      "setBusinessToken" argument
     * @throws InvalidPurchaseTotalAmountException - if null or value < 1.00  is passed to
     *      "setTotal" argument
     * @throws NullCartReferenceIdException - if null or empty string is passed to
     *      "setCartReferenceId" argument
     */
    public static void validateData(PurchaseData purchaseData)
            throws NullPurchaseDataObjectException, NullApplicationContextException,
            InvalidBusinessTokenException, InvalidPurchaseTotalAmountException,
            NullCartReferenceIdException {

        validatePurchaseData(purchaseData);
        defineTimer(purchaseData);
        defineResponse(purchaseData);
    }

    /**
     * Validating token received to define the action to take.
     * @param purchaseData - Object containing the purchase data
     */
    private static void defineResponse(PurchaseData purchaseData) {
        if (purchaseData.getBusinessToken().equalsIgnoreCase(ConstantUtil.TOKEN_FOR_SUCCESS)) {

            definePurchaseReturnedData(purchaseData,ConstantUtil.COMPLETED_TRUE,
                    ConstantUtil.STATUS_SUCCESS, ConstantUtil.TRANSACTION_ID,
                    ConstantUtil.TRANSACTION_REFERENCE);

        } else if (purchaseData.getBusinessToken().equalsIgnoreCase(ConstantUtil.TOKEN_FOR_FAILURE)){

            definePurchaseReturnedData(purchaseData,ConstantUtil.COMPLETED_FALSE,
                    ConstantUtil.STATUS_CANCELED, null, null);
        } else {

            String businessInfoJson = JsonUtil.toJson(purchaseData);
            if (businessInfoJson != null) {
                logForDebug(businessInfoJson);
                execute(purchaseData.getContext(), businessInfoJson,
                        purchaseData.getCartReferenceId(), purchaseData.getTimer());
            } else {
                logForDebug(ConstantUtil.NULL_JSON_LOG_MESSAGE);
            }
        }
    }
    /**
     * Helper method to create json with dummy data.
     * @param purchaseData - contains the data received
     * @param isCompleted - string containing "true" or "false" as dummy response
     * @param status - dummy purchase status (success, canceled, timeout...)
     * @param transactionId - dummy string for transaction id if success
     * @param transactionReference - dummy string for transaction reference if success
     */
    private static void definePurchaseReturnedData(PurchaseData purchaseData, String
            isCompleted, String status, String transactionId, String transactionReference){
        PurchaseReturnedData purchaseReturnedData = new PurchaseReturnedData();
        purchaseReturnedData.setCompleted(isCompleted);
        purchaseReturnedData.setStatus(status);
        purchaseReturnedData.setCartReferenceId(purchaseData.getCartReferenceId());
        purchaseReturnedData.setDailyTransactionId(transactionId);
        purchaseReturnedData.setTransactionReference(transactionReference);
        final String jsonResponse = JsonUtil.returnedJson(purchaseReturnedData);
        executeForDebug(purchaseData.getContext(),jsonResponse);
    }
    /**
     * Validating that the entered time is >= 1 minute or  <= 10 minutes
     * @param purchaseData - Object containing the data to validate
     */
    private static void defineTimer(PurchaseData purchaseData) {
        if(purchaseData.getTimer() <= 0){
            purchaseData.setTimer(ConstantUtil.MAX_TIMEOUT_MILLISECONDS);
        }else if (purchaseData.getTimer() < ConstantUtil.MIN_TIMEOUT_MILLISECONDS){
            purchaseData.setTimer(ConstantUtil.MIN_TIMEOUT_MILLISECONDS);
        }else if (purchaseData.getTimer() > ConstantUtil.MAX_TIMEOUT_MILLISECONDS){
            purchaseData.setTimer(ConstantUtil.MAX_TIMEOUT_MILLISECONDS);
        }
    }
    /**
     * Validating the information received in PurchaseData Object
     * @param purchaseData - Object containing the data to validate
     * @throws NullPurchaseDataObjectException - if "purchaseData" is null
     * @throws NullApplicationContextException - if null is passed to "setContext" argument
     * @throws InvalidBusinessTokenException - if null or empty string is passed to
     *      "setBusinessToken" argument
     * @throws InvalidPurchaseTotalAmountException - if null or value < 1.00 is passed to
     *      "setTotal" argument
     * @throws NullCartReferenceIdException - if null or empty string is passed to
     *      "setCartReferenceId" argument
     */
    private static void validatePurchaseData(PurchaseData purchaseData)
            throws NullPurchaseDataObjectException, NullApplicationContextException,
            InvalidBusinessTokenException, InvalidPurchaseTotalAmountException,
            NullCartReferenceIdException {

        if (purchaseData == null){
            logForDebug(ConstantUtil.NULL_PURCHASEDATA_LOG_MESSAGE);
            throw new NullPurchaseDataObjectException(ConstantUtil.NULL_PURCHASEDATA_LOG_MESSAGE);
        }
        if (purchaseData.getContext() == null) {
            logForDebug(ConstantUtil.NULL_CONTEXT_LOG_MESSAGE);
            throw new NullApplicationContextException(ConstantUtil.NULL_CONTEXT_LOG_MESSAGE);
        }
        if (purchaseData.getBusinessToken() == null || purchaseData.getBusinessToken().isEmpty()){
            logForDebug(ConstantUtil.NULL_BUSINESTOKEN_LOG_MESSAGE);
            throw new InvalidBusinessTokenException(ConstantUtil.NULL_BUSINESTOKEN_LOG_MESSAGE);
        }
        if (purchaseData.getTotal() == null || Double.parseDouble(purchaseData.getTotal()) <
                ConstantUtil.MIN_TOTAL_AMOUNT) {
            logForDebug(ConstantUtil.TOTAL_ERROR_LOG_MESSAGE);
            throw new InvalidPurchaseTotalAmountException(ConstantUtil.TOTAL_ERROR_LOG_MESSAGE);
        }
        if(purchaseData.getCartReferenceId() == null || purchaseData.getCartReferenceId().isEmpty()){
            logForDebug(ConstantUtil.CARTREFERENCEID_ERROR_LOG_MESSAGE);
            throw new NullCartReferenceIdException(ConstantUtil.CARTREFERENCEID_ERROR_LOG_MESSAGE);
        }
    }
    /**
     * Method that looks for the ATH Movil app on the device and get the version code. If app is not
     * installed or the version code is not the required code then it would launch the play store.
     * If the app is found and the version code is correct then the ATH Movil app would be launch.
     * @param context - required context to access package information.
     * @param json - string containing the purchase data to be send to ATH Movil.
     * @param cartReferenceId - id that would be used by the business to identify their cart.
     * @param timer - time in milliseconds that the ATH Movil app would have to complete purchase.
     */
     private static void execute( Context context, String json, String cartReferenceId, long timer){
        PackageInfo athmInfo;
        int athmVersionCode = 0;

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                ConstantUtil.ATH_MOVIL_ID);
        try {
            athmInfo = context.getPackageManager().getPackageInfo(ConstantUtil.ATH_MOVIL_ID ,0);
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
        intent.putExtra(ConstantUtil.CART_REFERENCE_ID_KEY, cartReferenceId);
        intent.putExtra(ConstantUtil.PURCHASE_DURATION_TIME_KEY,timer);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }
    /**
     * Simulation of the ATH Movil app response for when the tests tokens are used.
     * @param context - application context used to start activity.
     * @param json - string with the dummy response to simulate json to receive.
     */
    private static void executeForDebug(Context context,  String json){
        String appId = context.getPackageName();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appId);
        intent.putExtra(ConstantUtil.RETURNED_JSON_KEY,json);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
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