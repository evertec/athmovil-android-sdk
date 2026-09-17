package com.evertecinc.athmovil.sdk.checkout.objects.payment

import java.io.Serializable

class AuthorizationResponse : Serializable {
    var status: String? = null
    var data: AuthorizationObject? = null
}
