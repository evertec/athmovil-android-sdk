package com.evertecinc.athmovil.sdk;

public class Constants {

    public static final String CHECKOUT_DEMO_PREFS_KEY = "checkoutDemoPreferences";

    public enum RequestId {
        PUBLIC_TOKEN, PAYMENT_AMOUNT, TIMEOUT, SUBTOTAL, TAX, METADATA1, METADATA2, PHONE_NUMBER
    }

    public static final String PUBLIC_TOKEN_PREF_KEY= "selectedPublicToken";
    public static final String TIMEOUT_PREF_KEY= "selectedTimeout";
    public static final String PAYMENT_AMOUNT_PREF_KEY= "selectedPaymentAmount";
    public static final String THEME_PREF_KEY= "selectedTheme";
    public static final String BUILD_TYPE_PREF_KEY= "selectedBuildType";
    public static final String FLOW_TYPE_PREF_KEY= "selectedFlowType";

    public static final String SUBTOTAL_PREF_KEY = "subtotalEnabled";
    public static final String TAX_PREF_KEY = "taxEnabled";
    public static final String METADATA1_PREF_KEY = "metadata1Enabled";
    public static final String METADATA2_PREF_KEY = "metadata2Enabled";
    public static final String PHONE_NUMBER_PREF_KEY = "phoneNumberEnabled";
    public static final String ITEMS_PREF_KEY = "AthmSdkDummyItems";

}
