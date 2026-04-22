package com.evertecinc.athmovil.sdk.checkout;

import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.evertecinc.athmovil.sdk.checkout.utils.Util;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PaymentResponseUT {
    @InjectMocks
    private PaymentResponse paymentResponse;
    @Mock
    private PaymentResponseListener listener;
    private Gson gson = new Gson();

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        paymentResponse = Mockito.spy(new PaymentResponse());
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void WhenValidatingPaymentResponse_GivenCancelledPaymentResponse_ThenReturnOnCancelledPaymentData() {
        PaymentReturnedData result = gson.fromJson(setCancelledPaymentResponse(), PaymentReturnedData.class);
        PaymentResponse.validatePaymentResponse(result, listener, null);
        verify(listener, only()).onCancelledPayment(Util.getDateFormat(result.getDate()), result);
    }

    @Test
    public void WhenValidatingPaymentResponse_GivenExpiredPaymentResponse_ThenReturnOnExpiredPaymentData() {
        PaymentReturnedData result = gson.fromJson(setExpiredPaymentResponse(), PaymentReturnedData.class);
        PaymentResponse.validatePaymentResponse(result, listener, null);
        verify(listener, only()).onExpiredPayment(Util.getDateFormat(result.getDate()), result);
    }

    @Test
    public void WhenValidatingPaymentResponse_GivenCompletedPaymentResponse_ThenReturnOnCompletedPaymentData() {
        PaymentReturnedData result = gson.fromJson(setCompletedPaymentResponse(), PaymentReturnedData.class);
        PaymentResponse.validatePaymentResponse(result, listener, null);
        verify(listener, only()).onCompletedPayment(Util.getDateFormat(result.getDate()), result);
    }

    @Test
    public void WhenValidatingPaymentResponse_GivenBadPaymentResponse_ThenReturnOnErrorData() {
        PaymentResponse.decodeJSON(setExceptionError(), listener);
        verify(listener, only()).onPaymentException(ConstantUtil.ExceptionsLogs.RESPONSE_EXCEPTION_TITLE, ConstantUtil.ExceptionsLogs.DECODE_JSON_LOG_MESSAGE);
    }

    private String setCancelledPaymentResponse() {
        return "{\"status\":\"CancelledPayment\",\"total\":1.12,\"tax\":0,\"subtotal\":0," +
                "\"name\":\"Test\",\"phoneNumber\":7871234567,\"email\":\"test@test.com\"," +
                "\"date\":\"Wed Mar 14 15:30:00 EET 2018\",\"dailyTransactionID\":0000," +
                "\"metadata1\":\"Milk\",\"metadata2\":\"Shake 2\",\"items\":[]}";
    }

    private String setExpiredPaymentResponse() {
        return "{\"status\":\"ExpiredPayment\",\"total\":1.12,\"tax\":0,\"subtotal\":0," +
                "\"name\":\"Test\",\"phoneNumber\":7871234567,\"email\":\"test@test.com\"," +
                "\"date\":\"Wed Mar 14 15:30:00 EET 2018\",\"dailyTransactionID\":0000," +
                "\"metadata1\":\"Milk\",\"metadata2\":\"Shake 2\",\"items\":[]}";
    }

    private String setCompletedPaymentResponse() {
        return "{\"status\":\"CompletedPayment\",\"total\":1.12,\"tax\":0,\"subtotal\":0," +
                "\"name\":\"Test\",\"phoneNumber\":7871234567,\"email\":\"test@test.com\"," +
                "\"date\":\"Wed Mar 14 15:30:00 EET 2018\",\"dailyTransactionID\":0000," +
                "\"metadata1\":\"Milk\",\"metadata2\":\"Shake 2\",\"items\":[]}";
    }

    private String setExceptionError() {
        return "{[]}";
    }
}