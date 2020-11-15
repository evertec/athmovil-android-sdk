package com.evertecinc.athmovil.sdk.checkout;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.google.gson.Gson;

import static com.evertecinc.athmovil.sdk.checkout.utils.Util.getDateFormat;

public class PaymentResponse {

    public static void validatePaymentResponse(@NonNull Intent intent, @NonNull PaymentResponseListener listener) {
        PaymentReturnedData result;
        if (intent.getExtras() == null) {
            listener.onPaymentException(ConstantUtil.RESPONSE_EXCEPTION_TITLE, ConstantUtil.RESPONSE_NULL_EXCEPTION);
            return;
        }
        //Extracting response from intent extras
        String jsonResponseValue = intent.getExtras().getString("paymentResult");
        if (jsonResponseValue == null) {
            listener.onPaymentException(ConstantUtil.RESPONSE_EXCEPTION_TITLE, ConstantUtil.RESPONSE_NULL_EXCEPTION);
            return;
        }
        result = decodeJSON(jsonResponseValue, listener);
        if (result != null)
            validatePaymentResponse(result, listener);
    }

    @VisibleForTesting
    static PaymentReturnedData decodeJSON(String response, PaymentResponseListener listener) {
        Gson gson = new Gson();
        PaymentReturnedData result = null;
        try {
            result = gson.fromJson(response, PaymentReturnedData.class);
        }catch (Exception e){
            listener.onPaymentException(ConstantUtil.RESPONSE_EXCEPTION_TITLE, ConstantUtil.DECODE_JSON_LOG_MESSAGE);
        }
        return result;
    }

    @VisibleForTesting
    static void validatePaymentResponse(PaymentReturnedData result,
                                        PaymentResponseListener listener) {
        String status = result.getStatus();
        switch (status) {
            case "CompletedPayment":
                listener.onCompletedPayment(getDateFormat(result.getDate()),
                        result.getReferenceNumber(), result.getDailyTransactionID(),
                        result.getName(), result.getPhoneNumber(), result.getEmail(),
                        result.getTotal(), result.getTax(), result.getSubtotal(), result.getFee(),
                        result.getNetAmount(), result.getMetadata1(), result.getMetadata2(),
                        result.getItemsSelectedList());
                break;
            case "ExpiredPayment":
                listener.onExpiredPayment(getDateFormat(result.getDate()),
                        result.getReferenceNumber(), result.getDailyTransactionID(),
                        result.getName(), result.getPhoneNumber(), result.getEmail(),
                        result.getTotal(), result.getTax(), result.getSubtotal(), result.getFee(),
                        result.getNetAmount(), result.getMetadata1(), result.getMetadata2(),
                        result.getItemsSelectedList());
                break;
            default:
                listener.onCancelledPayment(getDateFormat(result.getDate()),
                        result.getReferenceNumber(), result.getDailyTransactionID(),
                        result.getName(), result.getPhoneNumber(), result.getEmail(),
                        result.getTotal(), result.getTax(), result.getSubtotal(), result.getFee(),
                        result.getNetAmount(), result.getMetadata1(), result.getMetadata2(),
                        result.getItemsSelectedList());
                break;
        }
    }
}