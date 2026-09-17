package com.evertecinc.athmovil.sdk.checkout.objects

import java.io.Serializable

class Items(
    var name: String,
    var desc: String?,
    var price: Double,
    var quantity: Long,
    var metadata: String?
) : Serializable {

    override fun toString(): String {
        return "Items(name='$name', desc=$desc, price=$price, quantity=$quantity, metadata=$metadata)"
    }
}
