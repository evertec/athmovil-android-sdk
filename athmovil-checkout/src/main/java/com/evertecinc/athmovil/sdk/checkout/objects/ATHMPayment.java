package com.evertecinc.athmovil.sdk.checkout.objects;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

public class ATHMPayment {
    private Context context;
    private String publicToken;
    private double subtotal;
    private double tax;
    private double total;
    private String callbackSchema;
    private String metaData1;
    private String metaData2;
    private String paymentId;
    private long timeout = 600;
    private ArrayList<Items> items = new ArrayList<>();

    public ArrayList<Items> getItems() {
        return new ArrayList<>(items);
    }

    public ATHMPayment(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long seconds) {
        this.timeout = seconds;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = new ArrayList<>();
        if (items != null) {
            this.items.addAll(items);
        }
    }

    public String getCallbackSchema() {
        return callbackSchema;
    }

    public void setCallbackSchema(String callbackSchema) {
        this.callbackSchema = callbackSchema;
    }

    public String getMetadata1() {
        return metaData1;
    }

    public void setMetadata1(String metaData1) {
        this.metaData1 = metaData1;
    }

    public String getMetadata2() {
        return metaData2;
    }

    public void setMetadata2(String metaData2) {
        this.metaData2 = metaData2;
    }

    public String getPaymentId() {
        if (paymentId == null) {
            paymentId = UUID.randomUUID().toString();
        }
        return paymentId;
    }

    public void setPaymentId() {
        paymentId = UUID.randomUUID().toString();
    }

    //For Evertec Test Only
    private String buildType;

    public String getBuildType() {
        if (buildType == null) {
            return "";
        }
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }
}

