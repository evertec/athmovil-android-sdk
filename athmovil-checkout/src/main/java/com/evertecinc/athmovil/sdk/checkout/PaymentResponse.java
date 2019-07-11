package com.evertecinc.athmovil.sdk.checkout;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;
import com.google.gson.Gson;

public class PaymentResponse {

    public static void validatePaymentResponse(@NonNull Intent intent, @NonNull PaymentResponseListener listener) {
        PaymentReturnedData result;
        if (intent.getExtras() == null) { return;}
        //Extracting response from intent extras
        String jsonResponseValue = intent.getExtras().getString("paymentResult");
        if (jsonResponseValue == null) {return;}
        Gson gson = new Gson();
        result = gson.fromJson(jsonResponseValue, PaymentReturnedData.class);
        if (result == null){return;}
        validatePaymentResponse(result,listener);
    }

    @VisibleForTesting
    static void validatePaymentResponse(PaymentReturnedData result, PaymentResponseListener listener){
        String status = result.getStatus();
        switch (status) {
            case "CompletedPayment":
                listener.onCompletedPayment(result.getReferenceNumber(), result.getTotal(),
                        result.getTax(), result.getSubtotal(), result.getMetadata1(),
                        result.getMetadata2(), result.getItemsSelectedList());
                break;
            case "ExpiredPayment":
                listener.onExpiredPayment(result.getReferenceNumber(), result.getTotal(),
                        result.getTax(), result.getSubtotal(), result.getMetadata1(),
                        result.getMetadata2(), result.getItemsSelectedList());
                break;
            default:
                listener.onCancelledPayment(result.getReferenceNumber(), result.getTotal(),
                        result.getTax(), result.getSubtotal(), result.getMetadata1(),
                        result.getMetadata2(), result.getItemsSelectedList());
                break;
        }
    }
}
