package com.evertecinc.athmovil.sdk.checkout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener;
import com.evertecinc.athmovil.sdk.checkout.exceptions.InvalidPaymentRequestException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.JsonEncoderException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullApplicationContextException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullATHMPaymentObjectException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.VerifyPaymentException;
import com.evertecinc.athmovil.sdk.checkout.interfaces.PostService;
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentResultFlag;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;
import com.evertecinc.athmovil.sdk.checkout.objects.payment.AuthorizationResponse;
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentErrorResponse;
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentRequest;
import com.evertecinc.athmovil.sdk.checkout.objects.payment.PaymentResponseObject;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.evertecinc.athmovil.sdk.checkout.utils.ExceptionUtil;
import com.evertecinc.athmovil.sdk.checkout.utils.JsonUtil;
import com.evertecinc.athmovil.sdk.checkout.utils.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil.COM_EVERTEC_ATHMOVIL_ANDROID;

/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 * Main class for the athmovil-checkout library.
 */
public class OpenATHM {

    //TODO: For Evertec Test Only
    private static String buildType = "";
    static PostService postsService;
    static ProgressDialog nDialog;


    /**
     * Method that the developer will use to pass the payment data to the library.
     *
     * @param ATHMPayment - Object containing the payment data
     */
    public static void validateData(@NonNull ATHMPayment ATHMPayment, @NonNull Context context) {

        if (ATHMPayment.getNewFlow()){
            Util.setPrefsString(ConstantUtil.IS_NEW_FLOW, "yes", context);
            Util.setPrefsString(ConstantUtil.PUBLIC_TOK, ATHMPayment.getPublicToken(), context);
            if(ATHMPayment.getPublicToken().equalsIgnoreCase("dummy")){
                sendData(ATHMPayment, context);
            }else{
                paymentServices(ATHMPayment, context);
            }
        } else {
            Util.setPrefsString(ConstantUtil.IS_NEW_FLOW, "no", context);
            sendData(ATHMPayment, context);
        }

    }

    public static void sendData(@NonNull ATHMPayment ATHMPayment, @NonNull Context context){
        PaymentResultFlag.getApplicationInstance().setPaymentRequest(ATHMPayment);
        try {
            validateATHMPayment(ATHMPayment);
            buildType = ATHMPayment.getBuildType();
            defineTimeout(ATHMPayment);
            defineResponse(ATHMPayment);
        } catch (Exception e) {
            showResults(context, null, ATHMPayment.getCallbackSchema(), e);
        }
    }

    /**
     * Validating token received to define the action to take.
     *
     * @param ATHMPayment - Object containing the payment data
     */
    public static void defineResponse(ATHMPayment ATHMPayment) throws JsonEncoderException {
        if (ATHMPayment.getPublicToken().equalsIgnoreCase(ConstantUtil.TOKEN_FOR_SUCCESS)) {

            definePaymentReturnedData(ATHMPayment, ConstantUtil.STATUS_SUCCESS, ConstantUtil.REFERENCE_NUMBER,
                    ATHMPayment.getTotal(), ATHMPayment.getTax(), ATHMPayment.getSubtotal(),
                    ATHMPayment.getMetadata1(), ATHMPayment.getMetadata2(), ATHMPayment.getPaymentId(),
                    ATHMPayment.getItems());

        } else if (ATHMPayment.getPublicToken().equalsIgnoreCase(ConstantUtil.TOKEN_FOR_FAILURE)) {

            definePaymentReturnedData(ATHMPayment, ConstantUtil.STATUS_CANCELLED, null,
                    ATHMPayment.getTotal(), ATHMPayment.getTax(), ATHMPayment.getSubtotal(),
                    ATHMPayment.getMetadata1(), ATHMPayment.getMetadata2(), ATHMPayment.getPaymentId(),
                    ATHMPayment.getItems());
        } else {
            String businessInfoJson = JsonUtil.toJson(ATHMPayment);
            if (businessInfoJson != null) {
                logForDebug(businessInfoJson);
                execute(ATHMPayment.getContext(), businessInfoJson, ATHMPayment.getTimeout());
            } else {
                logForDebug(ConstantUtil.ENCODE_JSON_LOG_MESSAGE);
                {
                    throw new JsonEncoderException(ConstantUtil.ENCODE_JSON_LOG_MESSAGE);
                }
            }
        }
    }

