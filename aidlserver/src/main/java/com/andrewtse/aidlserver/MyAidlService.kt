package com.andrewtse.aidlserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.widget.Toast

/**
 * @author xk
 * @date 2018/12/9
 */
class MyAidlService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return myBinder
    }

    private val myBinder: IBinder = object : IMyServiceInterface.Stub() {
        @Throws(RemoteException::class)
        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String) {
        }

        @Throws(RemoteException::class)
        override fun myToast(content: String) {
            Toast.makeText(applicationContext, content, Toast.LENGTH_SHORT).show()
        }
    }
}