package com.evertecinc.athmovil.sdk.checkout.interfaces;

import com.evertecinc.athmovil.sdk.checkout.objects.Items;

import java.util.ArrayList;

public interface PaymentResponseListener {

    void onCompletedPayment(String referenceNumber, Double total, Double tax, Double subtotal,
                            String metadata1, String metadata2, ArrayList<Items> items);

    void onCancelledPayment(String referenceNumber, Double total, Double tax, Double subtotal,
                            String metadata1, String metadata2, ArrayList<Items> items);

    void onExpiredPayment (String referenceNumber, Double total, Double tax, Double subtotal,
                           String metadata1, String metadata2, ArrayList<Items> items);
}




