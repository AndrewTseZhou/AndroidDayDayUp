package com.andrewtse.testdemo;

import android.app.Application;
import android.content.Context;
import com.taobao.sophix.SophixManager;

/**
 * @author xk
 * @date 2018/10/25
 */
public class MyRealApplicationStub extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SophixManager.getInstance().queryAndLoadNewPatch();
        CrashHandler.getInstance().init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