    /**
     * Helper method to create json with dummy data.
     *
     * @param ATHMPayment - contains the data received
     * @param status      - dummy payment status (success, cancelled, timeout...)
     */
    private static void definePaymentReturnedData(ATHMPayment ATHMPayment, String status,
                                                  String referenceNumber, double total, double tax,
                                                  double subtotal, String metadata1,
                                                  String metadata2, String paymentId, ArrayList<Items> items) {
        PaymentReturnedData paymentReturnedData = new PaymentReturnedData();
        paymentReturnedData.setStatus(status.toUpperCase());
        paymentReturnedData.setReferenceNumber(referenceNumber);
        paymentReturnedData.setTotal(total);
        paymentReturnedData.setTax(tax);
        paymentReturnedData.setSubtotal(subtotal);
        paymentReturnedData.setMetadata1(metadata1);
        paymentReturnedData.setMetadata2(metadata2);
        paymentReturnedData.setDailyTransactionID("0004");
        paymentReturnedData.setName("None");
        paymentReturnedData.setPhoneNumber("8888888888");
        paymentReturnedData.setEmail("test@test.com");
        paymentReturnedData.setFee(0.0);
        paymentReturnedData.setNetAmount(0.0);
        paymentReturnedData.setDate(new Date().toString());
        paymentReturnedData.setPaymentId(paymentId);
        paymentReturnedData.setItems(items);

        final String jsonResponse = JsonUtil.returnedJson(paymentReturnedData);
        showResults(ATHMPayment.getContext(), jsonResponse, ATHMPayment.getCallbackSchema(), null);
    }

    /**
     * Validating that the entered time is >= 1 minute or  <= 10 minutes
     *
     * @param ATHMPayment - Object containing the data to validatePaymentResponse
     */
    private static void defineTimeout(ATHMPayment ATHMPayment) {
        if(!ATHMPayment.getNewFlow()){
            if (ATHMPayment.getTimeout() <= 0) {
                ATHMPayment.setTimeout(ConstantUtil.MAX_TIMEOUT_SECONDS);
            } else if (ATHMPayment.getTimeout() < ConstantUtil.MIN_TIMEOUT_SECONDS) {
                ATHMPayment.setTimeout(ConstantUtil.MIN_TIMEOUT_SECONDS);
            } else if (ATHMPayment.getTimeout() > ConstantUtil.MAX_TIMEOUT_SECONDS) {
                ATHMPayment.setTimeout(ConstantUtil.MAX_TIMEOUT_SECONDS);
            }
        }
    }

    /**
     * Validating the information received in ATHMPayment Object
     *
     * @param ATHMPayment - Object containing the data to validatePaymentResponse
     * @throws NullATHMPaymentObjectException  - if "ATHMPayment" is null
     * @throws NullApplicationContextException - if null is passed to "setContext" argument
     * @throws InvalidPaymentRequestException  - if any value of the request is invalid
     */
    private static void validateATHMPayment(ATHMPayment ATHMPayment)
            throws NullATHMPaymentObjectException, NullApplicationContextException, InvalidPaymentRequestException {
        ExceptionUtil exceptionUtil = new ExceptionUtil();
        if (ATHMPayment == null) {
            logForDebug(ConstantUtil.NULL_ATHMPAYMENT_LOG_MESSAGE);
            throw new NullATHMPaymentObjectException(ConstantUtil.NULL_ATHMPAYMENT_LOG_MESSAGE);
        }
        if (!exceptionUtil.validateRequest(ATHMPayment)) {
            logForDebug(exceptionUtil.getExceptionMessage());
            throw new InvalidPaymentRequestException(exceptionUtil.getExceptionMessage());
        }
        if (ATHMPayment.getContext() == null) {
            logForDebug(ConstantUtil.NULL_CONTEXT_LOG_MESSAGE);
            throw new NullApplicationContextException(ConstantUtil.NULL_CONTEXT_LOG_MESSAGE);
        }
    }

