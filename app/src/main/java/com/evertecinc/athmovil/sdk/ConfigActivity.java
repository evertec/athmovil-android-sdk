package com.evertecinc.athmovil.sdk;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.text.TextUtils;

import com.evertecinc.athmovil.sdk.databinding.ActivityConfigBinding;

import static android.view.Gravity.END;

public class ConfigActivity extends AppCompatActivity implements
        CustomDialog.DialogResponseListener {

    ActivityConfigBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_config);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);

        setUpInitialValues();
        setUpButtonThemeSelection();
        setUpBuildTypeSelection();
        setOnClickListeners();
    }

    private void setUpInitialValues() {
        String savedPublicToken = Utils.getPrefsString(
                Constants.PUBLIC_TOKEN_PREF_KEY, this);
        String publicToken = savedPublicToken != null ? savedPublicToken :
                "dummy";
        binding.tvPublicToken.setText(publicToken);

        setupConfigs();
        setupAmounts();
        setUpMetadata();
    }

    private void setupConfigs() {
        int savedTimeout = Utils.getPrefsInt(Constants.TIMEOUT_PREF_KEY, this);
        int timeout = savedTimeout >= 0 ? savedTimeout : 0;
        binding.tvTimeout.setText(TextUtils.concat(String.valueOf(timeout), "s"));

        String savedTheme = Utils.getPrefsString(Constants.THEME_PREF_KEY, this);
        String theme = savedTheme != null ? savedTheme : "Original";
        binding.tvTheme.setText(theme);

        String savedBuildType = Utils.getPrefsString(Constants.BUILD_TYPE_PREF_KEY, this);
        String buildType = savedBuildType != null ? savedBuildType : getString(R.string.production);
        binding.tvBuildType.setText(buildType);
    }

    private void setupAmounts() {
        String savedPaymentAmount = Utils.getPrefsString(
                Constants.PAYMENT_AMOUNT_PREF_KEY, this);
        String paymentAmount = savedPaymentAmount != null ? savedPaymentAmount : "0.00";
        binding.tvPaymentAmount.setText(TextUtils.concat("$", paymentAmount));

        String savedSubtotal = Utils.getPrefsString(Constants.SUBTOTAL_PREF_KEY, this);
        String subtotal = savedSubtotal != null ? savedSubtotal : "0.00";
        binding.tvSubtotal.setText(TextUtils.concat("$", subtotal));

        String savedTax = Utils.getPrefsString(Constants.TAX_PREF_KEY, this);
        String tax = savedTax != null ? savedTax : "0.00";
        binding.tvTax.setText(TextUtils.concat("$", tax));
    }

    private void setUpMetadata() {
        String savedMetadata1 = Utils.getPrefsString(Constants.METADATA1_PREF_KEY, this);
        binding.tvMetadata1.setText(savedMetadata1);

        String savedMetadata2 = Utils.getPrefsString(Constants.METADATA2_PREF_KEY, this);
        binding.tvMetadata2.setText(savedMetadata2);
    }

    private void setOnClickListeners() {
        binding.ivClose.setOnClickListener(v -> onBackPressed());

        binding.llPublicTokenContainer.setOnClickListener(v -> CustomDialog.show(this,
                getString(R.string.public_token_alert_title),
                getString(R.string.public_token), getString(R.string.public_token_alert_message),
                Constants.RequestId.PUBLIC_TOKEN));

        binding.llTimeoutContainer.setOnClickListener(v -> CustomDialog.show(this,
                getString(R.string.timeout_alert_title),
                getString(R.string.timeout), getString(R.string.timeout_alert_message),
                Constants.RequestId.TIMEOUT));

        binding.llPaymentAmountContainer.setOnClickListener(v -> CustomDialog.show(this,
                getString(R.string.payment_alert_title),
                getString(R.string.payment_amount), getString(R.string.payment_alert_message),
                Constants.RequestId.PAYMENT_AMOUNT));

        binding.llSubtotalContainer.setOnClickListener(v -> CustomDialog.show(this,
                getString(R.string.subtotal_alert_title),
                getString(R.string.subtotal), getString(R.string.subtotal_alert_message),
                Constants.RequestId.SUBTOTAL));

        binding.llTaxContainer.setOnClickListener(v -> {
            CustomDialog.show(this, getString(R.string.tax_alert_title),
                    getString(R.string.tax), getString(R.string.tax_alert_message),
                    Constants.RequestId.TAX);
        });

        binding.llMetadata1Container.setOnClickListener(v -> {
            CustomDialog.show(this, getString(R.string.metadata1_alert_title),
                    getString(R.string.metadata_1), getString(R.string.metadata1_alert_message),
                    Constants.RequestId.METADATA1);
        });
        binding.llMetadata2Container.setOnClickListener(v -> {
            CustomDialog.show(this, getString(R.string.metadata2_alert_title),
                    getString(R.string.metadata_2), getString(R.string.metadata2_alert_message),
                    Constants.RequestId.METADATA2);
        });
    }

    @Override
    public void onDialogResponse(String data, Constants.RequestId id) {
        switch (id) {
            case PUBLIC_TOKEN:
                binding.tvPublicToken.setText(data);
                Utils.setPrefsString(Constants.PUBLIC_TOKEN_PREF_KEY, data, this);
                break;
            case TIMEOUT:
                binding.tvTimeout.setText(data);
                Utils.setPrefsInt(Constants.TIMEOUT_PREF_KEY, Integer.parseInt(data), this);
                break;
            case PAYMENT_AMOUNT:
                binding.tvPaymentAmount.setText(TextUtils.concat("$", data));
                Utils.setPrefsString(Constants.PAYMENT_AMOUNT_PREF_KEY, data, this);
                break;
            case SUBTOTAL:
                binding.tvSubtotal.setText(TextUtils.concat("$", data));
                Utils.setPrefsString(Constants.SUBTOTAL_PREF_KEY, data, this);
                break;
            case TAX:
                binding.tvTax.setText(TextUtils.concat("$", data));
                Utils.setPrefsString(Constants.TAX_PREF_KEY, data, this);
                break;
            case METADATA1:
                binding.tvMetadata1.setText(data.isEmpty() ? null : data);
                Utils.setPrefsString(Constants.METADATA1_PREF_KEY, data.isEmpty() ? null :
                        data, this);
                break;
            case METADATA2:
                binding.tvMetadata2.setText(data.isEmpty() ? null : data);
                Utils.setPrefsString(Constants.METADATA2_PREF_KEY, data.isEmpty() ? null :
                        data, this);
                break;
            default:
                break;
        }
    }

    private void setUpButtonThemeSelection() {
        binding.llThemeContainer.setOnClickListener(view -> {
            final PopupMenu themeSelector = new PopupMenu(this, binding.llThemeContainer);
            themeSelector.getMenuInflater().inflate(R.menu.theme_filter, themeSelector.getMenu());
            themeSelector.setGravity(END);
            themeSelector.setOnMenuItemClickListener(item -> {
                binding.tvTheme.setText(item.getTitle().toString());
                Utils.setPrefsString(Constants.THEME_PREF_KEY,
                        item.getTitle().toString(), this);
                return false;
            });
            themeSelector.show();
        });
    }

    private void setUpBuildTypeSelection() {
        binding.llBuildTypeContainer.setOnClickListener(view -> {
            final PopupMenu buildType = new PopupMenu(this, binding.llBuildTypeContainer);
            buildType.getMenuInflater().inflate(R.menu.build_type_filter, buildType.getMenu());
            buildType.setGravity(END);
            buildType.setOnMenuItemClickListener(item -> {
                binding.tvBuildType.setText(item.getTitle().toString());
                Utils.setPrefsString(Constants.BUILD_TYPE_PREF_KEY,
                        item.getTitle().toString(), this);
                return false;
            });
            buildType.show();
        });
    }
}
