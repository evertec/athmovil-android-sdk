package com.evertecinc.athmovil.sdk.checkout

/**
 * Core Kotlin helpers extracted from OpenATHM during incremental migration.
 */
object OpenATHMCore {

    @JvmStatic
    fun getUrlEnvironment(buildType: String?): String {
        return OpenATHM.getUrlEnvironment(buildType ?: "")
    }

    @JvmStatic
    fun safeString(value: String?): String {
        return value?.takeIf { it.isNotEmpty() } ?: ""
    }

    @JvmStatic
    fun safeDouble(value: Double): Double = value

    @JvmStatic
    fun bearerToken(token: String): String {
        return "Bearer $token"
    }

    @JvmStatic
    fun hasFindPaymentData(ecommerceId: String?): Boolean {
        return !ecommerceId.isNullOrBlank()
    }
}

