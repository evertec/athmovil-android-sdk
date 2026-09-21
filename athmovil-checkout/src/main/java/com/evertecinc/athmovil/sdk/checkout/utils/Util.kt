package com.evertecinc.athmovil.sdk.checkout.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.core.content.edit
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Util {

    @JvmStatic
    internal fun getDateFormat(newDate: String?): Date {
        var date = Date()
        if (!TextUtils.isEmpty(newDate)) {
            val dateFormat: DateFormat = SimpleDateFormat(ConstantUtil.ExceptionsLogs.DATE_PATTERN, Locale.US)
            try {
                date = if (newDate!!.matches(Regex("[0-9]+"))) {
                    val dateMillis = newDate.toLong()
                    Date(dateMillis)
                } else {
                    dateFormat.parse(newDate) ?: date
                }
            } catch (e: ParseException) {
                Log.e(ConstantUtil.ExceptionsLogs.LOG_TAG, "Date Parse Exception: ${e.message}")
            } catch (e: NumberFormatException) {
                Log.e(ConstantUtil.ExceptionsLogs.LOG_TAG, "Date Parse Exception: ${e.message}")
            }
        }
        return date
    }

    @JvmStatic
    internal fun setPrefsString(key: String, value: String, context: Context) {
        val prefs = context.getSharedPreferences(ConstantUtil.BasicData.CHECKOUT_PREFS_KEY, Context.MODE_PRIVATE)
        prefs.edit { putString(key, value) }
    }

    @JvmStatic
    internal fun getPrefsString(key: String, context: Context): String {
        val prefs = context.getSharedPreferences(ConstantUtil.BasicData.CHECKOUT_PREFS_KEY, Context.MODE_PRIVATE)
        return prefs.getString(key, null).orEmpty()
    }
}

