package com.andrewtse.testdemo.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import com.andrewtse.aidlserver.IMyServiceInterface;
import com.andrewtse.testdemo.R;

public class AIDLActivity extends AppCompatActivity {

    private IMyServiceInterface myServiceInterface;

    private float downX;
    private float downY;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定服务成功回调
            myServiceInterface = IMyServiceInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //服务断开时回调
            myServiceInterface = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);

        bindService();

        Button mBtnRemote = findViewById(R.id.btn_remote);
        mBtnRemote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myServiceInterface.myToast("AIDL测试");
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mBtnRemote.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final float xDistance = event.getX() - downX;
                        final float yDistance = event.getY() - downY;
                        if (xDistance != 0 && yDistance != 0) {
                            int l = (int) (v.getLeft() + xDistance);
                            int r = (int) (v.getRight() + xDistance);
                            int t = (int) (v.getTop() + yDistance);
                            int b = (int) (v.getBottom() + yDistance);
                            v.layout(l, t, r, b);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void bindService() {
        Intent intent = new Intent();
        //Android 5.0开始，启动服务必须使用显示的，不能用隐式的
        intent.setComponent(new ComponentName("com.andrewtse.aidlserver", "com.andrewtse.aidlserver.MyAidlService"));
        intent.setAction("com.andrewtse.aidlserver.AIDL_SERVICE");
//        intent.setPackage("com.andrewtse.aidlserver");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
}
