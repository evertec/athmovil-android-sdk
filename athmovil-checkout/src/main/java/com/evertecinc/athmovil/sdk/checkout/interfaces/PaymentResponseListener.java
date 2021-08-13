package com.evertecinc.athmovil.sdk.checkout.interfaces;

import com.evertecinc.athmovil.sdk.checkout.objects.Items;

import java.util.ArrayList;
import java.util.Date;

public interface PaymentResponseListener {

    void onCompletedPayment(Date date, String referenceNumber, String dailyTransactionID,
                            String name, String phoneNumber, String email,
                            Double total, Double tax, Double subtotal, Double fee, Double netAmount,
                            String metadata1, String metadata2, String paymentId, ArrayList<Items> items);

    void onCancelledPayment(Date date, String referenceNumber, String dailyTransactionID,
                            String name, String phoneNumber, String email,
                            Double total, Double tax, Double subtotal, Double fee, Double netAmount,
                            String metadata1, String metadata2, String paymentId, ArrayList<Items> items);

    void onExpiredPayment(Date date, String referenceNumber, String dailyTransactionID,
                          String name, String phoneNumber, String email,
                          Double total, Double tax, Double subtotal, Double fee, Double netAmount,
                          String metadata1, String metadata2, String paymentId, ArrayList<Items> items);

    void onPaymentException(String error, String description);
}