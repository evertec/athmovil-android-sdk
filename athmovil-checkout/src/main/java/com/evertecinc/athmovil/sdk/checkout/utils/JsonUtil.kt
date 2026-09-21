package com.evertecinc.athmovil.sdk.checkout.utils

import android.util.Log
import com.evertecinc.athmovil.sdk.checkout.BuildConfig
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment
import com.evertecinc.athmovil.sdk.checkout.objects.Items
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData
import com.google.gson.Gson
import com.google.gson.JsonIOException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object JsonUtil {

    @JvmStatic
    fun toJsonAnyObject(obj: Any): String {
        return try {
            Gson().toJson(obj)
        } catch (_: JsonIOException) {
            ""
        }
    }

    @JvmStatic
    fun toJson(payment: ATHMPayment): String? {
        return try {
            val json = JSONObject()
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_PUBLIC_TOKEN_KEY, payment.publicToken)
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_SUBTOTAL__KEY, payment.subtotal.toString())
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_TAX_KEY, payment.tax.toString())
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_TOTAL_KEY, payment.total.toString())
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_SCHEMA_KEY, payment.callbackSchema)
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_METADATA_1, payment.metadata1)
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_METADATA_2, payment.metadata2)
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_PAYMENT_I_D_KEY, payment.paymentId)
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ECOMMERCE_I_D, payment.ecommerceId)
            if (!payment.phoneNumber.isNullOrBlank()) {
                json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_PHONE, payment.phoneNumber)
            }

            val jsonArray = itemsToJson(payment.items)
            json.put(ConstantUtil.JsonKeys.ITEMS_SELECTED_LIST, jsonArray)
            json.toString()
        } catch (jsonError: JSONException) {
            logForDebug(jsonError.message)
            null
        }
    }

    @JvmStatic
    fun returnedJson(paymentInfo: PaymentReturnedData): String? {
        return try {
            val json = JSONObject()
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_STATUS_KEY, paymentInfo.status)
            json.put(ConstantUtil.BasicData.REFERENCE_NUMBER_KEY, paymentInfo.referenceNumber)
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_TOTAL_KEY, paymentInfo.total)
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_SUBTOTAL_KEY, paymentInfo.subtotal)
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_TAX_KEY, paymentInfo.tax)
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_METADATA1_KEY, paymentInfo.metadata1)
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_METADATA2_KEY, paymentInfo.metadata2)
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_PAYMENT_I_D_KEY, paymentInfo.paymentId)
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_SDK_VERSION_KEY, paymentInfo.sdkVersion)
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ECOMMERCE_I_D, paymentInfo.ecommerceId)
            val jsonArray = itemsToJson(paymentInfo.items)
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ITEM_LIST_KEY, jsonArray)
            json.toString()
        } catch (jsonError: JSONException) {
            logForDebug(jsonError.message)
            null
        }
    }

    @JvmStatic
    fun itemsToJson(itemsList: ArrayList<Items>?): JSONArray? {
        return try {
            val jsonArray = JSONArray()
            itemsList?.forEach { items ->
                val jsonObject = JSONObject()
                jsonObject.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ITEM_NAME_KEY, items.name)
                jsonObject.put(ConstantUtil.ReturnedJson.RETURNED_JSON_ITEM_DESCRIPTION_KEY, items.desc.orEmpty())
                jsonObject.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ITEM_PRICE_KEY, items.price.toString())
                jsonObject.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ITEM_QUANTITY_KEY, items.quantity.toString())
                jsonObject.put(ConstantUtil.PaymentObject.PAYMENT_JASON_ITEM_METADATA_KEY, items.metadata)
                jsonArray.put(jsonObject)
            }
            jsonArray
        } catch (jsonError: JSONException) {
            logForDebug(jsonError.message)
            null
        }
    }

    private fun logForDebug(message: String?) {
        if (BuildConfig.DEBUG) {
            val safeMessage = message ?: "No message found for this error"
            Log.e("JSON Convert Error", safeMessage)
        }
    }
}

