package com.evertecinc.athmovil.sdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.evertecinc.athmovil.sdk.checkout.*;
import com.evertecinc.athmovil.sdk.checkout.exceptions.*;
import com.evertecinc.athmovil.sdk.checkout.objects.*;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ExampleActivity extends AppCompatActivity {

    List<ItemsSelected> itemsSelectedList = new ArrayList<>();
    TextView exampleTextView;
    String txt;
    EditText timeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        timeEditText = findViewById(R.id.et_time);

        //Setting up items
        for (int i = 0; i < 6; i++) {
            itemsSelectedList.add(new ItemsSelected("Cake", "(8oz)", "0.25", "2"));
            itemsSelectedList.add(new ItemsSelected("Coca Cola", "(68oz)", "0.75", "1"));
        }

        //Extracting response from intent extras
        exampleTextView = findViewById(R.id.exampleTextView);
        if (getIntent().getExtras() != null){
            String jsonResponseValue = getIntent().getExtras().getString("paymentResult");

            if(jsonResponseValue != null){
                Gson gson = new Gson();
                PurchaseReturnedData result = gson.fromJson(jsonResponseValue,PurchaseReturnedData.class);

                boolean isCompleted = result.getCompleted().equalsIgnoreCase("true");

                if (isCompleted){
                     txt = "Status: " + result.getStatus() + "\n" + "Cart Reference Id (" + result.getCartReferenceId() + "),\n" + "Daily transaction Id (" +
                            result.getDailyTransactionId() +"),\n" + "Transaction Reference Number (" + result.getTransactionReference() + ")\n";
                    exampleTextView.setText(txt);

                }else {
                    txt = "Status: " + result.getStatus() + "\n" + "Cart Reference Id (" + result.getCartReferenceId() + ")";
                    exampleTextView.setText(txt);
                }
            }
        }
    }
    // Production functionality, provide correct data...
    public void onClickPayButton(View view) {
        PurchaseData purchaseData = new PurchaseData();
        purchaseData.setContext(ExampleActivity.this);
        purchaseData.setBusinessToken("fb1f7ae2849a07da1545a89d997d8a435a5f21ac");
        purchaseData.setCartReferenceId("1002345-325410");
        purchaseData.setTotal("1.00");
        purchaseData.setItemsSelectedList(itemsSelectedList);

        if (!timeEditText.getText().toString().isEmpty()) {
            long timer =  Long.parseLong(timeEditText.getText().toString()) * 1000;
            purchaseData.setTimer(timer);
        }
        sendData(purchaseData);
    }

    //Use for dummy success response.
    public void onClickDarkPayButton(View view) {
        PurchaseData purchaseData = new PurchaseData();
        purchaseData.setContext(ExampleActivity.this);
        purchaseData.setBusinessToken(ConstantUtil.TOKEN_FOR_SUCCESS);
        purchaseData.setCartReferenceId("1002345-325410");
        purchaseData.setTotal("1.39");

        sendData(purchaseData);
    }

    //Use for dummy failure response.
    public void onClickLightPayButton(View view) {
        PurchaseData purchaseData = new PurchaseData();
        purchaseData.setContext(ExampleActivity.this);
        purchaseData.setBusinessToken(ConstantUtil.TOKEN_FOR_FAILURE);
        purchaseData.setCartReferenceId("1002345-325410");
        purchaseData.setTotal("1.39");

        sendData(purchaseData);
    }

    private void sendData(PurchaseData purchaseData){
        try {

            OpenATHM.validateData(purchaseData);

        } catch (NullPurchaseDataObjectException | NullApplicationContextException | InvalidBusinessTokenException |
                InvalidPurchaseTotalAmountException | NullCartReferenceIdException e) {

            e.printStackTrace();
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}