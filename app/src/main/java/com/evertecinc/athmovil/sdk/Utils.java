package com.evertecinc.athmovil.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.evertecinc.athmovil.sdk.Constants.CHECKOUT_DEMO_PREFS_KEY;

public class Utils {

    public static void setPrefsString(final String key, final String value, final Context context) {
        try {
            final SharedPreferences prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e){
            Log.e(TAG, "setPrefsString: " + e);
        }
    }

    public static String getPrefsString(final String key ,final Context context) {
        String savedValue = "";
        try {
            final SharedPreferences prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE);
            savedValue = prefs.getString(key, null);
        } catch (Exception e){
            Log.e(TAG, "getPrefsString: " + e);
        }
        return savedValue;
    }

    public static void setPrefsBoolean(final String key, final boolean value, final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getPrefsBoolean(final String key, final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, true);
    }

    public static void setPrefsInt(final String key, final int value, final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getPrefsInt(final String key, final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE);
        return prefs.getInt(key, 0);
    }

    public static String getBalanceString(String balance) {
        Float zeroBalance = 0.0f;
        try {
            if (balance != null && !TextUtils.isEmpty(balance)) {
                Float fBalance = Float.parseFloat(balance);
                if (fBalance.compareTo(zeroBalance) >= 0) {
                    return NumberFormat.getCurrencyInstance(Locale.US).format(fBalance);
                }
            }
        } catch (final Exception utilsError) {
            Log.w("BalanceFormatException", utilsError);
        }

        return "N/A";
    }

    public static ArrayList<Items> decodeJSON(String itemList) {
        Gson gson = new Gson();
        ArrayList<Items> items;
        try {
            items = gson.fromJson(itemList, new TypeToken<List<Items>>(){}.getType());
        }catch (Exception jsonError){
            Log.e("JSON Convert Error", jsonError.getMessage());
            return null;
        }
        return items;
    }

    public static class CurrencyTextWatcher implements TextWatcher {

        boolean mEditing;
        private CharSequence s;
        private int start;
        private int before;
        private int count;
        private int after;

        public CurrencyTextWatcher() {
            mEditing = false;
        }

        public synchronized void afterTextChanged(Editable s) {
            if (!mEditing) {
                mEditing = true;
                String digits = s.toString().replaceAll("\\D", "");
                NumberFormat nf = NumberFormat.getCurrencyInstance();
                try {
                    if (!s.toString().endsWith("-")) {
                        String formatted = nf.format(Double.parseDouble(digits) / 100).replaceAll("\\$", "");
                        if (s.toString().startsWith("-")) {
                            s.replace(0, s.length(), "-" + formatted);
                        } else {
                            s.replace(0, s.length(), formatted);
                        }
                    } else {
                        if (!s.toString().startsWith("-")) {
                            s.replace(0, s.length(), "-" + s.subSequence(0, s.length() - 1));
                        } else {
                            if (!s.toString().equals("-")) {
                                s.replace(0, s.length(), s.subSequence(1, s.length() - 1));
                            }
                        }
                    }
                } catch (NumberFormatException nfe) {
                    s.clear();
                }

                mEditing = false;
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            this.s = s;
            this.start = start;
            this.count = count;
            this.after = after;
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            this.s = s;
            this.start = start;
            this.before = before;
            this.count = count;
        }

    }
}