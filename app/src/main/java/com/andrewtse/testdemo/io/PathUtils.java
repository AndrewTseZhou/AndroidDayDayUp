package com.andrewtse.testdemo.io;

import android.content.Context;
import android.os.Environment;


/**
 * 说明：路径工具
 */
public class PathUtils {
    /**
     * 获取APP文件所在目录
     * @return  /data/app/top.toly.test-2/base.apk
     */
    public static String getAPPPath(Context ctx) {
        return ctx.getPackageResourcePath();
    }

    /**
     * 获取APP文件所在目录
     * @return  /data/app/top.toly.test-1/base.apk
     */
    public static String getCodePath(Context ctx) {
        return ctx.getPackageCodePath();
    }


    /**
     * 获取files文件所在目录
     * @return  /data/data/top.toly.test/files
     */
    public static String getFilesPath(Context ctx) {
        return ctx.getFilesDir().getAbsolutePath();
    }


    /**
     * cache
     * @return  /data/data/top.toly.test/cache
     */
    public static String getCachePath(Context ctx) {
        return ctx.getCacheDir().getAbsolutePath();
    }

    /**
     * 获取SD卡根目录
     *
     * @return SD卡根目录
     */
    public static String getSDPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


}
