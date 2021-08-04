package com.evertecinc.athmovil.sdk;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.evertecinc.athmovil.sdk.checkout.OpenATHM;
import com.evertecinc.athmovil.sdk.checkout.PayButton;
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.evertecinc.athmovil.sdk.databinding.ActivityCartBinding;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    String paymentAmount;
    ArrayList<Items> items = new ArrayList<>();
    private String buildType;
    ATHMPayment payment = new ATHMPayment(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);

        String savedPaymentAmount = Utils.getPrefsString(Constants.PAYMENT_AMOUNT_PREF_KEY, this);
        String savedTax = Utils.getPrefsString(Constants.TAX_PREF_KEY, this);
        String savedSubtotal = Utils.getPrefsString(Constants.SUBTOTAL_PREF_KEY, this);
        paymentAmount = savedPaymentAmount != null ? savedPaymentAmount : "0.0";
        savedTax = savedTax != null ? savedTax : "0.0";
        savedSubtotal = savedSubtotal != null ? savedSubtotal : "0.0";
        binding.tvTotal.setText(TextUtils.concat("$", paymentAmount));
        binding.tvSubtotal.setText(TextUtils.concat("$", savedSubtotal));
        binding.tvTax.setText(TextUtils.concat("$", savedTax));

        setUpTheme(Utils.getPrefsString(Constants.THEME_PREF_KEY, this));
        setBuildType(Utils.getPrefsString(Constants.BUILD_TYPE_PREF_KEY, this));
        setUpItems();

        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.btnAthmCheckout.setOnClickListener(v ->  sendData());
    }

    private void setUpTheme(String savedTheme) {
        if (TextUtils.isEmpty(savedTheme)) {
            savedTheme = "Original";
        }
        switch (savedTheme) {
            case "Dark":
                binding.btnAthmCheckout.setTheme(PayButton.ButtonTheme.DARK);
                break;
            case "Light":
                binding.btnAthmCheckout.setTheme(PayButton.ButtonTheme.LIGHT);
                break;
            default:
                binding.btnAthmCheckout.setTheme(PayButton.ButtonTheme.ORIGINAL);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        showLoader();

        // Call to validate the transaction if flow was broken
        OpenATHM.verifyPaymentStatus(this);
        hideLoader();
    }

    private void setUpItems() {
        items = (ArrayList<Items>) getIntent().getExtras().getSerializable("items");
    }

    private void showLoader(){
        binding.btnAthmCheckout.getBackground().setAlpha(50);
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoader(){
        binding.loadingProgressBar.setVisibility(View.GONE);
        binding.btnAthmCheckout.getBackground().setAlpha(255);
    }

    private void setBuildType(String savedBuildType) {
        if (TextUtils.isEmpty(savedBuildType)) {
            savedBuildType = getString(R.string.production);
        }

        if (savedBuildType.equalsIgnoreCase(getString(R.string.development))) {
            buildType = ".debug";
        } else if (savedBuildType.equalsIgnoreCase(getString(R.string.pilot))) {
            buildType = ".piloto";
        } else if (savedBuildType.equalsIgnoreCase(getString(R.string.production))) {
            buildType = "";
        } else {
            buildType = ".qa";
        }
    }

    private void sendData() {
        String token = Utils.getPrefsString(Constants.PUBLIC_TOKEN_PREF_KEY, this);
        if (TextUtils.isEmpty(token)) {
            token = "dummy";
        }
        payment.setPublicToken(token);

        if (Utils.getPrefsBoolean(Constants.ITEMS_BOOL_PREF_KEY, this)) {
            payment.setItems(items);
        }

        sendAmounts();
        sendMetadata();
        sendConfigs();
    }

    public void sendMetadata(){
        String metadata1 = Utils.getPrefsString(Constants.METADATA1_PREF_KEY, this);
        if (TextUtils.isEmpty(metadata1)) {
            metadata1 = "";
        }
        payment.setMetadata1(metadata1);

        String metadata2 = Utils.getPrefsString(Constants.METADATA2_PREF_KEY, this);
        if (TextUtils.isEmpty(metadata2)) {
            metadata2 = "";
        }
        payment.setMetadata2(metadata2);
    }

    public void sendConfigs(){
        long timeout = Utils.getPrefsInt(Constants.TIMEOUT_PREF_KEY, this);
        payment.setTimeout(timeout);

        //Need the Schema without the app bundle.
        payment.setCallbackSchema("ATHMSDK");

        makePayment(payment, this);
    }

    public void sendAmounts(){
        String subtotal = Utils.getPrefsString(Constants.SUBTOTAL_PREF_KEY, this);
        if (TextUtils.isEmpty(subtotal)) {
            subtotal = "0";
        }
        subtotal = subtotal.replaceAll(",", "");
        payment.setSubtotal(Double.parseDouble(subtotal));

        String tax = Utils.getPrefsString(Constants.TAX_PREF_KEY, this);
        if (TextUtils.isEmpty(tax)) {
            tax = "0";
        }
        tax = tax.replaceAll(",", "");
        payment.setTax(Double.parseDouble(tax));

        String amount = Utils.getPrefsString(Constants.PAYMENT_AMOUNT_PREF_KEY, this);
        if (TextUtils.isEmpty(amount)) {
            amount = "0";
        }
        amount = amount.replaceAll(",", "");
        payment.setTotal(Double.parseDouble(amount));
    }

    /**
     * Method to make a payment with ATHM SDK
     *
     * @param payment - object that contains all the data needed to do a payment
     * @param context - application context
     */
    private void makePayment(ATHMPayment payment, Context context){
        OpenATHM.validateData(payment, context);
    }
}