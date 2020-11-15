package com.evertecinc.athmovil.sdk;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.evertecinc.athmovil.sdk.checkout.*;
import com.evertecinc.athmovil.sdk.checkout.objects.*;

import java.util.ArrayList;

public class CheckoutCartExampleActivity extends AppCompatActivity {

    String buildTypeSelected = null;
    EditText timeEditText, buildType, language, theme, token;
    CheckBox cbWithList, cbWithTax, cbWithSubtotal;
    PayButton btnATHMCheckout;
    ArrayList<Items> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_example);

        timeEditText = findViewById(R.id.et_time);
        cbWithList = findViewById(R.id.cbWithList);
        cbWithTax = findViewById(R.id.cbWithTax);
        cbWithSubtotal = findViewById(R.id.cbWithSubtotal);
        btnATHMCheckout =  findViewById(R.id.btnATHMCheckout);
        buildType = findViewById(R.id.etSelectBuildType);
        language = findViewById(R.id.etSelectLanguage);
        theme = findViewById(R.id.etSelectTheme);
        token = findViewById(R.id.etPublicToken);

        //Setting up items
        for (int i = 0; i < 2; i++) {
            items.add(new Items("Ssd", "(8oz)        ", 1.0, 2L, null));
            items.add(new Items("Coca Cola", "(68oz)", 0.75, 1L, "expiration 0820"));
        }
        setUpBuildTypeSelection();
        setUpButtonLanguageSelection();
        setUpButtonThemeSelection();
    }

    // Production functionality, provide correct data...
    public void onClickPayButton(View view) {
        ATHMPayment payment = new ATHMPayment(this);
        payment.setPublicToken("933OH0Y08SFD7QEN2QJH6YD4RSKZZS7YAL0IUY0F");
        payment.setTotal(1.12);
        payment.setMetadata1("test");
        payment.setMetadata2("test      ");
        payment.setCallbackSchema("ATHMSDK");

        if (!TextUtils.isEmpty(token.getText())){
            payment.setPublicToken(token.getText().toString());
        }

        if (cbWithList.isChecked()) {
            payment.setItems(items);
        }

        if (cbWithSubtotal.isChecked()) {
            payment.setSubtotal(0);
        }

        if (cbWithTax.isChecked()) {
            payment.setTax(0.12);
        }

        String timeInSeconds = timeEditText.getText().toString();
        if (!timeInSeconds.isEmpty()) {
            long timeout = Long.parseLong(timeInSeconds);
            payment.setTimeout(timeout);
        }

        //For Evertec Test Only
        //TODO: Remove for production
        if (buildTypeSelected == null) {
            Toast.makeText(this, "Select a build type to continue.", Toast.LENGTH_LONG).show();
        } else {
            payment.setBuildType(buildTypeSelected);
            sendData(payment);
        }
    }

    private void sendData(ATHMPayment ATHMPayment) {
        try {
            OpenATHM.validateData(ATHMPayment);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //For Evertec Test Only
    //TODO: Remove for production
    private void setUpBuildTypeSelection(){
        buildType.setFocusable(false);
        buildType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu buildTypeSelector = new PopupMenu(CheckoutCartExampleActivity.this, buildType);
                buildTypeSelector.getMenuInflater().inflate(R.menu.build_type_filter, buildTypeSelector.getMenu());
                buildTypeSelector.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString().equalsIgnoreCase("Development")) {
                            buildTypeSelected = ".debug";
                            buildType.setText(item.getTitle().toString());
                        } else if (item.getTitle().toString().equalsIgnoreCase("Quality")) {
                            buildTypeSelected = ".qa";
                            buildType.setText(item.getTitle().toString());
                        } else if (item.getTitle().toString().equalsIgnoreCase("Piloto")) {
                            buildTypeSelected = ".piloto";
                            buildType.setText(item.getTitle().toString());
                        } else if (item.getTitle().toString().equalsIgnoreCase("Produccion")) {
                            buildTypeSelected = "";
                            buildType.setText(item.getTitle().toString());
                        }
                        return false;
                    }
                });

                buildTypeSelector.show();
            }
        });
    }

    private void setUpButtonLanguageSelection(){
        language.setFocusable(false);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu languageSelector = new PopupMenu(CheckoutCartExampleActivity.this, language);
                languageSelector.getMenuInflater().inflate(R.menu.language_filter, languageSelector.getMenu());
                languageSelector.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getTitle().toString()){
                            case "English":
                                btnATHMCheckout.setLanguage(PayButton.ButtonLanguage.EN);
                                break;
                            case "Spanish":
                                btnATHMCheckout.setLanguage(PayButton.ButtonLanguage.ES);
                                break;
                            default:
                                btnATHMCheckout.setLanguage(PayButton.ButtonLanguage.DEFAULT);
                                break;
                        }
                        language.setText(item.getTitle().toString());
                        return false;
                    }
                });

                languageSelector.show();
            }
        });
    }

    private void setUpButtonThemeSelection(){
        theme.setFocusable(false);
        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu themeSelector = new PopupMenu(CheckoutCartExampleActivity.this, theme);
                themeSelector.getMenuInflater().inflate(R.menu.theme_filter, themeSelector.getMenu());
                themeSelector.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getTitle().toString()){
                            case "Dark":
                                btnATHMCheckout.setTheme(PayButton.ButtonTheme.DARK);
                                break;
                            case "Light":
                                btnATHMCheckout.setTheme(PayButton.ButtonTheme.LIGHT);
                                break;
                            default:
                                btnATHMCheckout.setTheme(PayButton.ButtonTheme.ORIGINAL);
                                break;
                        }
                        theme.setText(item.getTitle().toString());
                        return false;
                    }
                });

                themeSelector.show();
            }
        });
    }
}
