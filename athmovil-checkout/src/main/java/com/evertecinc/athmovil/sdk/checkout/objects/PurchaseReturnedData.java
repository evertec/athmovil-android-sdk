package com.evertecinc.athmovil.sdk.checkout.objects;
/**
 * Created by Juan Gabriel Zaragoza Bonilla on 4/11/2018.
 */
public class PurchaseReturnedData {
    private String completed;
    private String cartReferenceId;
    private String dailyTransactionId;
    private String transactionReference;
    private String status;

    public String getCompleted() { return completed; }
    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getCartReferenceId() { return cartReferenceId; }
    public void setCartReferenceId(String cartReferenceId) {
        this.cartReferenceId = cartReferenceId;
    }

    public String getDailyTransactionId() {
        return dailyTransactionId;
    }
    public void setDailyTransactionId(String dailyTransactionId) {
        this.dailyTransactionId = dailyTransactionId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
