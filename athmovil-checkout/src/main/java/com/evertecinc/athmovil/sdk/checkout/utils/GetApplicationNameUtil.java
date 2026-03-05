package com.evertecinc.athmovil.sdk.checkout.utils;

import static com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil.ATHM_APP_NOT_FOUND;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class GetApplicationNameUtil {

    public static String getApplicationName(Context context) {
        try {
            if (context == null) {
                return ATHM_APP_NOT_FOUND;
            }

            PackageManager pm = context.getPackageManager();
            if (pm == null) {
                return ATHM_APP_NOT_FOUND;
            }

            ApplicationInfo appInfo = context.getApplicationInfo();
            if (appInfo == null) {
                return ATHM_APP_NOT_FOUND;
            }

            CharSequence appLabel = appInfo.loadLabel(pm);
            return appLabel.toString().trim();

        } catch (Exception e) {
            return ATHM_APP_NOT_FOUND;
        }
    }

}
