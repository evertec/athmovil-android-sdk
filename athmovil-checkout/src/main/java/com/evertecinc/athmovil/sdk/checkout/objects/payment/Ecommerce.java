package com.evertecinc.athmovil.sdk.checkout.objects.payment;

import com.google.gson.annotations.SerializedName;

public class Ecommerce {

    @SerializedName("ecommerceId")
    public String ecommerceId;

    @SerializedName("auth_token")
    public String aut_token;

    public String getAuth_token() {
        return aut_token;
    }

    public void setAuth_token(String aut_token) {
        this.aut_token = aut_token;
    }

    public String getEcommerceId() {
        return ecommerceId;
    }

    public void setEcommerceId(String ecommerceId) {
        this.ecommerceId = ecommerceId;
    }
}
