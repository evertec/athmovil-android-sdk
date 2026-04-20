package com.evertecinc.athmovil.sdk.checkout.utils;

import android.text.TextUtils;
import android.util.Log;

import com.evertecinc.athmovil.sdk.checkout.BuildConfig;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 */
public class JsonUtil {
    public static String toJsonAnyObject(Object obj){
        try{
            Gson gson = new Gson();
            return gson.toJson(obj);
        }catch(JsonIOException e){
            return "";
        }
    }
    public static String toJson(ATHMPayment ATHMPayment) {
        try {
            JSONObject json = new JSONObject();
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_PUBLIC_TOKEN_KEY, ATHMPayment.getPublicToken());
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_SUBTOTAL__KEY, String.valueOf(ATHMPayment.getSubtotal()));
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_TAX_KEY, String.valueOf(ATHMPayment.getTax()));
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_TOTAL_KEY, String.valueOf(ATHMPayment.getTotal()));
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_SCHEMA_KEY, ATHMPayment.getCallbackSchema());
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_METADATA_1, ATHMPayment.getMetadata1());
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_METADATA_2, ATHMPayment.getMetadata2());
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_PAYMENT_I_D_KEY, ATHMPayment.getPaymentId());
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ECOMMERCE_I_D, ATHMPayment.getEcommerceId());
            if(!TextUtils.isEmpty(ATHMPayment.getPhoneNumber())){
                json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_PHONE, ATHMPayment.getPhoneNumber());
            }

            JSONArray jsonArray = itemsToJson(ATHMPayment.getItems());
            json.put("itemsSelectedList", jsonArray);
            return json.toString();
        } catch (JSONException jsonError) {
            logForDebug(jsonError.getMessage());
            return null;
        }
    }

    //For dummy responses
    public static String returnedJson(PaymentReturnedData paymentInfo) {
        try {
            JSONObject json = new JSONObject();
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_STATUS_KEY, paymentInfo.getStatus());
            json.put(ConstantUtil.BasicData.REFERENCE_NUMBER_KEY, paymentInfo.getReferenceNumber());
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_TOTAL_KEY, paymentInfo.getTotal());
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_SUBTOTAL_KEY, paymentInfo.getSubtotal());
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_TAX_KEY, paymentInfo.getTax());
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_METADATA1_KEY, paymentInfo.getMetadata1());
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_METADATA2_KEY, paymentInfo.getMetadata2());
            json.put(ConstantUtil.ReturnedJson.RETURNED_JSON_PAYMENT_I_D_KEY, paymentInfo.getPaymentId());
            JSONArray jsonArray = itemsToJson(paymentInfo.getItemsSelectedList());
            json.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ITEM_LIST_KEY, jsonArray);
            return json.toString();
        } catch (JSONException jsonError) {
            logForDebug(jsonError.getMessage());
            return null;
        }
    }

    public static JSONArray itemsToJson(ArrayList<Items> itemsList){
        try {
            JSONArray jsonArray = new JSONArray();
            for (Items items : itemsList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ITEM_NAME_KEY, items.getName());
                jsonObject.put(ConstantUtil.ReturnedJson.RETURNED_JSON_ITEM_DESCRIPTION_KEY, String.valueOf(items.getDesc()));
                jsonObject.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ITEM_PRICE_KEY, String.valueOf(items.getPrice()));
                jsonObject.put(ConstantUtil.PaymentObject.PAYMENT_JSON_ITEM_QUANTITY_KEY, String.valueOf(items.getQuantity()));
                jsonObject.put(ConstantUtil.PaymentObject.PAYMENT_JASON_ITEM_METADATA_KEY, items.getMetadata());
                jsonArray.put(jsonObject);
            }
            return jsonArray;
        }
        catch (JSONException jsonError) {
            logForDebug(jsonError.getMessage());
            return null;
        }
    }

    private static void logForDebug(String message) {
        if (BuildConfig.DEBUG) {
            String m = message;
            if (message == null) {
                m = "No message found for this error";
            }

            Log.e("JSON Convert Error", m);
        }
    }
}
