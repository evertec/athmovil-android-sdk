package com.evertecinc.athmovil.sdk.checkout.objects;

import android.support.annotation.NonNull;
/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 */
public class ItemsSelected {
    private String name;
    private String description;
    private String price;
    private String quantity;

    public ItemsSelected(@NonNull String name, @NonNull String description, @NonNull String price,
                         @NonNull String quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}