package com.evertecinc.athmovil.sdk.checkout.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evertecinc.athmovil.sdk.checkout.BuildConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class NewRelicConfig {

    public static final String NR_CONSTANT = ConstantUtil.NR_VARIABLE;

    public static final String URL_CONSTANT = ConstantUtil.NR_URL;


    public static void sendEventToNewRelic(@Nullable String eventType, @Nullable String paymentReference,
                                           @Nullable String payment_status, @Nullable String merchantAppId,
                                           @Nullable String buildType) {

        String AB123 = NR_CONSTANT.replaceAll(ConstantUtil.RVARIBALES, "");
        String AB124 = URL_CONSTANT.replaceAll(ConstantUtil.RVARIBALES, "");
        buildType = (buildType == null || buildType.isEmpty()) ? "PROD" : buildType;

        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("payment_reference", paymentReference);
        event.put("payment_status", payment_status);
        event.put("merchant_app_id", merchantAppId);
        event.put("build_type", buildType);
        event.put("timestamp", System.currentTimeMillis());
        event.put("sdk_version", ConstantUtil.SDK_VERSION);
        event.put("sdk_platform", ConstantUtil.SDK_PLATFORM);
        event.put("device_os_version", Build.VERSION.RELEASE);
        event.put("device_os_model", Build.MODEL);

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(event);

        RequestBody body = RequestBody.create(
                Objects.requireNonNull(MediaType.parse("application/json")),
                jsonPayload
        );

        Request request = new Request.Builder()
                .url(AB124)
                .addHeader("X-Insert-Key", AB123)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                logForDebug("New Relic Event API payment_status: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
                try (response) {
                    if (response.isSuccessful()) {
                        logForDebug("Event sent to New Relic successfully");
                    } else {
                        logForDebug("New Relic API response payment_status: " + response.code());
                    }
                }
            }
        });
    }

    private static void logForDebug(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(ConstantUtil.LOG_TAG, message);
        }
    }

}
