package com.andrewtse.testdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.andrewtse.testdemo.activity.AIDLActivity;
import com.andrewtse.testdemo.activity.AbilityActivity;
import com.andrewtse.testdemo.activity.AnimationActivity;
import com.andrewtse.testdemo.activity.AppBarLayoutActivity;
import com.andrewtse.testdemo.activity.AudioActivity;
import com.andrewtse.testdemo.activity.BezierDemoActivity;
import com.andrewtse.testdemo.activity.BottomNavActivity;
import com.andrewtse.testdemo.activity.CameraDemoActivity;
import com.andrewtse.testdemo.activity.ContentProviderActivity;
import com.andrewtse.testdemo.activity.CustomCanvasActivity;
import com.andrewtse.testdemo.activity.DatePickerActivity;
import com.andrewtse.testdemo.activity.DrawerLayoutAndToolBarActivity;
import com.andrewtse.testdemo.activity.FlutterDemoActivity;
import com.andrewtse.testdemo.activity.GsonDemoActivity;
import com.andrewtse.testdemo.activity.HookDemoActivity;
import com.andrewtse.testdemo.activity.ImageScaleActivity;
import com.andrewtse.testdemo.activity.ImmersiveActivity;
import com.andrewtse.testdemo.activity.InstallAndRemoveActivity;
import com.andrewtse.testdemo.activity.JniActivity;
import com.andrewtse.testdemo.activity.LottieActivity;
import com.andrewtse.testdemo.activity.MemoryLeakActivity;
import com.andrewtse.testdemo.activity.OpenCVActivity;
import com.andrewtse.testdemo.activity.PendingIntentActivity;
import com.andrewtse.testdemo.activity.QRCodeActivity;
import com.andrewtse.testdemo.activity.RcActivity;
import com.andrewtse.testdemo.activity.RetrofitActivity;
import com.andrewtse.testdemo.activity.ScreenShotActivity;
import com.andrewtse.testdemo.activity.SophixActivity;
import com.andrewtse.testdemo.activity.SuspensionWindowActivity;
import com.andrewtse.testdemo.activity.SwipeLayoutActivity;
import com.andrewtse.testdemo.activity.SwitchButtonActivity;
import com.andrewtse.testdemo.activity.TempActivity;
import com.andrewtse.testdemo.activity.TimeAxleActivity;
import com.andrewtse.testdemo.activity.TimerActivity;
import com.andrewtse.testdemo.activity.ViewDragHelperActivity;
import com.andrewtse.testdemo.activity.ViewMoveActivity;
import com.andrewtse.testdemo.aop.CheckPermission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class LaunchActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "LaunchActivity";
    private ConstraintLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除标题栏
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //禁止状态栏下拉
        //getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);

        setContentView(R.layout.activity_launch);
//        Bmob.initialize(this, "8da047e27e7c871af0d0bc3563f40c57");

        coordinatorLayout = findViewById(R.id.coordinator_layout);

        findViewById(R.id.btn_rc_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_temp_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_bezier_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_timer_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_qr_code_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_jni_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_memory_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_gson_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_sophix_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_canvas_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_receiver_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_pending_intent_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_screen_shot_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_switch_button_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_swipelayout_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_toolbar_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_appbar_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_aidl_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_provider_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_retrofit_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_animation_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_view_drag_helper_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_navigation_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_view_move_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_image_scale_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_immersive_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_ability_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_opencv_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_audio_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_camera_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_hook_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_date_picker_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_suspension_window_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_flutter_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_lottie_activity_click).setOnClickListener(this);
        findViewById(R.id.btn_time_axle_activity_click).setOnClickListener(this);