    /**
     * Method that looks for the ATH Móvil app on the device and get the version code. If app is not
     * installed or the version code is not the required code then it would launch the play store.
     * If the app is found and the version code is correct then the ATH Móvil app would be launch.
     *
     * @param context - required context to access package information.
     * @param json    - string containing the payment data to be send to ATH Móvil.
     * @param timeout - time in seconds that the ATH Móvil app would have to complete payment.
     */
    private static void execute(Context context, String json, long timeout) {
        timeout = (timeout * 1000);
        PackageInfo athmInfo;
        int athmVersionCode = 0;

        if(buildType.equalsIgnoreCase(".qa_dev")){
            buildType = ".qa";
        }
        String athmBundleId = COM_EVERTEC_ATHMOVIL_ANDROID + buildType;

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(athmBundleId);
        try {
            athmInfo = context.getPackageManager().getPackageInfo(athmBundleId, 0);
            athmVersionCode = athmInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            logForDebug(e.getMessage());
        }
        if (intent == null || athmVersionCode <= ConstantUtil.ATH_MOVIL_REQUIRED_VERSION_CODE) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(ConstantUtil.ATH_MOVIL_MARKET_URL));
        }
        intent.putExtra((ConstantUtil.BUNDLE), context.getPackageName());
        intent.putExtra(ConstantUtil.JSON_DATA_KEY, json);
        intent.putExtra(ConstantUtil.PAYMENT_DURATION_TIME_KEY, timeout);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Local response for payment validation call and tests tokens.
     *
     * @param context - application context used to start activity.
     * @param json    - string with the response values to show.
     */
    private static void showResults(Context context, String json, String callbackSchema, Exception exception) {
        String appId = context.getPackageName() + "." + callbackSchema;
        Intent intent = new Intent(appId);
        intent.putExtra(ConstantUtil.RETURNED_JSON_KEY, json);
        if (exception != null) {
            intent.putExtra(ConstantUtil.RETURNED_JSON_KEY, ConstantUtil.EXCEPTION);
            intent.putExtra(ConstantUtil.EXCEPTION_CAUSE, ConstantUtil.REQUEST_EXCEPTION_TITLE);
            if (exception.getMessage().equalsIgnoreCase(ConstantUtil.PAYMENT_VALIDATION_FAILED)) {
                intent.putExtra(ConstantUtil.EXCEPTION_CAUSE, ConstantUtil.RESPONSE_EXCEPTION_TITLE);
            }
            intent.putExtra(ConstantUtil.EXCEPTION, exception.getMessage());
        }
        context.startActivity(intent);
    }

    /**
     * Helper method for log errors on console only when in debug mode.
     *
     * @param message - error message received as string.
     */
    private static void logForDebug(String message) {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug")) {
            Log.d(ConstantUtil.LOG_TAG, message);
        }
    }

    /**
     * Call ATH Móvil service to verify transaction status in case ATH Móvil app does not return it.
     *
     * @param context - Application context
     */
    public static void verifyPaymentStatus(Context context) {
        String isNewFlow = Util.getPrefsString(ConstantUtil.IS_NEW_FLOW, context);
        if(!TextUtils.isEmpty(isNewFlow) && isNewFlow.equalsIgnoreCase("yes")){
            return;
        }
        ATHMPayment payment = PaymentResultFlag.getApplicationInstance().getPaymentRequest();
        if (payment == null) {
            return;
        }

        //if its the dummy case it will return always cancelled status
        if (payment.getPublicToken().equalsIgnoreCase("dummy")) {
            showResults(context, ConstantUtil.STATUS_CANCELLED, payment.getCallbackSchema(),
                    null);
            return;
        }
        //TODO: Find a better implementation or remove development URL.
        String url = ConstantUtil.PRODUCTION_URL;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
        postsService = retrofit.create(PostService.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("publicToken", payment.getPublicToken());
        jsonObject.addProperty("paymentID", payment.getPaymentId());
        retrofit2.Call<JsonObject> call = postsService.sendPost(jsonObject);

        showLoading(context);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                // pass response to the example activity
                hideLoading();
                if (response.isSuccessful() && response.body() != null &&
                        !response.body().toString().contains("errorCode")) {

                    String paymentResponse = response.body().toString();
                    PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
                    showResults(context, paymentResponse, payment.getCallbackSchema(), null);
                } else {
                    showResults(context, ConstantUtil.STATUS_CANCELLED, payment.getCallbackSchema(),
                            null);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                hideLoading();
                showResults(context, ConstantUtil.STATUS_CANCELLED, payment.getCallbackSchema(),
                        new VerifyPaymentException(ConstantUtil.PAYMENT_VALIDATION_FAILED));
                logForDebug(t.getMessage());
            }
        });
    }

    /**
     * Call ATH Móvil service to verify transaction status in case ATH Móvil app does not return it.
     *
     */
    public static void paymentServices(ATHMPayment payment, Context context) {

        String url = ConstantUtil.AWS_URL_PAYMENT_PRO;

        Retrofit retrofit = retrofitInstance("https://"+url);
        postsService = retrofit.create(PostService.class);

        showLoading(context);

        Call<PaymentResponseObject> call = postsService.paymentPost(url, setObjectPaymentRequest(payment));

        call.enqueue(new Callback<PaymentResponseObject>() {
            @Override
            public void onResponse(@NonNull Call<PaymentResponseObject> call,
                                   @NonNull Response<PaymentResponseObject> response) {
                hideLoading();
                try {
                    String requestJson = JsonUtil.toJsonAnyObject(response.body());
                    logForDebug(requestJson);

                    if (response.isSuccessful() && response.body() != null
                            && !response.body().toString().contains("errorcode")) {

                        logForDebug(JsonUtil.toJsonAnyObject(response.body()));
                        String ecommerce = response.body().data.getEcommerceId();
                        String auth_token = response.body().data.getAuth_token();

                        if (!TextUtils.isEmpty(ecommerce) && !TextUtils.isEmpty(auth_token)) {
                            Util.setPrefsString(ConstantUtil.TOKEN_AUTH_KEY, auth_token, context);
                            Util.setPrefsString(ConstantUtil.ECOMMERCE_I_D_KEY, ecommerce, context);
                            payment.setEcommerceId(ecommerce);
                            sendData(payment, context);
                            return;
                        }
                    }

                    if (response.errorBody() != null) {
                        Gson gson = new GsonBuilder().create();
                        PaymentErrorResponse mError;
                        mError = gson.fromJson(response.errorBody().string(), PaymentErrorResponse.class);
                        verified_error(context, mError);
                        return;
                    }

                } catch (IOException | JsonSyntaxException e) {
                    logForDebug(e.getMessage());
                }

                getAlert(context, context.getString(R.string.payment_error_alert_title), context.getString(R.string.payment_error_alert_message));
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                hideLoading();
                logForDebug(t.getMessage());
                getAlert(context, context.getString(R.string.payment_error_alert_title), context.getString(R.string.payment_error_alert_message));
            }
        });
    }

    private static void verified_error(Context context, PaymentErrorResponse mError){
        logForDebug(mError.getMessage());
        if(mError == null){
            getAlert(context, context.getString(R.string.payment_error_alert_title), context.getString(R.string.payment_error_alert_message));
            return;
        }

        switch (mError.getErrorcode()){
            case "BCUS_0092":
                getAlert(context, context.getString(R.string.business_token_expired_title), context.getString(R.string.business_token_expired_message));
                break;
            default:
                getAlert(context, context.getString(R.string.payment_error_alert_title), context.getString(R.string.payment_error_alert_message));
                break;
        }
    }

    private static Retrofit retrofitInstance(String baseUrl){
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }

    private static PaymentRequest setObjectPaymentRequest(ATHMPayment payment){
        PaymentRequest objRequest = new PaymentRequest();
        objRequest.setPublicToken(!TextUtils.isEmpty(payment.getPublicToken()) ? payment.getPublicToken() : "");
        objRequest.setEnv(!TextUtils.isEmpty(payment.getBuildType()) ? payment.getBuildType() : "");

        objRequest.setTimeout(payment.getTimeout());
        objRequest.setTotal(payment.getTotal() != 0.0 ? payment.getTotal() : 0);
        objRequest.setTax(payment.getTax() != 0.0 ? payment.getTax() : 0);
        objRequest.setSubtotal(payment.getSubtotal() != 0.0 ? payment.getSubtotal() : 0);

        objRequest.setMetadata1(!TextUtils.isEmpty(payment.getMetadata1()) ? payment.getMetadata1() : "");
        objRequest.setMetadata2(!TextUtils.isEmpty(payment.getMetadata2()) ? payment.getMetadata2() : "");
        objRequest.setPhoneNumber(!TextUtils.isEmpty(payment.getPhoneNumber()) ? payment.getPhoneNumber() : "");
        objRequest.setEcommerceId(!TextUtils.isEmpty(payment.getEcommerceId()) ? payment.getEcommerceId() : "");
        objRequest.setItems(payment.getItems() != null ? payment.getItems() : null);

        String requestJson = JsonUtil.toJsonAnyObject(objRequest);
        logForDebug(requestJson);
        return objRequest;
    }

    public static void authorizationServices(@NonNull PaymentResponseListener interATH,
                                             @NonNull Context context, @NonNull Intent intent) {
        showLoading(context);
        ATHMPayment payment = PaymentResultFlag.getApplicationInstance().getPaymentRequest();
        if (payment == null) {
            hideLoading();
            return;
        }

        String token = Util.getPrefsString(ConstantUtil.TOKEN_AUTH_KEY, context);
        if (TextUtils.isEmpty(token)) {
            hideLoading();
            return;
        }

        String url = ConstantUtil.AWS_URL_PAYMENT_PRO;

        Retrofit retrofit = retrofitInstance("https://"+url);
        postsService = retrofit.create(PostService.class);

        Call<AuthorizationResponse> call = postsService.autorizationPost(url,"Bearer "+token);

        call.enqueue(new Callback<AuthorizationResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthorizationResponse> call,
                                   @NonNull Response<AuthorizationResponse> response) {
                hideLoading();

                if(response.body() != null){
                    logForDebug(JsonUtil.toJsonAnyObject(response.body()));
                    PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
                    PaymentResponse.validateDataResponse(intent, interATH, response.body());
                }else{
                    AuthorizationResponse aut = new AuthorizationResponse();
                    aut.setStatus("Error");
                    PaymentResponse.validateDataResponse(intent, interATH, aut);
                }

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                hideLoading();
                logForDebug(t.getMessage());
                PaymentResultFlag.getApplicationInstance().setPaymentRequest(null);
                AuthorizationResponse aut = new AuthorizationResponse();
                aut.setStatus("Error");
                PaymentResponse.validateDataResponse(intent, interATH, aut);
            }
        });
    }

    public static void getAlert(Context context,String title, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }


    /**
     * Manage certificates
     * TODO: Remove certificate bypass for production
     */
    private static OkHttpClient getHttpClient() {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            //Handling connection timeout fo prod
            builder.connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS);

            return builder.build();
        } catch (Exception e) {
            return new OkHttpClient.Builder().build();
        }
    }

    public static void showLoading(Context context){
        nDialog = new ProgressDialog(context);
        nDialog.setMessage(context.getString(R.string.loading));
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
        nDialog.show();
    }

    public static void hideLoading(){
        if(nDialog.isShowing()){
            nDialog.hide();
        }
    }
}