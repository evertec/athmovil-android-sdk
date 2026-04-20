package com.evertecinc.athmovil.sdk.checkout.interfaces;

import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;
import java.util.Date;

public interface PaymentResponseListener {

    void onCompletedPayment(Date date, PaymentReturnedData result);

    void onCancelledPayment(Date date, PaymentReturnedData result);

    void onExpiredPayment(Date date, PaymentReturnedData result);

    void onFailedPayment(Date date, PaymentReturnedData result);

    void onPaymentException(String error, String description);

}