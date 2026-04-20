package com.evertecinc.athmovil.sdk.checkout;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener;
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentResultFlag;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;
import com.evertecinc.athmovil.sdk.checkout.objects.payment.AuthorizationResponse;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.evertecinc.athmovil.sdk.checkout.utils.Util;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import static com.evertecinc.athmovil.sdk.checkout.utils.NewRelicConfig.sendEventToNewRelic;
import static com.evertecinc.athmovil.sdk.checkout.utils.Util.getDateFormat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Date;

public class PaymentResponse {

    /**
     * Method that validates the response to give the user a result
     *
     * @param intent   - intent that has the data
     * @param listener - listener that receives the response
     */
    public static void validatePaymentResponse(@NonNull Intent intent, @NonNull Context context,
                                               @NonNull PaymentResponseListener listener) {
        String publicToken = Util.getPrefsString(ConstantUtil.BasicData.PUBLIC_TOK, context);
        if(!publicToken.equalsIgnoreCase("dummy")){
            if (PaymentResponse.statusVerify(intent, listener)) {
                OpenATHM.authorizationServices(listener, context, intent);
                return;
            }
        }

        validateDataResponse(intent, listener, null);
    }

    public static boolean verifiedGetExtra(@NonNull Intent intent){
        if (intent.getExtras() == null)  return false;

        String jsonResponseValue = intent.getExtras().getString("paymentResult");

        return jsonResponseValue != null;
    }

    public static void setDefaultError(@NonNull PaymentResponseListener listener){
        PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
        listener.onPaymentException(
                ConstantUtil.ExceptionsLogs.RESPONSE_EXCEPTION_TITLE,
                ConstantUtil.ExceptionsLogs.RESPONSE_NULL_EXCEPTION
        );
    }

    public static void setDecodeJsonError(@NonNull PaymentResponseListener listener){
        PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
        listener.onPaymentException(
                ConstantUtil.ExceptionsLogs.RESPONSE_EXCEPTION_TITLE,
                ConstantUtil.ExceptionsLogs.DECODE_JSON_LOG_MESSAGE
        );
    }

    static void validateDataResponse(@NonNull Intent intent, @NonNull PaymentResponseListener listener,
                                     AuthorizationResponse responseService){

        PaymentReturnedData result;
        if (!verifiedGetExtra(intent)) {
            setDefaultError(listener);
            return;
        }

        //Extracting response from intent extras
        String jsonResponseValue = intent.getExtras().getString("paymentResult");

        if (jsonResponseValue.equalsIgnoreCase(ConstantUtil.ExceptionsLogs.EXCEPTION)) {
            PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
            String exceptionCause = intent.getExtras().getString(ConstantUtil.ExceptionsLogs.EXCEPTION_CAUSE);
            if (intent.getExtras().getString(ConstantUtil.ExceptionsLogs.EXCEPTION) != null &&
                    exceptionCause != null) {
                if(exceptionCause.equalsIgnoreCase(ConstantUtil.ExceptionsLogs.RESPONSE_EXCEPTION_TITLE)){
                    listener.onPaymentException(ConstantUtil.ExceptionsLogs.RESPONSE_EXCEPTION_TITLE,
                            ConstantUtil.ExceptionsLogs.PAYMENT_VALIDATION_FAILED);
                    return;
                }
                listener.onPaymentException(intent.getExtras().getString(ConstantUtil.ExceptionsLogs.EXCEPTION_CAUSE),
                        intent.getExtras().getString(ConstantUtil.ExceptionsLogs.EXCEPTION));
            } else {
                setDecodeJsonError(listener);
            }
            return;
        }
        result = checkIfDummy(jsonResponseValue, listener);
        validatePaymentResponse(result, listener, responseService);
    }

    public static PaymentReturnedData checkIfDummy(String response, PaymentResponseListener listener){
        if (
                !response.equalsIgnoreCase("dummy") &&
                !response.equalsIgnoreCase(ConstantUtil.ReturnedJson.STATUS_CANCELLED)
        ) {
            return decodeJSON(response, listener);
        }
        return null;
    }

