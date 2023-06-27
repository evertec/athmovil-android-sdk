package com.evertecinc.athmovil.sdk.checkout.objects.payment;

import java.io.Serializable;

public class PaymentResponseObject implements Serializable {
    public String status;
    public Ecommerce data;
    public String message;
    public String errorcode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Ecommerce getData() {
        return data;
    }

    public void setData(Ecommerce data) {
        this.data = data;
    }
}
