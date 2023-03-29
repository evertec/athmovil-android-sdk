package com.evertecinc.athmovil.sdk.checkout.objects.payment;

import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentRequest {

    @SerializedName("env")
    public String env;

    @SerializedName("publicToken")
    public String pubToken;

    @SerializedName("timeout")
    public long timeout;

    @SerializedName("total")
    public Double total;

    @SerializedName("tax")
    public Double tax;

    @SerializedName("subtotal")
    public Double subtotal;

    @SerializedName("metadata1")
    public String metadata1;

    @SerializedName("metadata2")
    public String metadata2;

    @SerializedName("items")
    public List<Items> items;

    @SerializedName("phoneNumber")
    public String phoNumber;

    @SerializedName("ecommerceId")
    public String ecommerceId;

    public String getEcommerceId() {
        return ecommerceId;
    }

    public void setEcommerceId(String ecommerceId) {
        this.ecommerceId = ecommerceId;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getPublicToken() {
        return pubToken;
    }

    public void setPublicToken(String pubToken) {
        this.pubToken = pubToken;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public String getMetadata1() {
        return metadata1;
    }

    public void setMetadata1(String metadata1) {
        this.metadata1 = metadata1;
    }

    public String getMetadata2() {
        return metadata2;
    }

    public void setMetadata2(String metadata2) {
        this.metadata2 = metadata2;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public String getPhoneNumber() {
        return phoNumber;
    }

    public void setPhoneNumber(String phoNumber) {
        this.phoNumber = phoNumber;
    }

    public PaymentRequest(){}

    public PaymentRequest(String env, String publicToken, long timeout, Double total, Double tax, Double subtotal, String metadata1, String metadata2, List<Items> items, String phoNumber) {
        this.env = env;
        this.pubToken = publicToken;
        this.timeout = timeout;
        this.total = total;
        this.tax = tax;
        this.subtotal = subtotal;
        this.metadata1 = metadata1;
        this.metadata2 = metadata2;
        this.items = items;
        this.phoNumber = phoNumber;
    }
}