    public static  Boolean statusVerify(@NonNull Intent intent, @NonNull PaymentResponseListener listener){
        if (intent.getExtras() != null) {
            String jsonResponseValue = intent.getExtras().getString("paymentResult");
            if (jsonResponseValue != null && !TextUtils.isEmpty(jsonResponseValue)) {
                PaymentReturnedData result = PaymentResponse.checkIfDummy(jsonResponseValue, listener);
                if (result != null) {
                    String status = result.getStatus();
                    if (status != null) {
                        status = status.replace("PAYMENT", "");
                        return status.equalsIgnoreCase("COMPLETED");
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method that decodes the payment response data
     *
     * @param response - the payment response
     * @param listener - listener that receives the response
     */
    @VisibleForTesting
    static PaymentReturnedData decodeJSON(String response, PaymentResponseListener listener) {
        Gson gson = new Gson();
        PaymentReturnedData result = null;
        try {
            result = gson.fromJson(response, PaymentReturnedData.class);
        } catch (JsonSyntaxException e) {
            setDecodeJsonError(listener);
        }
        return result;
    }

    /**
     * Method that fills the response data with the request data if there was an error
     *
     * @param result         - the payment response decoded
     * @param paymentRequest - the payment start request
     */
    private static PaymentReturnedData setRequestData(ATHMPayment paymentRequest, PaymentReturnedData result) {
        if (result == null) {
            result = new PaymentReturnedData();
        }

        result.setTotal(paymentRequest.getTotal() != 0.0 ? paymentRequest.getTotal() : 0);
        result.setSubtotal(paymentRequest.getSubtotal() != 0.0 ? paymentRequest.getSubtotal() : 0);
        result.setTax(paymentRequest.getTax() != 0.0 ? paymentRequest.getTax() : 0);
        result.setMetadata1(!paymentRequest.getMetadata1().isEmpty() ? paymentRequest.getMetadata1() : "");
        result.setMetadata2(!paymentRequest.getMetadata2().isEmpty() ? paymentRequest.getMetadata2() : "");
        result.setItems(paymentRequest.getItems());

        return result;
    }

    /**
     * Method that returns the correct response to the listener
     *
     * @param result   - the payment response decoded
     * @param listener - listener that receives the response
     */
    @VisibleForTesting
    static void validatePaymentResponse(PaymentReturnedData result, PaymentResponseListener listener, AuthorizationResponse responseService) {
        ATHMPayment paymentRequest = PaymentResultFlag.getApplicationInstance().getPaymentRequest();
        PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
        if (result == null || result.getTotal() == 0.0) {
            result = setRequestData(paymentRequest, result);
        }

        String status = getStatus(result, responseService);
        if (responseService != null) {
            updateResultFromService(result, responseService, status, false);
        }

        notifyListenerByStatus(status, result, listener);
    }

    @VisibleForTesting
    static void validatePaymentResponse(PaymentResponseListener listener, AuthorizationResponse responseService) {
        PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
        PaymentReturnedData result = new PaymentReturnedData();

        String status = getStatus(null, responseService);
        if (responseService != null) {
            updateResultFromService(result, responseService, status, true);
        }

        notifyListenerByStatus(status, result, listener);
    }

    private static String getStatus(@Nullable PaymentReturnedData result, AuthorizationResponse responseService) {
        String status = (result == null || result.getStatus() == null) ? "PAYMENT NOT FOUND" : result.getStatus().replace("PAYMENT", "");

        if (responseService != null) {
            if (!responseService.getStatus().equalsIgnoreCase("error")) {
                status = handleSuccessStatus(responseService);
            } else {
                status = handleErrorStatus(responseService);
            }
        }
        return status;
    }

    private static String handleSuccessStatus(AuthorizationResponse responseService) {
        if (responseService.getData() != null && "COMPLETED".equals(responseService.getData().getEcommerceStatus())) {
            sendEventToNewRelic(ConstantUtil.NW_RESPONSE_SUCCESS_PAYMENT,
                    responseService.getData().getEcommerceId(),
                    "COMPLETED",
                    PaymentResultFlag.getApplicationInstance().getEcommerceAppName(),
                    ConstantUtil.BUILD_TYPE
            );
            return "COMPLETED";
        } else {
            return "CANCELLED";
        }
    }

    private static String handleErrorStatus(AuthorizationResponse responseService) {
        String schemeForNR = PaymentResultFlag.getApplicationInstance().getEcommerceAppName();
        schemeForNR = schemeForNR != null ? schemeForNR : "N/A";
        sendEventToNewRelic(ConstantUtil.NW_RESPONSE_FAILED_PAYMENT,
                responseService.getData() != null ? responseService.getData().getEcommerceId() : "N/A",
                responseService.getStatus(),
                schemeForNR,
                ConstantUtil.BUILD_TYPE
        );
        return "FAILED";
    }

    private static void updateResultFromService(
            PaymentReturnedData result,
            AuthorizationResponse responseService,
            String status,
            boolean withOutResult
    ) {
        if (!"COMPLETED".equals(status) || responseService.getData() == null) return;

        var data = responseService.getData();
        result.setReferenceNumber(defaultString(data.getReferenceNumber()));
        result.setDailyTransactionID(defaultString(data.getDailyTransactionID()));
        result.setNetAmount(defaultDouble(data.getNetAmount()));
        result.setFee(defaultDouble(data.getFee()));
        result.setMetadata1(defaultString(data.getMetadata1()));
        result.setMetadata2(defaultString(data.getMetadata2()));

        if (withOutResult){
            result.setTotal(defaultDouble(data.getTotal()));
            result.setSubtotal(defaultDouble(data.getSubtotal()));
            result.setTax(defaultDouble(data.getTax()));

            if(data.getItems() instanceof ArrayList)
                result.setItems((ArrayList<Items>) data.getItems());
        }
    }

    private static String defaultString(String value) {
        return value != null ? value : "";
    }

    private static double defaultDouble(Double value) {
        return value != null ? value : 0.0;
    }

    private static void notifyListenerByStatus(String status, PaymentReturnedData result, PaymentResponseListener listener) {
        Date date = getDateFormat(result.getDate());
        switch (status) {
            case "COMPLETED":
                listener.onCompletedPayment(date, result);
                break;
            case "EXPIRED":
                listener.onExpiredPayment(date, result);

                sendEventToNewRelic(ConstantUtil.NW_RESPONSE_FAILED_PAYMENT,
                        ConstantUtil.NW_RESPONSE_EXPIRED_PAYMENT,
                        status,
                        PaymentResultFlag.getApplicationInstance().getEcommerceAppName(),
                        ConstantUtil.BUILD_TYPE
                );
                break;
            case "CANCELLED":
                listener.onCancelledPayment(date, result);
                sendEventToNewRelic(
                    ConstantUtil.NW_RESPONSE_FAILED_PAYMENT,
                    ConstantUtil.NW_RESPONSE_CANCELLED_PAYMENT,
                    status,
                    PaymentResultFlag.getApplicationInstance().getEcommerceAppName(),
                    ConstantUtil.BUILD_TYPE
                );
                break;
            default:
                listener.onFailedPayment(date, result);
                break;
        }
    }
}