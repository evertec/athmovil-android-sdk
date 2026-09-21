package com.evertecinc.athmovil.sdk.checkout.objects.payment

import com.google.gson.annotations.SerializedName

class Ecommerce {
    @SerializedName("ecommerceId")
    var ecommerceId: String? = null

    @SerializedName("auth_token")
    var auth_token: String? = null
}


