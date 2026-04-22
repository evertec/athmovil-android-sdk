package com.evertecinc.athmovil.sdk;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.evertecinc.athmovil.sdk.checkout.OpenATHM;
import com.evertecinc.athmovil.sdk.checkout.PayButton;
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.evertecinc.athmovil.sdk.databinding.ActivityCartBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


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
        initView();
    }

    public void initView(){
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

        binding.ivBack.setOnClickListener(v -> finish());
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
        hideLoader();
    }

    private void setUpItems() {
        Serializable serializable = getIntent().getSerializableExtra("items");

        if (serializable instanceof ArrayList<?>) {
            items = (ArrayList<Items>) serializable;
        }
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

        buildType = "";
    }

    private void sendData() {
        String token = Utils.getPrefsString(Constants.PUBLIC_TOKEN_PREF_KEY, this);

        payment.setPublicToken(token);
        payment.setItems(items);
        payment.setPhoneNumber(Utils.getPrefsString(Constants.PHONE_NUMBER_PREF_KEY, this));

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

        //For Evertec Test Only
        setBuildType(Utils.getPrefsString(Constants.BUILD_TYPE_PREF_KEY, this));
        payment.setBuildType(buildType);

        makePayment(payment, this);
    }

    private double parseAmount(String value) {
        if (TextUtils.isEmpty(value)) return 0.0;
        value = value.trim();
        try {
            return NumberFormat.getInstance(Locale.US).parse(value).doubleValue();
        } catch (ParseException e) {
            try {
                return NumberFormat.getInstance(new Locale("es", "ES")).parse(value).doubleValue();
            } catch (ParseException ex) {
                return 0.0;
            }
        }
    }

    public void sendAmounts(){
        String subtotal = Utils.getPrefsString(Constants.SUBTOTAL_PREF_KEY, this);
        payment.setSubtotal(parseAmount(subtotal));

        String tax = Utils.getPrefsString(Constants.TAX_PREF_KEY, this);
        payment.setTax(parseAmount(tax));

        String amount = Utils.getPrefsString(Constants.PAYMENT_AMOUNT_PREF_KEY, this);
        payment.setTotal(parseAmount(amount));
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