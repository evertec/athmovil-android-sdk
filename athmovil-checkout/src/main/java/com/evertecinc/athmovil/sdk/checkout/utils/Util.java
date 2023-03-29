package com.evertecinc.athmovil.sdk.checkout.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentResultFlag;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil.DATE_PATTERN;

public class Util {

    @VisibleForTesting
    public static Date getDateFormat(String newDate) {
        Date date = new Date();
        if (!TextUtils.isEmpty(newDate)) {
            DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
            try {
                if(newDate.matches("[0-9]+")) {
                    Long dateMillis = Long.decode(newDate);
                    date.setTime(dateMillis);
                } else {
                    date = dateFormat.parse(date.toString());
                }
            } catch (ParseException e) {
                Log.e("Error", "Date Parse Exception: " + e.getMessage());
            }
        }
        return date;
    }

    public static ATHMPayment trimData(ATHMPayment payment) {
        payment.setMetadata1(payment.getMetadata1() != null ? payment.getMetadata1().trim() : null);
        payment.setMetadata2(payment.getMetadata2() != null ? payment.getMetadata2().trim() : null);
        payment.setPaymentId(payment.getPaymentId().trim());
        for (int i = 0; i < payment.getItems().size(); i++) {
            if (payment.getItems().get(i).getMetadata() != null) {
                payment.getItems().get(i).setMetadata(payment.getItems().get(i).getMetadata()
                        != null ? payment.getItems().get(i).getMetadata().trim() : null);
            }
            if (payment.getItems().get(i).getName() != null) {
                payment.getItems().get(i).setName(payment.getItems().get(i).getName()
                        != null ? payment.getItems().get(i).getName().trim() : null);
            }
            if (payment.getItems().get(i).getDesc() != null) {
                payment.getItems().get(i).setDesc(payment.getItems().get(i).getDesc()
                        != null ? payment.getItems().get(i).getDesc().trim() : null);
            }
        }
        PaymentResultFlag.getApplicationInstance().setPaymentRequest(payment);
        return payment;
    }

    public static void setPrefsString(final String key, final String value, final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(ConstantUtil.CHECKOUT_PREFS_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getPrefsString(final String key ,final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(ConstantUtil.CHECKOUT_PREFS_KEY, Context.MODE_PRIVATE);
        String savedValue = !TextUtils.isEmpty(prefs.getString(key, null)) ? prefs.getString(key, null) : "";
        return savedValue;
    }
}
