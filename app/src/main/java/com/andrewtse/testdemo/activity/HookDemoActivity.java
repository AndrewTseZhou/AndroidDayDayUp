package com.andrewtse.testdemo.activity;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrewtse.testdemo.LaunchActivity;
import com.andrewtse.testdemo.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HookDemoActivity extends AppCompatActivity {

    private static final String TAG = "HookDemoActivity";

    @BindView(R.id.btn_hook_click)
    Button mBtnHookClick;
    @BindView(R.id.hook_activity_root_view)
    ConstraintLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_demo);
        ButterKnife.bind(this);
        mBtnHookClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 哈哈哈哈哈");
            }
        });

        hookOnClickListener(mBtnHookClick);
        try {
            hookNotification(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hookOnClickListener(View view) {
        try {
            //获取ListenerInfo对象
            //Class.forName("android.view.View").getDeclaredMethod("getListenerInfo");
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo"); //等同于上一句
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(view);

            //获取原始的OnClickListener事件方法
            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
            Field mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            mOnClickListener.setAccessible(true);
            View.OnClickListener originOnClickListener = (View.OnClickListener) mOnClickListener.get(listenerInfo);

            //用Hook代理类替换原始的OnClickListener
            View.OnClickListener hookOnClickListener = new HookedOnClickListener(originOnClickListener);
            mOnClickListener.set(listenerInfo, hookOnClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class HookedOnClickListener implements View.OnClickListener {
        private View.OnClickListener origin;

        HookedOnClickListener(View.OnClickListener origin) {
            this.origin = origin;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(HookDemoActivity.this, "hook click", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Before click, do what you want to to.");
            if (origin != null) {
                origin.onClick(v);
            }
            Log.i(TAG, "After click, do what you want to to.");
        }
    }

    private void hookNotification(Context context) throws Exception {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Method getService = NotificationManager.class.getDeclaredMethod("getService");
        getService.setAccessible(true);
        Object sOriginService = getService.invoke(notificationManager);

        Class<?> notiMngClz = Class.forName("android.app.INotificationManager");
        Object proxyNotiMng = Proxy.newProxyInstance(context.getClass().getClassLoader(), new Class[]{notiMngClz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Log.d(TAG, "invoke(). method:" + method);
                String name = method.getName();
                Log.d(TAG, "invoke: name=" + name);
                if (args != null && args.length > 0) {
                    for (Object arg : args) {
                        Log.d(TAG, "invoke: arg=" + arg);
                    }
                }
                Toast.makeText(context.getApplicationContext(), "检测到有人发通知了", Toast.LENGTH_SHORT).show();
                // 操作交由 sOriginService 处理，不拦截通知
                return method.invoke(sOriginService, args);
            }
        });

        Field sServiceField = NotificationManager.class.getDeclaredField("sService");
        sServiceField.setAccessible(true);
        sServiceField.set(notificationManager, proxyNotiMng);
    }

    public static void hookAMSAfter26() {
        try {
            //获取 IActivityManagerSingleton
            Field declaredField = ActivityManager.class.getDeclaredField("IActivityManagerSingleton");
            declaredField.setAccessible(true);
            Object value = declaredField.get(null);

            Class<?> singletonClz = Class.forName("android.util.Singleton");
            Field instanceField = singletonClz.getDeclaredField("mInstance");
            instanceField.setAccessible(true);
            Object iActivityManagerObject = instanceField.get(value);

            //获取代理对象
            Class<?> iActivity = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{iActivity}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    method.invoke(iActivityManagerObject, args);
                    System.out.println("hhh 就不让你跳");
                    return null;
                }
            });

            //将proxy替换原来的对象
            instanceField.set(value, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookAMSBefore26() {
        try {
            //获取 IActivityManagerSingleton
            Class<?> forName = Class.forName("android.app.ActivityManagerNative");
            Field defaultField = forName.getDeclaredField("gDefault");
            defaultField.setAccessible(true);
            Object value = defaultField.get(null);

            Class<?> singletonClz = Class.forName("android.util.Singleton");
            Field instanceField = singletonClz.getDeclaredField("mInstance");
            instanceField.setAccessible(true);
            Object iActivityManagerObject = instanceField.get(value);

            //获取代理对象
            Class<?> iActivity = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{iActivity}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    method.invoke(iActivityManagerObject, args);
                    System.out.println("hhh 就不让你跳");
                    return null;
                }
            });

            //将proxy替换原来的对象
            instanceField.set(value, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
