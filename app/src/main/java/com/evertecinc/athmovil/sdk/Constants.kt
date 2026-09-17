package com.evertecinc.athmovil.sdk

object Constants {

    const val CHECKOUT_DEMO_PREFS_KEY = "checkoutDemoPreferences"

    enum class RequestId {
        PUBLIC_TOKEN, PAYMENT_AMOUNT, TIMEOUT, SUBTOTAL, TAX, METADATA1, METADATA2, PHONE_NUMBER
    }

    const val PUBLIC_TOKEN_PREF_KEY = "selectedPublicToken"
    const val TIMEOUT_PREF_KEY = "selectedTimeout"
    const val PAYMENT_AMOUNT_PREF_KEY = "selectedPaymentAmount"
    const val THEME_PREF_KEY = "selectedTheme"
    const val LANGUAGE_PREF_KEY = "selectedLanguage"
    const val BUILD_TYPE_PREF_KEY = "selectedBuildType"

    const val SUBTOTAL_PREF_KEY = "subtotalEnabled"
    const val TAX_PREF_KEY = "taxEnabled"
    const val METADATA1_PREF_KEY = "metadata1Enabled"
    const val METADATA2_PREF_KEY = "metadata2Enabled"
    const val PHONE_NUMBER_PREF_KEY = "phoneNumberEnabled"
    const val ITEMS_PREF_KEY = "AthmSdkDummyItems"
}
