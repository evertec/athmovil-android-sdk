package com.evertecinc.athmovil.sdk.checkout.utils;

import android.util.Log;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 */
public class JsonUtil {
    public static String toJson(ATHMPayment ATHMPayment) {
        try {
            JSONObject json = new JSONObject();
            json.put(ConstantUtil.PAYMENT_JSON_PUBLIC_TOKEN_KEY, ATHMPayment.getPublicToken());
            json.put(ConstantUtil.PAYMENT_JSON_SUBTOTAL__KEY, String.valueOf(ATHMPayment.getSubtotal()));
            json.put(ConstantUtil.PAYMENT_JSON_TAX_KEY, String.valueOf(ATHMPayment.getTax()));
            json.put(ConstantUtil.PAYMENT_JSON_TOTAL_KEY, String.valueOf(ATHMPayment.getTotal()));
            json.put(ConstantUtil.PAYMENT_JSON_SCHEMA_KEY, ATHMPayment.getCallbackSchema());
            json.put(ConstantUtil.PAYMENT_JSON_METADATA_1, ATHMPayment.getMetadata1());
            json.put(ConstantUtil.PAYMENT_JSON_METADATA_2, ATHMPayment.getMetadata2());
            json.put(ConstantUtil.PAYMENT_JSON_PAYMENT_ID_KEY, ATHMPayment.getPaymentId());
            JSONArray jsonArray = itemsToJson(ATHMPayment.getItems());
            json.put("itemsSelectedList", jsonArray);
            return json.toString().trim();
        } catch (Exception jsonError) {
            Log.e("JSON Convert Error", jsonError.getMessage());
            return null;
        }
    }

    //For dummy responses
    public static String returnedJson(PaymentReturnedData paymentInfo) {
        try {
            JSONObject json = new JSONObject();
            json.put(ConstantUtil.RETURNED_JSON_STATUS_KEY, paymentInfo.getStatus());
            json.put(ConstantUtil.REFERENCE_NUMBER_KEY, paymentInfo.getReferenceNumber());
            json.put(ConstantUtil.RETURNED_JSON_TOTAL_KEY, paymentInfo.getTotal());
            json.put(ConstantUtil.RETURNED_JSON_SUBTOTAL_KEY, paymentInfo.getSubtotal());
            json.put(ConstantUtil.RETURNED_JSON_TAX_KEY, paymentInfo.getTax());
            json.put(ConstantUtil.RETURNED_JSON_METADATA1_KEY,
                    (paymentInfo.getMetadata1() == null) ? "" : paymentInfo.getMetadata1());
            json.put(ConstantUtil.RETURNED_JSON_METADATA2_KEY,
                    (paymentInfo.getMetadata2() == null) ? "" : paymentInfo.getMetadata2());
            json.put(ConstantUtil.RETURNED_JSON_PAYMENT_ID_KEY, paymentInfo.getPaymentId());
            JSONArray jsonArray = itemsToJson(paymentInfo.getItemsSelectedList());
            json.put(ConstantUtil.PAYMENT_JSON_ITEM_LIST_KEY, jsonArray);
            return json.toString();
        } catch (Exception jsonError) {
            Log.e("JSON Convert Error", jsonError.getMessage());
            return null;
        }
    }

    public static JSONArray itemsToJson(ArrayList<Items> itemsList){
        try {
            JSONArray jsonArray = new JSONArray();
            for (Items items : itemsList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(ConstantUtil.PAYMENT_JSON_ITEM_NAME_KEY, items.getName());
                jsonObject.put(ConstantUtil.RETURNED_JSON_ITEM_DESCRIPTION_KEY,
                        (items.getDescription() == null) ? "" : items.getDescription());
                jsonObject.put(ConstantUtil.PAYMENT_JSON_ITEM_PRICE_KEY, String.valueOf(items.getPrice()));
                jsonObject.put(ConstantUtil.PAYMENT_JSON_ITEM_QUANTITY_KEY,
                        String.valueOf(items.getQuantity()));
                jsonObject.put(ConstantUtil.PAYMENT_JASON_ITEM_METADATA_KEY,
                        (items.getMetadata() == null) ? "" : items.getMetadata());
                jsonArray.put(jsonObject);
            }
            return jsonArray;
        }
        catch (Exception jsonError) {
            Log.e("JSON Convert Error", jsonError.getMessage());
            return null;
        }
    }
}
