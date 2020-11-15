package com.evertecinc.athmovil.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

import com.evertecinc.athmovil.sdk.checkout.PaymentResponse;
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;

import java.util.ArrayList;
import java.util.Date;

public class CheckoutResultsExampleActivity extends Activity implements PaymentResponseListener {

    TextView tvTransactionStatus, tvTransactionData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_example);
        tvTransactionStatus = findViewById(R.id.tvTransactionStatus);
        tvTransactionData = findViewById(R.id.tvTransactionData);
        PaymentResponse.validatePaymentResponse(getIntent(), this);
    }

    @Override
    public void onCompletedPayment(Date date, String referenceNumber, String dailyTransactionID,
                                   String name, String phoneNumber, String email,
                                   Double total, Double tax, Double subtotal, Double fee, Double netAmount,
                                   String metadata1, String metadata2, ArrayList<Items> items) {
        tvTransactionStatus.setText(R.string.ATHM_COMPLETED_STATUS);
        String message = "referenceNumber " + referenceNumber + "\ntotal " + total + "\nsubtotal " + subtotal
                + "\ntax " + tax + "\nmetadata1 " + metadata1 + "\nmetadata2 " + metadata2
                + "\ndailyTransactionID " + dailyTransactionID + "\nname " + name + "\nphoneNumber " + phoneNumber
                + "\ndate " + date.toString() + "\nemail " + email + "\nfee " + fee + "\nnetAmount " + netAmount +
                "\nitems " + getAllItems(items);
        tvTransactionData.setText(message);
    }

    @Override
    public void onCancelledPayment(Date date, String referenceNumber, String dailyTransactionID,
                                   String name, String phoneNumber, String email,
                                   Double total, Double tax, Double subtotal, Double fee, Double netAmount,
                                   String metadata1, String metadata2, ArrayList<Items> items) {
        tvTransactionStatus.setText(R.string.ATHM_CANCELLED_STATUS);
        String message = "referenceNumber " + referenceNumber + "\ntotal " + total + "\nsubtotal " + subtotal
                + "\ntax " + tax + "\nmetadata1 " + metadata1 + "\nmetadata2 " + metadata2
                + "\ndailyTransactionID " + dailyTransactionID + "\nname " + name + "\nphoneNumber " + phoneNumber
                + "\ndate " + date.toString() + "\nemail " + email + "\nfee " + fee + "\nnetAmount " + netAmount +
                "\nitems " + getAllItems(items);
        tvTransactionData.setText(message);
    }

    @Override
    public void onExpiredPayment(Date date, String referenceNumber, String dailyTransactionID,
                                 String name, String phoneNumber, String email,
                                 Double total, Double tax, Double subtotal, Double fee, Double netAmount,
                                 String metadata1, String metadata2, ArrayList<Items> items) {

        tvTransactionStatus.setText(R.string.ATHM_EXPIRED_STATUS);
        String message = "referenceNumber " + referenceNumber + "\ntotal " + total + "\nsubtotal " + subtotal
                + "\ntax " + tax + "\nmetadata1 " + metadata1 + "\nmetadata2 " + metadata2
                + "\ndailyTransactionID " + dailyTransactionID + "\nname " + name + "\nphoneNumber " + phoneNumber
                + "\ndate " + date.toString() + "\nemail " + email + "\nfee " + fee + "\nnetAmount " + netAmount +
                "\nitems " + getAllItems(items);
        tvTransactionData.setText(message);
    }

    @Override
    public void onPaymentException(String error, String description) {
        Toast.makeText(this, error + " " + description, Toast.LENGTH_LONG).show();
    }

    private StringBuilder getAllItems(ArrayList<Items> items) {
        StringBuilder allItems = new StringBuilder();
        for (Items item : items) {
            allItems.append("\nname ").append(item.getName()).append(", desc ").append(item.getDesc()).append(", price ").append(item.getPrice()).append(", quantity ").append(item.getQuantity()).append(", metadata ").append(item.getMetadata()).append("\n");
        }
        return allItems;
    }
}
