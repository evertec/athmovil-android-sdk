package com.evertecinc.athmovil.sdk.checkout.objects;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Juan Gabriel Zaragoza Bonilla on 3/19/2018.
 */
public class Items implements Serializable {
    private String name;
    private String description;
    private Double price;
    private Long quantity;
    private String metadata;

    public Items(@NonNull String name, String description, @NonNull Double price,
                 @NonNull Long quantity, String metadata) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}