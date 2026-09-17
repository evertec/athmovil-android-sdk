package com.evertecinc.athmovil.sdk.checkout.objects.payment

import java.io.Serializable

class PaymentResponseObject : Serializable {
    var status: String? = null
    var data: Ecommerce? = null
    var message: String? = null
    var errorcode: String? = null
}
