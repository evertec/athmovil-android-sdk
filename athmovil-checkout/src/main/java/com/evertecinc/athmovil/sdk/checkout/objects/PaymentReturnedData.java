package com.evertecinc.athmovil.sdk.checkout.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Juan Gabriel Zaragoza Bonilla on 4/11/2018.
 */
public class PaymentReturnedData {
    private String referenceNumber;
    private String status;
    private double subtotal;
    private double total;
    private double tax;
    private String metadata1;
    private String metadata2;
    private ArrayList<Items> items = new ArrayList<>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReferenceNumber() {
        return referenceNumber;
    }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber;}

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public String getMetadata1() { return metadata1; }
    public void setMetadata1(String metadata1) {
        this.metadata1 = metadata1;
    }

    public String getMetadata2() { return metadata2; }
    public void setMetadata2(String metaData2) {
        this.metadata2 = metaData2;
    }

    public ArrayList<Items> getItemsSelectedList() { return items; }
    public void setItemsSelectedList(ArrayList<Items> items) { this.items = items; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentReturnedData data = (PaymentReturnedData) o;
        return Objects.equals(referenceNumber, data.referenceNumber) &&
                Objects.equals(status, data.status)&&
                Objects.equals(subtotal, data.subtotal)&&
                Objects.equals(total, data.total)&&
                Objects.equals(tax, data.tax)&&
                Objects.equals(metadata1, data.metadata1)&&
                Objects.equals(metadata2, data.metadata2)&&
                Objects.equals(items, data.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceNumber, status,subtotal, total,
                tax, metadata1,metadata2, items);
    }
}
