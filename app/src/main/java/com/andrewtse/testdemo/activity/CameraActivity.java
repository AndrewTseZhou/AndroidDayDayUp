package com.andrewtse.testdemo.activity;

import android.content.res.ColorStateList;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.io.FileHelper;
import com.andrewtse.testdemo.io.ToastUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class CameraActivity extends AppCompatActivity implements Callback {

    private static final String TAG = "CameraActivity";

    @BindView(R.id.sv_video)
    public SurfaceView mSvVideo;
    @BindView(R.id.iv_snap)
    public ImageView mIvSnap;
    @BindView(R.id.iv_delay)
    public ImageView mIvDelay;
    @BindView(R.id.iv_flash)
    public ImageView mIvFlash;
    @BindView(R.id.iv_switch)
    public ImageView mIvSwitch;
    @BindView(R.id.tv_count_down)
    public TextView mTvCountDown;
    @BindView(R.id.iv_zoom)
    public TextView mIvZoom;
    @BindView(R.id.tv_pic)
    public TextView mTvPic;
    @BindView(R.id.tv_video)
    public TextView mTvVideo;

    private Camera mCamera;
    private SurfaceHolder mHolder;
    //当前缩放比例
    private int curZoom;
    private Parameters mParameters;
    //是否开启闪光灯
    private boolean isFlashLight;
    //是否是后置摄像头
    private boolean isBack;
    //前置摄像头标记
    private static final int FRONT = 1;
    //后置摄像头标记
    private static final int BACK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        mHolder = mSvVideo.getHolder();
        mHolder.addCallback(this);

        // 打开摄像头并将展示方向旋转90度
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        //获取相机参数
        mParameters = mCamera.getParameters();

        mSvVideo.setOnClickListener(v -> mCamera.autoFocus((success, camera) -> Log.d(TAG, "Camera autoFocus: " + success)));
        mIvZoom.setOnClickListener(v -> setZoom());
        mIvSnap.setOnClickListener(v -> takePicture("hello.jpg"));
        mIvFlash.setOnClickListener(v -> {
            if (!isFlashLight) {
                mIvFlash.setImageTintList(ColorStateList.valueOf(0xffEFB90F));
            } else {
                mIvFlash.setImageTintList(ColorStateList.valueOf(0xfffffffF));
            }
            isFlashLight = !isFlashLight;
            mParameters.setFlashMode(isFlashLight ? Parameters.FLASH_MODE_TORCH : Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
        });
        mIvSwitch.setOnClickListener(v -> {
            changeCamera(isBack ? BACK : FRONT);
            isBack = !isBack;
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);//Camera+SurfaceHolder
            mCamera.startPreview();
            mCamera.autoFocus(null);
        } catch (Exception e) {
            e.printStackTrace();
            mCamera.release();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
    }

    /**
     * 拍照
     */
    private void takePicture(String fileName) {
        mCamera.takePicture(null, null, (data, camera) -> {
            File file = FileHelper.get().createFile("DCIM/CustomCamera/" + fileName);
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(file);
                fos.write(data);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //拍完照要camera.startPreview();再开启预览， 否则界面就不动了
                mCamera.startPreview();
            }
        });
    }

    /**
     * 缩放
     */
    private void setZoom() {
        if (mParameters.isZoomSupported()) {
            Parameters parameters = mParameters;
            int maxZoom = parameters.getMaxZoom();
            if (maxZoom == 0) {
                return;
            }
            curZoom = parameters.getZoom();
            curZoom += maxZoom / 10;
            if (curZoom > maxZoom) {
                curZoom = 0;
            }
            parameters.setZoom(curZoom);
            mCamera.setParameters(parameters);
            String rate = new DecimalFormat("#.0").format(curZoom / (maxZoom / 10 * 2.f) + 1);
            mIvZoom.setText(rate + "x");
        } else {
            ToastUtil.show(this, "不支持变焦");
        }
    }

    /**
     * 切换摄像头
     */
    private void changeCamera(int type) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = openCamera(type);
        try {
            if (mCamera != null) {
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                mCamera.autoFocus(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Camera openCamera(int type) {
        int frontIndex = -1;
        int backIndex = -1;
        int cameraCount = Camera.getNumberOfCameras();
        CameraInfo info = new CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (CameraInfo.CAMERA_FACING_FRONT == info.facing) {
                frontIndex = i;
            } else if (CameraInfo.CAMERA_FACING_BACK == info.facing) {
                backIndex = i;
            }
        }

        if (type == FRONT && frontIndex != -1) {
            return Camera.open(frontIndex);
        } else if (type == BACK && backIndex != -1) {
            return Camera.open(backIndex);
        }
        return null;
    }
}
