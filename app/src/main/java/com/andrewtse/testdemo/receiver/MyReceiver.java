package com.andrewtse.testdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * @author xk
 * @date 2018/11/7
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            System.out.println("onReceive: app installed, " + intent.getDataString().substring(8));
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            System.out.println("onReceive: app uninstalled, " + intent.getDataString().substring(8));
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            System.out.println("屏幕关闭");
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            System.out.println("屏幕开启");
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            System.out.println("网络变化");
        }
    }
}
