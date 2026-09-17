package com.evertecinc.athmovil.sdk

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import com.evertecinc.athmovil.sdk.Constants.CHECKOUT_DEMO_PREFS_KEY
import com.evertecinc.athmovil.sdk.checkout.objects.Items
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.util.ArrayList
import java.util.Locale

object Utils {

    @JvmStatic
    fun setPrefsString(key: String, value: String?, context: Context) {
        try {
            val prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.apply()
        } catch (e: Exception) {
            when (e) {
                is ClassCastException, is NullPointerException -> Log.e(TAG, "setPrefsString: $e")
                else -> throw e
            }
        }
    }

    @JvmStatic
    fun getPrefsString(key: String, context: Context): String {
        var savedValue = ""
        try {
            val prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE)
            savedValue = prefs.getString(key, null) ?: ""
        } catch (e: Exception) {
            when (e) {
                is ClassCastException, is NullPointerException -> Log.e(TAG, "getPrefsString: $e")
                else -> throw e
            }
        }
        return savedValue
    }

    @JvmStatic
    fun setPrefsBoolean(key: String, value: Boolean, context: Context) {
        val prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    @JvmStatic
    fun getPrefsBoolean(key: String, context: Context): Boolean {
        val prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, true)
    }

    @JvmStatic
    fun setPrefsInt(key: String, value: Int, context: Context) {
        val prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    @JvmStatic
    fun getPrefsInt(key: String, context: Context): Int {
        val prefs = context.getSharedPreferences(CHECKOUT_DEMO_PREFS_KEY, Context.MODE_PRIVATE)
        return prefs.getInt(key, 0)
    }

    @JvmStatic
    fun getBalanceString(balance: String?): String {
        val zeroBalance = 0.0f
        try {
            if (balance != null && !TextUtils.isEmpty(balance)) {
                val fBalance = balance.toFloat()
                if (fBalance >= zeroBalance) {
                    return NumberFormat.getCurrencyInstance(Locale.US).format(fBalance.toDouble())
                }
            }
        } catch (utilsError: Exception) {
            when (utilsError) {
                is NumberFormatException, is NullPointerException -> Log.w("BalanceFormatException", utilsError)
                else -> throw utilsError
            }
        }
        return "N/A"
    }

    @JvmStatic
    fun decodeJSON(itemList: String?): ArrayList<Items>? {
        val gson = Gson()
        return try {
            val type = object : TypeToken<List<Items>>() {}.type
            gson.fromJson<ArrayList<Items>>(itemList, type)
        } catch (jsonError: Exception) {
            when (jsonError) {
                is JsonSyntaxException, is NullPointerException -> {
                    Log.e("JSON Convert Error", jsonError.message ?: "")
                    null
                }
                else -> throw jsonError
            }
        }
    }

    class CurrencyTextWatcher : TextWatcher {
        private var mEditing = false

        override fun afterTextChanged(s: Editable) {
            if (!mEditing) {
                mEditing = true
                val digits = s.toString().replace("\\D".toRegex(), "")
                val nf = NumberFormat.getCurrencyInstance()
                try {
                    if (!s.toString().endsWith("-")) {
                        val formatted = nf.format(digits.toDouble() / 100).replace("\\$".toRegex(), "")
                        if (s.toString().startsWith("-")) {
                            s.replace(0, s.length, "-$formatted")
                        } else {
                            s.replace(0, s.length, formatted)
                        }
                    } else {
                        if (!s.toString().startsWith("-")) {
                            s.replace(0, s.length, "-" + s.subSequence(0, s.length - 1))
                        } else {
                            if (s.toString() != "-") {
                                s.replace(0, s.length, s.subSequence(1, s.length - 1))
                            }
                        }
                    }
                } catch (nfe: NumberFormatException) {
                    s.clear()
                }
                mEditing = false
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }
}
