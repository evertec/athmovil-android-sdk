package com.evertecinc.athmovil.sdk.checkout.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.VisibleForTesting;

public class Util {

    @VisibleForTesting
    public static Date getDateFormat(String newDate) {
        Date date = new Date();
        if (!TextUtils.isEmpty(newDate)) {
            DateFormat dateFormat = new SimpleDateFormat(ConstantUtil.ExceptionsLogs.DATE_PATTERN, Locale.US);
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

    public static void setPrefsString(final String key, final String value, final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(ConstantUtil.BasicData.CHECKOUT_PREFS_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getPrefsString(final String key ,final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(ConstantUtil.BasicData.CHECKOUT_PREFS_KEY, Context.MODE_PRIVATE);
        String savedValue = !TextUtils.isEmpty(prefs.getString(key, null)) ? prefs.getString(key, null) : "";
        return savedValue;
    }
}
