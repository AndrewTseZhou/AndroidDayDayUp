package com.andrewtse.testdemo.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.receiver.MyReceiver;

/**
 * 应用安装与卸载监听
 */

public class InstallAndRemoveActivity extends AppCompatActivity {

    private static final String TAG = "InstallAndRemoveActivity";
    private MyReceiver mReceiver1;
    private MyReceiver mReceiver2;
    private MyReceiver mReceiver3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_and_remove);
        mReceiver1 = new MyReceiver();
        mReceiver2 = new MyReceiver();
        mReceiver3 = new MyReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("android.intent.action.PACKAGE_ADDED");
        filter1.addAction("android.intent.action.PACKAGE_REPLACED");
        filter1.addAction("android.intent.action.PACKAGE_REMOVED");
        filter1.addDataScheme("package");
        this.registerReceiver(mReceiver1, filter1);

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("android.intent.action.SCREEN_ON");
        filter2.addAction("android.intent.action.SCREEN_OFF");
        this.registerReceiver(mReceiver2, filter2);

        IntentFilter filter3 = new IntentFilter();
        filter3.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(mReceiver3, filter3);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                                            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                                            .build();

            cm.requestNetwork(request, new ConnectivityManager.NetworkCallback() {

                @Override
                public void onAvailable(Network network) {
                    Log.d(TAG, "onAvailable(): network " + network);
                    // do something
                }
            });
        }

        System.out.println("注册监听");
    }

    @Override
    protected void onDestroy() {
        if (mReceiver1 != null) {
            this.unregisterReceiver(mReceiver1);
            System.out.println("销毁监听");
        }
        super.onDestroy();
    }
}