//        findViewById(R.id.fab).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(coordinatorLayout, "测试消息哦", BaseTransientBottomBar.LENGTH_LONG).setAction("确定", new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(LaunchActivity.this, "哈哈哈", Toast.LENGTH_LONG).show();
//                    }
//                }).show();
//            }
//        });
        testAOP();
    }

    @Override
    protected void onStart() {
        System.out.println("onstart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        System.out.println("onresume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        System.out.println("onpause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        System.out.println("onstop");
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        System.out.println("onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        System.out.println("ondestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_rc_activity_click:
                intent = new Intent(this, RcActivity.class);
                break;
            case R.id.btn_temp_activity_click:
                intent = new Intent(this, TempActivity.class);
                break;
            case R.id.btn_bezier_activity_click:
                intent = new Intent(this, BezierDemoActivity.class);
                break;
            case R.id.btn_timer_activity_click:
                intent = new Intent(this, TimerActivity.class);
                break;
            case R.id.btn_qr_code_activity_click:
                intent = new Intent(this, QRCodeActivity.class);
                break;
            case R.id.btn_jni_activity_click:
                intent = new Intent(this, JniActivity.class);
                break;
            case R.id.btn_memory_activity_click:
                intent = new Intent(this, MemoryLeakActivity.class);
                break;
            case R.id.btn_gson_activity_click:
                intent = new Intent(this, GsonDemoActivity.class);
                break;
            case R.id.btn_sophix_activity_click:
                intent = new Intent(this, SophixActivity.class);
                break;
            case R.id.btn_canvas_activity_click:
                intent = new Intent(this, CustomCanvasActivity.class);
                break;
            case R.id.btn_receiver_activity_click:
                intent = new Intent(this, InstallAndRemoveActivity.class);
                break;
            case R.id.btn_pending_intent_activity_click:
                intent = new Intent(this, PendingIntentActivity.class);
                break;
            case R.id.btn_screen_shot_activity_click:
                intent = new Intent(this, ScreenShotActivity.class);
                break;
            case R.id.btn_switch_button_activity_click:
                intent = new Intent(this, SwitchButtonActivity.class);
                break;
            case R.id.btn_swipelayout_activity_click:
                intent = new Intent(this, SwipeLayoutActivity.class);
                break;
            case R.id.btn_toolbar_activity_click:
                intent = new Intent(this, DrawerLayoutAndToolBarActivity.class);
                break;
            case R.id.btn_appbar_activity_click:
                intent = new Intent(this, AppBarLayoutActivity.class);
                break;
            case R.id.btn_aidl_activity_click:
                intent = new Intent(this, AIDLActivity.class);
                break;
            case R.id.btn_provider_activity_click:
                intent = new Intent(this, ContentProviderActivity.class);
                break;
            case R.id.btn_retrofit_activity_click:
                intent = new Intent(this, RetrofitActivity.class);
                break;
            case R.id.btn_animation_activity_click:
                intent = new Intent(this, AnimationActivity.class);
                break;
            case R.id.btn_view_drag_helper_activity_click:
                intent = new Intent(this, ViewDragHelperActivity.class);
                break;
            case R.id.btn_navigation_activity_click:
                intent = new Intent(this, BottomNavActivity.class);
                break;
            case R.id.btn_view_move_activity_click:
                intent = new Intent(this, ViewMoveActivity.class);
                break;
            case R.id.btn_image_scale_activity_click:
                intent = new Intent(this, ImageScaleActivity.class);
                break;
            case R.id.btn_immersive_activity_click:
                intent = new Intent(this, ImmersiveActivity.class);
                break;
            case R.id.btn_ability_activity_click:
                intent = new Intent(this, AbilityActivity.class);
                break;
            case R.id.btn_opencv_activity_click:
                intent = new Intent(this, OpenCVActivity.class);
                break;
            case R.id.btn_audio_activity_click:
                intent = new Intent(this, AudioActivity.class);
                break;
            case R.id.btn_camera_activity_click:
                intent = new Intent(this, CameraDemoActivity.class);
                break;
            case R.id.btn_hook_activity_click:
                intent = new Intent(this, HookDemoActivity.class);
                break;
            case R.id.btn_date_picker_activity_click:
                intent = new Intent(this, DatePickerActivity.class);
                break;
            case R.id.btn_suspension_window_activity_click:
                intent = new Intent(this, SuspensionWindowActivity.class);
                break;
            case R.id.btn_flutter_activity_click:
                intent = new Intent(this, FlutterDemoActivity.class);
                break;
            case R.id.btn_lottie_activity_click:
                intent = new Intent(this, LottieActivity.class);
                break;
            case R.id.btn_time_axle_activity_click:
                intent = new Intent(this, TimeAxleActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            Log.d(TAG, "用户授权了");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @CheckPermission
    public void testAOP() {
        Log.d(TAG, "testAOP");
    }
}
