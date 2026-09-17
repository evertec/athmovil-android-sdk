package com.evertecinc.athmovil.sdk.checkout.objects

import java.util.ArrayList
import java.util.Objects

class PaymentReturnedData {
    var status: String? = null
        get() = field?.uppercase()
    var date: String? = null
    var referenceNumber: String? = null
    var dailyTransactionID: String? = null
        get() = field?.let { trimLeadingZeros(it) }
    var name: String? = null
    var phoneNumber: String? = null
    var email: String? = null
    var total: Double = 0.0
    var tax: Double = 0.0
    var subtotal: Double = 0.0
    var fee: Double = 0.0
    var netAmount: Double = 0.0
    var metadata1: String? = null
    var metadata2: String? = null
    var paymentId: String? = null
    var ecommerceId: String? = null
    var sdkVersion: String? = null
    var fromFindPayment: Boolean = false
    var items = ArrayList<Items>()

    fun getItemsSelectedList(): ArrayList<Items> {
        return ArrayList(items)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (other is PaymentReturnedData) {
            return referenceNumber == other.referenceNumber &&
                    status == other.status &&
                    subtotal == other.subtotal &&
                    total == other.total &&
                    tax == other.tax &&
                    metadata1 == other.metadata1 &&
                    metadata2 == other.metadata2 &&
                    ecommerceId == other.ecommerceId &&
                    sdkVersion == other.sdkVersion &&
                    date == other.date &&
                    dailyTransactionID == other.dailyTransactionID &&
                    name == other.name &&
                    phoneNumber == other.phoneNumber &&
                    email == other.email &&
                    fee == other.fee &&
                    netAmount == other.netAmount &&
                    fromFindPayment == other.fromFindPayment &&
                    items == other.items
        }
        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(
            referenceNumber, status, subtotal, total,
            tax, metadata1, metadata2, ecommerceId, sdkVersion, items, date, dailyTransactionID,
            name, phoneNumber, email, fee, netAmount, fromFindPayment
        )
    }

    companion object {
        @JvmStatic
        fun trimLeadingZeros(source: String): String {
            for (i in source.indices) {
                val c = source[i]
                if (c != '0') {
                    return source.substring(i)
                }
            }
            return "0"
        }
    }
}
