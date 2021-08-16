package com.evertecinc.athmovil.sdk.checkout;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener;
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentResultFlag;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.google.gson.Gson;

import static com.evertecinc.athmovil.sdk.checkout.utils.Util.getDateFormat;

public class PaymentResponse {

    /**
     * Method that validates the response to give the user a result
     *
     * @param intent   - intent that has the data
     * @param listener - listener that receives the response
     */
    public static void validatePaymentResponse(@NonNull Intent intent,
                                               @NonNull PaymentResponseListener listener) {
        PaymentReturnedData result = null;
        if (intent.getExtras() == null) {
            listener.onPaymentException(ConstantUtil.RESPONSE_EXCEPTION_TITLE,
                    ConstantUtil.RESPONSE_NULL_EXCEPTION);
            return;
        }
        //Extracting response from intent extras
        String jsonResponseValue = intent.getExtras().getString("paymentResult");

        if (jsonResponseValue == null) {
            PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
            listener.onPaymentException(ConstantUtil.RESPONSE_EXCEPTION_TITLE,
                    ConstantUtil.RESPONSE_NULL_EXCEPTION);
            return;
        }
        if (jsonResponseValue.equalsIgnoreCase(ConstantUtil.EXCEPTION)) {
            PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
            String exceptionCause = intent.getExtras().getString(ConstantUtil.EXCEPTION_CAUSE);
            if (intent.getExtras().getString(ConstantUtil.EXCEPTION) != null &&
                    exceptionCause != null) {
                if(exceptionCause.equalsIgnoreCase(ConstantUtil.RESPONSE_EXCEPTION_TITLE)){
                    listener.onPaymentException(ConstantUtil.RESPONSE_EXCEPTION_TITLE,
                            ConstantUtil.PAYMENT_VALIDATION_FAILED);
                    return;
                }
                listener.onPaymentException(intent.getExtras().getString(ConstantUtil.EXCEPTION_CAUSE),
                        intent.getExtras().getString(ConstantUtil.EXCEPTION));
            } else {
                listener.onPaymentException(ConstantUtil.RESPONSE_EXCEPTION_TITLE,
                        ConstantUtil.DECODE_JSON_LOG_MESSAGE);
            }
            return;
        }
        result = checkIfDummy(jsonResponseValue, listener);
        validatePaymentResponse(result, listener);
    }

    public static PaymentReturnedData checkIfDummy(String response, PaymentResponseListener listener){
        if (!response.equalsIgnoreCase("dummy") &&
                !response.equalsIgnoreCase(ConstantUtil.STATUS_CANCELLED)) {
            return decodeJSON(response, listener);
        }
        return null;
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
        } catch (Exception e) {
            listener.onPaymentException(ConstantUtil.RESPONSE_EXCEPTION_TITLE,
                    ConstantUtil.DECODE_JSON_LOG_MESSAGE);
            PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
        }
        return result;
    }

    /**
     * Method that fills the response data with the request data if there was an error
     *
     * @param result         - the payment response decoded
     * @param paymentRequest - the payment start request
     */
    private static PaymentReturnedData setRequestData(ATHMPayment paymentRequest,
                                                      PaymentReturnedData result) {
        if (result == null) {
            result = new PaymentReturnedData();
        }
        result.setTotal(paymentRequest.getTotal());
        result.setSubtotal(paymentRequest.getSubtotal());
        result.setTax(paymentRequest.getTax());
        result.setMetadata1(paymentRequest.getMetadata1());
        result.setMetadata2(paymentRequest.getMetadata2());
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
    static void validatePaymentResponse(PaymentReturnedData result,
                                        PaymentResponseListener listener) {
        ATHMPayment paymentRequest = PaymentResultFlag.getApplicationInstance().getPaymentRequest();
        PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
        if (result == null || result.getTotal() == 0.0) {
            result = setRequestData(paymentRequest, result);
        }
        String status;
        if (result.getStatus() == null) {
            status = "PAYMENT NOT FOUND";
        } else {
            status = result.getStatus().replace("PAYMENT", "");
        }
        switch (status) {
            case "COMPLETED":
                listener.onCompletedPayment(getDateFormat(result.getDate()),
                        result.getReferenceNumber(), result.getDailyTransactionID(),
                        result.getName(), result.getPhoneNumber(), result.getEmail(),
                        result.getTotal(), result.getTax(), result.getSubtotal(), result.getFee(),
                        result.getNetAmount(), result.getMetadata1(), result.getMetadata2(),
                        result.getPaymentId(), result.getItemsSelectedList());
                break;
            case "EXPIRED":
                listener.onExpiredPayment(getDateFormat(result.getDate()),
                        result.getReferenceNumber(), result.getDailyTransactionID(),
                        result.getName(), result.getPhoneNumber(), result.getEmail(),
                        result.getTotal(), result.getTax(), result.getSubtotal(), result.getFee(),
                        result.getNetAmount(), result.getMetadata1(), result.getMetadata2(),
                        result.getPaymentId(), result.getItemsSelectedList());
                break;
            default:
                listener.onCancelledPayment(getDateFormat(result.getDate()),
                        result.getReferenceNumber(), result.getDailyTransactionID(),
                        result.getName(), result.getPhoneNumber(), result.getEmail(),
                        result.getTotal(), result.getTax(), result.getSubtotal(), result.getFee(),
                        result.getNetAmount(), result.getMetadata1(), result.getMetadata2(),
                        result.getPaymentId(), result.getItemsSelectedList());
                break;
        }
    }
}