package com.andrewtse.testdemo.suspension_window;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.andrewtse.testdemo.activity.SuspensionWindowActivity;
import com.andrewtse.testdemo.utils.PermissionUtil;

/**
 * @author xk
 * @date 2019/4/7
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT.equals(intent.getAction())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PermissionUtil.isCanDrawOverlays(context) && PermissionUtil.isAccessibilityServiceEnable(context)) {
                    serviceGo(context);
                } else {
                    mainPageGo(context);
                }
            } else {
                if (PermissionUtil.isAccessibilityServiceEnable(context)) {
                    serviceGo(context);
                } else {
                    mainPageGo(context);
                }
            }
        }
    }

    private void mainPageGo(Context context) {
        Intent launch = new Intent(context, SuspensionWindowActivity.class);
        launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
    }

    private void serviceGo(Context context) {
        Intent intent = new Intent(context, SideBarService.class);
        context.startService(intent);
    }
}
