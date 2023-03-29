package com.evertecinc.athmovil.sdk.checkout.objects;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

public class ATHMPayment {
    private Context context;
    public String env;
    private String publicToken;
    private long timeout = 600;
    private double total;
    private double tax;
    private double subtotal;
    private String metaData1;
    private String metaData2;
    private String paymentId;
    private String phoneNumber;
    private Boolean isNewFlow = false;

    private ArrayList<Items> items =  new ArrayList<>();
    private String callbackSchema;
    private String ecommerceId;

    public String getMetaData1() {
        return metaData1;
    }

    public void setMetaData1(String metaData1) {
        this.metaData1 = metaData1;
    }

    public String getMetaData2() {
        return metaData2;
    }

    public void setMetaData2(String metaData2) {
        this.metaData2 = metaData2;
    }

    public String getEcommerceId() {
        return ecommerceId;
    }

    public void setEcommerceId(String ecommerceId) {
        this.ecommerceId = ecommerceId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<Items> getItems() {
        return new ArrayList<>(items);
    }

    public ATHMPayment(Context context){
        this.context = context;
    }

    public Context getContext() { return context; }

    public String getPublicToken() {return publicToken;}
    public void setPublicToken(String publicToken) {this.publicToken = publicToken;}

    public double getSubtotal() { return subtotal ; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal ; }

    public double getTax() {return tax;}
    public void setTax(double tax) { this.tax = tax ; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total ; }

    public long getTimeout() { return timeout ; }
    public void setTimeout(long seconds) { this.timeout = seconds ; }

    public void setItems(ArrayList<Items> items) {
        this.items = new ArrayList<>();
        if (items != null) {
            this.items.addAll(items);
        }
    }

    public String getCallbackSchema() { return callbackSchema; }
    public void setCallbackSchema(String callbackSchema) { this.callbackSchema = callbackSchema; }

    public String getMetadata1() { return metaData1; }
    public void setMetadata1(String metaData1) { this.metaData1 = metaData1; }

    public String getMetadata2() { return metaData2; }
    public void setMetadata2(String metaData2) { this.metaData2 = metaData2; }

    public String getPaymentId() {
        if(paymentId==null) {
            paymentId = UUID.randomUUID().toString();
        }
        return paymentId; }

    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Boolean getNewFlow() {
        if (isNewFlow == null) {
            return true;
        }
        return isNewFlow;
    }

    public void setNewFlow(Boolean newFlow) {
        isNewFlow = newFlow;
    }

    //For Evertec Test Only
    private String buildType;
    public String getBuildType() {
        if (buildType == null) {
            return "";
        }
        return buildType;
    }
    public void setBuildType(String buildType) { this.buildType = buildType; }
}

