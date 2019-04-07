package com.andrewtse.testdemo.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

import androidx.annotation.RequiresApi;

/**
 * @author xk
 * @date 2019/4/7
 */
public class PermissionUtil {

    public static boolean isAccessibilityServiceEnable(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        assert accessibilityManager != null;
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().contains(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isSettingsCanWrite(Context context) {
        return Settings.System.canWrite(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isCanDrawOverlays(Context context) {
        return Settings.canDrawOverlays(context);
    }
}
