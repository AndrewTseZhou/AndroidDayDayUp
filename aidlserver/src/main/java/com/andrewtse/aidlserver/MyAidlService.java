package com.andrewtse.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

/**
 * @author xk
 * @date 2018/12/9
 */
public class MyAidlService extends Service {

    public MyAidlService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    private IBinder myBinder = new IMyServiceInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void myToast(String content) throws RemoteException {
            Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
        }
    };
}
