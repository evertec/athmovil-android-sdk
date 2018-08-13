package com.evertecinc.athmovil.sdk.checkout.objects;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 */
public class PurchaseData {
    private Context context;
    private String businessToken;
    private String cartReferenceId;
    private String subtotal;
    private String tax;
    private String total;
    private long timer = 600000;
    private List<ItemsSelected> itemsSelectedList =  new ArrayList<>();

    public Context getContext() { return context; }
    public void setContext(Context context) { this.context = context; }

    public String getBusinessToken() {return businessToken;}
    public void setBusinessToken(String businessToken) {this.businessToken = businessToken;}

    public String getCartReferenceId() { return cartReferenceId; }
    public void setCartReferenceId(String cartReferenceId) {
        this.cartReferenceId = cartReferenceId;
    }

    public String getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTax() {
        return tax;
    }
    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTotal() { return total; }
    public void setTotal(String total) {
        this.total = total;
    }

    public long getTimer() { return timer; }
    public void setTimer(long timer) { this.timer = timer; }

    public List<ItemsSelected> getItemsSelectedList() { return itemsSelectedList; }
    public void setItemsSelectedList(List<ItemsSelected> itemsSelectedList) {
        this.itemsSelectedList = itemsSelectedList;
    }
}