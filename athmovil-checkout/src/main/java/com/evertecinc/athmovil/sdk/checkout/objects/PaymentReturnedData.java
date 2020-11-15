package com.evertecinc.athmovil.sdk.checkout.objects;

import java.util.ArrayList;
import java.util.Objects;

public class PaymentReturnedData {

    private String status;
    private String date;
    private String referenceNumber;
    private String dailyTransactionID;
    private String name;
    private String phoneNumber;
    private String email;
    private double total;
    private double tax;
    private double subtotal;
    private double fee;
    private double netAmount;
    private String metadata1;
    private String metadata2;
    private ArrayList<Items> items = new ArrayList<>();

    public String getDailyTransactionID() {
        return dailyTransactionID;
    }

    public void setDailyTransactionID(String dailyTransactionID) {
        this.dailyTransactionID = dailyTransactionID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = new ArrayList<>();
        this.items.addAll(items);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public String getMetadata1() {
        return metadata1;
    }

    public void setMetadata1(String metadata1) {
        this.metadata1 = metadata1;
    }

    public String getMetadata2() {
        return metadata2;
    }

    public void setMetadata2(String metaData2) {
        this.metadata2 = metaData2;
    }

    public ArrayList<Items> getItemsSelectedList() {
        return new ArrayList<>(items);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentReturnedData data = (PaymentReturnedData) o;
        return Objects.equals(referenceNumber, data.referenceNumber) &&
                Objects.equals(status, data.status) &&
                Objects.equals(subtotal, data.subtotal) &&
                Objects.equals(total, data.total) &&
                Objects.equals(tax, data.tax) &&
                Objects.equals(metadata1, data.metadata1) &&
                Objects.equals(metadata2, data.metadata2) &&
                Objects.equals(items, data.items) &&
                Objects.equals(date, data.date) &&
                Objects.equals(referenceNumber, data.referenceNumber) &&
                Objects.equals(dailyTransactionID, data.dailyTransactionID) &&
                Objects.equals(name, data.name) &&
                Objects.equals(phoneNumber, data.phoneNumber) &&
                Objects.equals(email, data.email) &&
                Objects.equals(fee, data.fee) &&
                Objects.equals(netAmount, data.netAmount) &&
                Objects.equals(items, data.items);

    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceNumber, status, subtotal, total,
                tax, metadata1, metadata2, items, date, dailyTransactionID,
                name, phoneNumber, email, fee, netAmount);
    }

}
