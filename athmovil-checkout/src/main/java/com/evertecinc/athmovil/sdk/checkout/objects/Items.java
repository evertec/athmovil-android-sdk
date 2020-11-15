package com.evertecinc.athmovil.sdk.checkout.objects;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 */
public class Items implements Serializable {
    private String name;
    private String desc;
    private Double price;
    private Long quantity;
    private String metadata;

    public Items(@NonNull String name, @NonNull String desc, @NonNull Double price,
                 @NonNull Long quantity, String metadata) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.quantity = quantity;
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Double getPrice() {
        return price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}