package com.evertecinc.athmovil.sdk.checkout.utils

import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class JsonUtilUT {

    @Test
    fun paymentReturnedData_whenSameSdkVersion_hasSameHashAndEquals() {
        val left = PaymentReturnedData().apply {
            status = ConstantUtil.ResponseStatus.COMPLETED
            sdkVersion = "7.0.0"
        }
        val right = PaymentReturnedData().apply {
            status = ConstantUtil.ResponseStatus.COMPLETED
            sdkVersion = "7.0.0"
        }

        assertEquals(left, right)
        assertEquals(left.hashCode(), right.hashCode())
    }

    @Test
    fun paymentReturnedData_whenDifferentSdkVersion_isNotEqual() {
        val left = PaymentReturnedData().apply {
            status = ConstantUtil.ResponseStatus.COMPLETED
            sdkVersion = "7.0.0"
        }
        val right = PaymentReturnedData().apply {
            status = ConstantUtil.ResponseStatus.COMPLETED
            sdkVersion = "8.0.0"
        }

        assertNotEquals(left, right)
    }
}


