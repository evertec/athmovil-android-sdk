package com.evertecinc.athmovil.sdk.checkout.objects.payment

import com.evertecinc.athmovil.sdk.checkout.objects.Items
import com.google.gson.annotations.SerializedName

class AuthorizationObject {

    @SerializedName("env")
    var env: String? = null

    @SerializedName("publicToken")
    var pubToken: String? = null

    @SerializedName("timeout")
    var timeout: Long = 0

    @SerializedName("total")
    var total: Double? = null

    @SerializedName("tax")
    var tax: Double? = null

    @SerializedName("subtotal")
    var subtotal: Double? = null

    @SerializedName("metadata1")
    var metadata1: String? = null

    @SerializedName("metadata2")
    var metadata2: String? = null

    @SerializedName("items")
    var items: List<Items>? = null

    @SerializedName("phoneNumber")
    var phoNumber: String? = null

    @SerializedName("ecommerceId")
    var ecommerceId: String? = null

    @SerializedName("ecommerceStatus")
    var ecommerceStatus: String? = null

    @SerializedName("referenceNumber")
    var referenceNumber: String? = null

    @SerializedName("dailyTransactionId")
    var dailyTransactionID: String? = null

    @SerializedName("totalRefundedAmount")
    var totalRefundedAmount: String? = null

    @SerializedName("netAmount")
    var netAmount: Double? = null

    @SerializedName("fee")
    var fee: Double? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("email")
    var email: String? = null

    constructor()

    constructor(
        env: String?,
        pubToken: String?,
        timeout: Long,
        total: Double?,
        tax: Double?,
        subtotal: Double?,
        metadata1: String?,
        metadata2: String?,
        items: List<Items>?,
        phoNumber: String?
    ) {
        this.env = env
        this.pubToken = pubToken
        this.timeout = timeout
        this.total = total
        this.tax = tax
        this.subtotal = subtotal
        this.metadata1 = metadata1
        this.metadata2 = metadata2
        this.items = items
        this.phoNumber = phoNumber
    }

    // Properties for Java compatibility
    var publicToken: String?
        get() = pubToken
        set(value) {
            pubToken = value
        }

    var phoneNumber: String?
        get() = phoNumber
        set(value) {
            phoNumber = value
        }
}
