package com.evertecinc.athmovil.sdk.checkout.utils;

import android.util.Log;
import com.evertecinc.athmovil.sdk.checkout.objects.ItemsSelected;
import com.evertecinc.athmovil.sdk.checkout.objects.PurchaseData;
import com.evertecinc.athmovil.sdk.checkout.objects.PurchaseReturnedData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 */
public class JsonUtil {
    public static String toJson(PurchaseData purchaseData) {
        try {
            JSONObject json = new JSONObject();
            json.put(ConstantUtil.PURCHASE_JSON_BUSINESS_TOKEN_KEY, purchaseData.getBusinessToken());
            json.put(ConstantUtil.PURCHASE_JSON_SUBTOTAL__KEY, purchaseData.getSubtotal());
            json.put(ConstantUtil.PURCHASE_JSON_TAX_KEY, purchaseData.getTax());
            json.put(ConstantUtil.PURCHASE_JSON_TOTAL_KEY, purchaseData.getTotal());

            JSONArray jsonArray = new JSONArray();

            for(ItemsSelected itemsSelected: purchaseData.getItemsSelectedList()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(ConstantUtil.PURCHASE_JSON_ITEM_NAME_KEY, itemsSelected.getName());
                jsonObject.put(ConstantUtil.PURCHASE_JSON_ITEM_DESCRIPTION_KEY,
                        itemsSelected.getDescription());
                jsonObject.put(ConstantUtil.PURCHASE_JSON_ITEM_PRICE_KEY, itemsSelected.getPrice());
                jsonObject.put(ConstantUtil.PURCHASE_JSON_ITEM_QUANTITY_KEY,
                        itemsSelected.getQuantity());
                jsonArray.put(jsonObject);
            }
            json.put(ConstantUtil.PURCHASE_JSON_ITEM_LIST_KEY,jsonArray);
            return json.toString();
        } catch (JSONException jsonError){
            Log.e("JSON Convert Error",jsonError.getMessage());
            return null;
        }
    }
    public static String returnedJson(PurchaseReturnedData purchaseInfo){
        try {
            JSONObject json = new JSONObject();

            json.put(ConstantUtil.RETURNED_JSON_COMPLETION_KEY, purchaseInfo.getCompleted());
            json.put(ConstantUtil.RETURNED_JSON_STATUS_KEY, purchaseInfo.getStatus());
            json.put(ConstantUtil.CART_REFERENCE_ID_KEY, purchaseInfo.getCartReferenceId());
            json.put(ConstantUtil.RETURNED_JSON_DAILY_TRANSACTION_ID_KEY,
                    purchaseInfo.getDailyTransactionId());
            json.put(ConstantUtil.RETURNED_JSON_TRANSACTION_REFERENCE_KEY,
                    purchaseInfo.getTransactionReference());

            return json.toString();
        } catch (JSONException jsonError){
            Log.e("JSON Convert Error",jsonError.getMessage());
            return null;
        }
    }
}
