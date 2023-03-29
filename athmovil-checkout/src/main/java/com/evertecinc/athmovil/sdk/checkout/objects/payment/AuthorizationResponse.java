package com.evertecinc.athmovil.sdk.checkout.objects.payment;

import java.io.Serializable;

public class AuthorizationResponse implements Serializable {
    String status;
    AutorizationObject data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AutorizationObject getData() {
        return data;
    }

    public void setData(AutorizationObject data) {
        this.data = data;
    }
}
