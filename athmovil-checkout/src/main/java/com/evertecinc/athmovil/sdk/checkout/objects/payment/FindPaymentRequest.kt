package com.evertecinc.athmovil.sdk.checkout.objects.payment

import com.google.gson.annotations.SerializedName

class FindPaymentRequest(
    @SerializedName("ecommerceId")
    var ecommerceId: String?,
    @SerializedName("publicToken")
    var pubToken: String?
)

