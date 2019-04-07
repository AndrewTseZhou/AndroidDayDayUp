package com.andrewtse.testdemo.suspension_window;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.andrewtse.testdemo.suspension_window.widgets.SideBarArrow;

/**
 * @author xk
 * @date 2019/4/7
 */
public class SideBarHideReceiver extends BroadcastReceiver {

    private SideBarArrow mLeft = null;
    private SideBarArrow mRight = null;

    private static final String ACTION_HIDE = "com.andrewtse.testdemo.ACTION_HIDE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_HIDE.equals(intent.getAction())) {
            if (mLeft != null) {
                mLeft.launcherInvisibleSideBar();
            }

            if (mRight != null) {
                mRight.launcherInvisibleSideBar();
            }
        }
    }

    public void setSideBar(SideBarArrow left, SideBarArrow right) {
        this.mLeft = left;
        this.mRight = right;
    }
}
