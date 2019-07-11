package com.evertecinc.athmovil.sdk.checkout;

import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.Gson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PaymentResponseUT {
    @InjectMocks
    private PaymentResponse paymentResponse;
    @Mock
    private PaymentResponseListener listener;
    private Gson gson = new Gson();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);// required for the "@Mock" annotations
        paymentResponse = Mockito.spy(new PaymentResponse());
    }

    @Test
    public void WhenValidatingPaymentResponse_GivenCancelledPaymentResponse_ThenReturnOnCancelledPaymentData() {
        PaymentReturnedData result = gson.fromJson(setCancelledPaymentResponse(), PaymentReturnedData.class);
        PaymentResponse.validatePaymentResponse(result, listener);
        verify(listener, only()).onCancelledPayment(result.getReferenceNumber(), result.getTotal(),
                result.getTax(), result.getSubtotal(), result.getMetadata1(),
                result.getMetadata2(), result.getItemsSelectedList());
    }

    @Test
    public void WhenValidatingPaymentResponse_GivenExpiredPaymentResponse_ThenReturnOnExpiredPaymentData() {
        PaymentReturnedData result = gson.fromJson(setExpiredPaymentResponse(), PaymentReturnedData.class);
        PaymentResponse.validatePaymentResponse(result, listener);
        verify(listener, only()).onExpiredPayment(result.getReferenceNumber(), result.getTotal(),
                result.getTax(), result.getSubtotal(), result.getMetadata1(),
                result.getMetadata2(), result.getItemsSelectedList());
    }

    @Test
    public void WhenValidatingPaymentResponse_GivenCompletedPaymentResponse_ThenReturnOnCompletedPaymentData() {
        PaymentReturnedData result = gson.fromJson(setCompletedPaymentResponse(), PaymentReturnedData.class);
        PaymentResponse.validatePaymentResponse(result, listener);
        verify(listener, only()).onCompletedPayment(result.getReferenceNumber(), result.getTotal(),
                result.getTax(), result.getSubtotal(), result.getMetadata1(),
                result.getMetadata2(), result.getItemsSelectedList());
    }

    private String setCancelledPaymentResponse(){
        return  "{\"status\":\"CancelledPayment\",\"total\":1.12,\"tax\":0,\"subtotal\":0,\"metadata1\":\"Milk\",\"metadata2\":\"Shake 2\",\"items\":[]}";
    }

    private String setExpiredPaymentResponse(){
        return  "{\"status\":\"ExpiredPayment\",\"total\":1.12,\"tax\":0,\"subtotal\":0,\"metadata1\":\"Milk\",\"metadata2\":\"Shake 2\",\"items\":[]}";
    }

    private String setCompletedPaymentResponse(){
        return  "{\"status\":\"CompletedPayment\",\"referenceNumber\":1234-abcd,\"total\":1.12,\"tax\":0.12,\"subtotal\":1.00,\"metadata1\":\"Milk\",\"metadata2\":\"Shake 2\",\"items\":[]}";
    }
}