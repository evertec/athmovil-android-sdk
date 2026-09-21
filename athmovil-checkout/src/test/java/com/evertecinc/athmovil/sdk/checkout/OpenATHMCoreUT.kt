package com.evertecinc.athmovil.sdk.checkout

import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenATHMCoreUT {

    @Test
    fun getUrlEnvironment_returnsExpectedEndpoints() {
        assertEquals(ConstantUtil.AWS_URL_PAYMENT_PRO, OpenATHMCore.getUrlEnvironment(""))
    }

    @Test
    fun hasFindPaymentData_validatesEcommerceId() {
        assertTrue(OpenATHMCore.hasFindPaymentData("ecommerce"))
        assertFalse(OpenATHMCore.hasFindPaymentData(""))
        assertFalse(OpenATHMCore.hasFindPaymentData(null))
    }

    @Test
    fun bearerToken_prefixesBearerKeyword() {
        assertEquals("Bearer abc", OpenATHMCore.bearerToken("abc"))
    }
}


