package com.evertecinc.athmovil.sdk.checkout.interfaces;

import com.evertecinc.athmovil.sdk.checkout.objects.payment.AuthorizationResponse;
import com.evertecinc.athmovil.sdk.checkout.objects.payment.FindPaymentRequest;
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentRequest;
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentResponseObject;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PostService {
    @Headers({
            "Content-type: application/json"
    })
    @POST(ConstantUtil.BasicData.API_ROUTE)
    Call<JsonObject> sendPost(@Body JsonObject body);

    @POST(ConstantUtil.BasicData.API_ROUTE_PAYMENT_SERVICE)
    Call<PaymentResponseObject> paymentPost(@Header("Host") String host, @Body PaymentRequest body);

    @POST(ConstantUtil.BasicData.API_ROUTE_AUTORIZATION_SERVICE)
    Call<AuthorizationResponse> autorizationPost(@Header("Host") String host, @Header("Authorization") String token);

    @POST(ConstantUtil.BasicData.API_ROUTE_FINDPAYMENT_SERVICE)
    Call<AuthorizationResponse> findPaymentPost(@Header("Host") String host, @Header("Authorization") String token, @Body FindPaymentRequest body);
}
