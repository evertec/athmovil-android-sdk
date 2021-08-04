package com.evertecinc.athmovil.sdk;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.evertecinc.athmovil.sdk.checkout.PaymentResponse;
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.evertecinc.athmovil.sdk.databinding.ActivityPaymentResponseBinding;

import java.util.ArrayList;
import java.util.Date;

public class PaymentResponseActivity extends AppCompatActivity implements PaymentResponseListener, View.OnClickListener {
    

    private ActivityPaymentResponseBinding binding;
    private ArrayList<Items> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_response);
            binding.executePendingBindings();
            binding.setLifecycleOwner(this);
            binding.llShowItemsContainer.setOnClickListener(this);
            binding.btnClose.setOnClickListener(this);
            PaymentResponse.validatePaymentResponse(getIntent(), this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llShowItemsContainer:
                if (!items.isEmpty()) {
                    Intent intent = new Intent(this, ItemsListActivity.class);
                    intent.putExtra("items", items);
                    startActivity(intent);
                }
                break;
            case R.id.btnClose:
                onBackPressed();
                break;
            default:
                break;
        }
    }



    @Override
    public void onCancelledPayment(Date date, String referenceNumber, String dailyTransactionID,
                                   String name, String phoneNumber, String email,
                                   Double total, Double tax, Double subtotal, Double fee, Double netAmount,
                                   String metadata1, String metadata2, String paymentId, ArrayList<Items> items) {
        binding.tvStatus.setText("CANCELLED");
        binding.tvReferenceNumber.setText(referenceNumber);
        binding.tvTotal.setText(Utils.getBalanceString(total.toString()));
        binding.tvTax.setText(Utils.getBalanceString(tax.toString()));
        binding.tvSubtotal.setText(Utils.getBalanceString(subtotal.toString()));
        binding.tvFee.setText(Utils.getBalanceString(fee.toString()));
        binding.tvNetAmount.setText(Utils.getBalanceString(netAmount.toString()));
        binding.tvMetadata1.setText(metadata1);
        binding.tvMetadata2.setText(metadata2);
        binding.tvEmail.setText(email);
        binding.tvName.setText(name);
        binding.tvDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", date));
        binding.tvDailyTransactionID.setText(dailyTransactionID);
        binding.tvPhoneNumber.setText(phoneNumber);

        this.items = items;
    }

    @Override
    public void onExpiredPayment(Date date, String referenceNumber, String dailyTransactionID,
                                 String name, String phoneNumber, String email,
                                 Double total, Double tax, Double subtotal, Double fee, Double netAmount,
                                 String metadata1, String metadata2, String paymentId, ArrayList<Items> items) {
        binding.tvStatus.setText("EXPIRED");
        binding.tvReferenceNumber.setText(referenceNumber);
        binding.tvTotal.setText(Utils.getBalanceString(total.toString()));
        binding.tvTax.setText(Utils.getBalanceString(tax.toString()));
        binding.tvSubtotal.setText(Utils.getBalanceString(subtotal.toString()));
        binding.tvFee.setText(Utils.getBalanceString(fee.toString()));
        binding.tvNetAmount.setText(Utils.getBalanceString(netAmount.toString()));
        binding.tvMetadata1.setText(metadata1);
        binding.tvMetadata2.setText(metadata2);
        binding.tvEmail.setText(email);
        binding.tvName.setText(name);
        binding.tvDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", date));
        binding.tvDailyTransactionID.setText(dailyTransactionID);
        binding.tvPhoneNumber.setText(phoneNumber);

        this.items = items;
    }

    @Override
    public void onCompletedPayment(Date date, String referenceNumber, String dailyTransactionID,
                                   String name, String phoneNumber, String email,
                                   Double total, Double tax, Double subtotal, Double fee, Double netAmount,
                                   String metadata1, String metadata2, String paymentId, ArrayList<Items> items) {
        binding.tvStatus.setText("COMPLETED");
        binding.tvReferenceNumber.setText(referenceNumber);
        binding.tvTotal.setText(Utils.getBalanceString(total.toString()));
        binding.tvTax.setText(Utils.getBalanceString(tax.toString()));
        binding.tvSubtotal.setText(Utils.getBalanceString(subtotal.toString()));
        binding.tvFee.setText(Utils.getBalanceString(fee.toString()));
        binding.tvNetAmount.setText(Utils.getBalanceString(netAmount.toString()));
        binding.tvMetadata1.setText(metadata1);
        binding.tvMetadata2.setText(metadata2);
        binding.tvEmail.setText(email);
        binding.tvName.setText(name);
        binding.tvDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", date));
        binding.tvDailyTransactionID.setText(dailyTransactionID);
        binding.tvPhoneNumber.setText(phoneNumber);

        this.items = items;
    }

    @Override
    public void onPaymentException(String error, String message) {
        binding.tvStatus.setText("ERROR");
        binding.tvDate.setText(error);
        binding.tvDateTitle.setText("error");
        binding.tvReferenceNumber.setText(message);
        binding.tvReferenceNumberTitle.setText("message");
        binding.tvTotal.setText("");
        binding.tvTax.setText("");
        binding.tvSubtotal.setText("");
        binding.tvFee.setText("");
        binding.tvNetAmount.setText("");
        binding.tvMetadata1.setText("");
        binding.tvMetadata2.setText("");
        binding.tvEmail.setText("");
        binding.tvName.setText("");
        binding.tvDailyTransactionID.setText("");
        binding.tvPhoneNumber.setText("");
    }
}