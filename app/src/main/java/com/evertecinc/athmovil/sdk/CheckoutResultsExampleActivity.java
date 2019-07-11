package com.evertecinc.athmovil.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import com.evertecinc.athmovil.sdk.checkout.PaymentResponse;
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import java.util.ArrayList;

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
    public void onCompletedPayment(String referenceNumber, Double total, Double tax, Double subtotal,
                                   String metadata1, String metadata2, ArrayList<Items> items)
    {
        tvTransactionStatus.setText("CompletedPayment");
        String message = "referenceNumber " + referenceNumber + "\ntotal " + total + "\nsubtotal " + subtotal
                + "\ntax " + tax + "\nmetadata1 " + metadata1 + "\nmetadata2 " + metadata2 + "\nitems " + getAllItems(items);
        tvTransactionData.setText(message);
    }

    @Override
    public void onCancelledPayment(String referenceNumber, Double total, Double tax, Double subtotal,
                                   String metadata1, String metadata2, ArrayList<Items> items)
    {
        tvTransactionStatus.setText("CancelledPayment");
        String message = "referenceNumber " + referenceNumber + "\ntotal " + total + "\nsubtotal " + subtotal
                + "\ntax " + tax + "\nmetadata1 " + metadata1 + "\nmetadata2 " + metadata2 + "\nitems " + getAllItems(items);
        tvTransactionData.setText(message);
    }

    @Override
    public void onExpiredPayment(String referenceNumber, Double total, Double tax, Double subtotal,
                                 String metadata1, String metadata2, ArrayList<Items> items)
    {
        tvTransactionStatus.setText("ExpiredPayment");
        String message = "referenceNumber " + referenceNumber + "\ntotal " + total + "\nsubtotal " + subtotal
                + "\ntax " + tax + "\nmetadata1 " + metadata1 + "\nmetadata2 " + metadata2 + "\nitems " + getAllItems(items);
        tvTransactionData.setText(message);
    }

    private StringBuilder getAllItems(ArrayList<Items> items){
        StringBuilder allItems = new StringBuilder();
        for (Items item : items) {
            allItems.append("\nname " + item.getName() + ", desc " + item.getDesc() + ", price " + item.getPrice()
                    + ", quantity " + item.getQuantity() + ", metadata " + item.getMetadata() +"\n");
        }
        return allItems;
    }
}
