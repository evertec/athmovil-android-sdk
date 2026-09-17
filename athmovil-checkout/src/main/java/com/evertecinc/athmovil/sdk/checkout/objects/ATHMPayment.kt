package com.evertecinc.athmovil.sdk.checkout.objects

import android.content.Context
import java.util.ArrayList
import java.util.UUID

class ATHMPayment(val context: Context) {
    var env: String? = null
    var publicToken: String? = null
    var timeout: Long = 600
    var total: Double = 0.0
    var tax: Double = 0.0
    var subtotal: Double = 0.0
    var metadata1: String? = null
    var metadata2: String? = null
    var paymentId: String? = null
        get() {
            if (field == null) {
                field = UUID.randomUUID().toString()
            }
            return field
        }
    var phoneNumber: String? = null

    var items = ArrayList<Items>()
    var callbackSchema: String? = null
    var ecommerceId: String? = null

    var buildType: String? = ""
        get() = field ?: ""

    @Deprecated("Use metadata1", ReplaceWith("metadata1"))
    fun getMetaData1(): String? = metadata1
    @Deprecated("Use metadata1", ReplaceWith("metadata1"))
    fun setMetaData1(metadata1: String?) { this.metadata1 = metadata1 }

    @Deprecated("Use metadata2", ReplaceWith("metadata2"))
    fun getMetaData2(): String? = metadata2
    @Deprecated("Use metadata2", ReplaceWith("metadata2"))
    fun setMetaData2(metadata2: String?) { this.metadata2 = metadata2 }
}
