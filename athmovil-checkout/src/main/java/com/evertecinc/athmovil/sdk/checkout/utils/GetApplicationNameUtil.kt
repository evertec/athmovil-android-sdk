package com.evertecinc.athmovil.sdk.checkout.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

object GetApplicationNameUtil {

    @JvmStatic
    fun getApplicationName(context: Context?): String {
        return try {
            if (context == null) {
                ConstantUtil.ATHM_APP_NOT_FOUND
            } else {
                val pm: PackageManager = context.packageManager ?: return ConstantUtil.ATHM_APP_NOT_FOUND
                val appInfo: ApplicationInfo = context.applicationInfo ?: return ConstantUtil.ATHM_APP_NOT_FOUND
                val label = appInfo.loadLabel(pm).toString().trim()
                if (label.isNotEmpty()) label else ConstantUtil.ATHM_APP_NOT_FOUND
            }
        } catch (_: Exception) {
            ConstantUtil.ATHM_APP_NOT_FOUND
        }
    }
}


