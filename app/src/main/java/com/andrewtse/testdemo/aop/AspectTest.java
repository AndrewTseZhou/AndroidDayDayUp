package com.andrewtse.testdemo.aop;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author xk
 * @date 2018/11/15
 */
@Aspect
public class AspectTest {

    private static final String TAG = "AspectTest";

    @Pointcut("execution(@com.andrewtse.testdemo.aop.CheckPermission * *(..))")
    public void checkPermissionBehavior() {

    }

    @Around("checkPermissionBehavior()")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.d(TAG, "checkNetStart");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CheckPermission annotation = signature.getMethod().getAnnotation(CheckPermission.class);
        if (annotation != null) {
            Object object = joinPoint.getThis();
            Activity context = (Activity) getContext(object);
            if (context != null) {
                if (!(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    int i = ContextCompat.checkSelfPermission(context, Manifest.permission_group.STORAGE);
                    if (PackageManager.PERMISSION_GRANTED != i) {
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    return null;
                }
            }
        }
        return joinPoint.proceed();
    }

    @Before("execution(* com.andrewtse.testdemo.LaunchActivity.testAOP())")
    public void onActivityMethodBefore(JoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "onActivityMethodBefore: " + key);
    }

    /**
     * 通过对象获取上下文
     */
    private Context getContext(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            return fragment.getActivity();
        } else if (object instanceof View) {
            View view = (View) object;
            return view.getContext();
        }
        return null;
    }
}
