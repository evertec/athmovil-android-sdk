package com.evertecinc.athmovil.sdk.checkout.objects;

import android.app.Application;

public class PaymentResultFlag extends Application {

    public PaymentResultFlag() {
        super();
    }

    private static PaymentResultFlag applicationInstance = new PaymentResultFlag();

    public static PaymentResultFlag getApplicationInstance() {
        return applicationInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;
    }

    private ATHMPayment paymentRequest = null;

    public static void setApplicationInstance(PaymentResultFlag applicationInstance) {
        PaymentResultFlag.applicationInstance = applicationInstance;
    }

    public ATHMPayment getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(ATHMPayment paymentRequest) {
        this.paymentRequest = paymentRequest;
    }
}

