package com.evertecinc.athmovil.sdk.checkout.objects.payment;

import com.google.gson.annotations.SerializedName;

public class FindPaymentRequest {

    @SerializedName("ecommerceId")
    public String ecommerceId;

    @SerializedName("publicToken")
    public String pubToken;

    public FindPaymentRequest(String ecommerceId, String pubToken) {
        this.ecommerceId = ecommerceId;
        this.pubToken = pubToken;
    }
}