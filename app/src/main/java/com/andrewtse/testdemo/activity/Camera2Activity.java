package com.andrewtse.testdemo.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.io.FileHelper;
import com.andrewtse.testdemo.io.StrUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Camera2Activity extends AppCompatActivity implements Callback {

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

    private SurfaceHolder mHolder;
    private Handler mainHandler;
    private Handler childHandler;

    private Size mWinSize;
    private Size justSize;
    private float mRate = 1;
    //是否开启闪光灯
    private boolean isFlashLight;
    //是否是后置摄像头
    private boolean isBack;

    private String mCameraID;
    private ImageReader mImageReader;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraCharacteristics mCharacteristics;
    private CameraDevice.StateCallback mStateCallback;

    //前置摄像头标记
    private static final int FRONT = 1;
    //后置摄像头标记
    private static final int BACK = 0;

    //旋转方向集合
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    //以防止在关闭相机之前应用程序退出
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        ButterKnife.bind(this);

        mWinSize = loadWinSize(this);
        mHolder = mSvVideo.getHolder();
        mHolder.addCallback(this);

        //自动聚焦
        mSvVideo.setOnClickListener(v -> {
        });

        //缩放变焦
        mIvZoom.setOnClickListener(v -> setZoom());

        mIvSnap.setOnClickListener(v -> takePicture());

        //开闪光灯
        mIvFlash.setOnClickListener(v -> {
            if (!isFlashLight) {
                mIvFlash.setImageTintList(ColorStateList.valueOf(0xffEFB90F));
            } else {
                mIvFlash.setImageTintList(ColorStateList.valueOf(0xfffffffF));
            }
            isFlashLight = !isFlashLight;
            try {
                CaptureRequest.Builder reqBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                reqBuilder.addTarget(mHolder.getSurface());
                reqBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
                reqBuilder.set(CaptureRequest.FLASH_MODE, isFlashLight ? CameraMetadata.FLASH_MODE_TORCH : CameraMetadata.FLASH_MODE_OFF);
                mCameraCaptureSession.setRepeatingRequest(reqBuilder.build(), null, childHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        });

        //切换镜头
        mIvSwitch.setOnClickListener(v -> {
            changeCamera(isBack ? BACK : FRONT);
            isBack = !isBack;
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        //主线程Handler
        mainHandler = new Handler(getMainLooper());
        //子线程Handler
        childHandler = new Handler(handlerThread.getLooper());

        mCameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;//后摄像头
        //获取摄像头管理器
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            // 获取指定摄像头的特性
            mCharacteristics = mCameraManager.getCameraCharacteristics(mCameraID);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        Rect activeArraySize = mCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        Float aFloat = mCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);

        fitPhotoSize();
        adjustScreenW(mSvVideo);//相机适应屏幕

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mStateCallback = new CameraDevice.StateCallback() {

                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraOpenCloseLock.release();
                    mCameraDevice = camera;
                    startPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    mCameraOpenCloseLock.release();
                    mCameraDevice.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    mCameraOpenCloseLock.release();
                    mCameraDevice.close();
                }
            };

            mCameraManager.openCamera(mCameraID, mStateCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraDevice.close();//释放资源
    }

    /**
     * 开启预览
     */
    private void startPreview() {
        initImageReader();
        try {
            // 创建预览需要的CaptureRequest.Builder
            final CaptureRequest.Builder reqBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            reqBuilder.addTarget(mHolder.getSurface());
            //reqBuilder可以设置参数
            // 自动对焦
            reqBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动打开闪光灯
            reqBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) return;
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        // 显示预览
                        mCameraCaptureSession.setRepeatingRequest(reqBuilder.build(), null, childHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(Camera2Activity.this, "配置失败", Toast.LENGTH_SHORT).show();
                }
            };

            mCameraDevice.createCaptureSession(Arrays.asList(mHolder.getSurface(), mImageReader.getSurface()), stateCallback, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍照
     */
    private void takePicture() {
        if (mCameraDevice != null) {
            try {
                CaptureRequest.Builder reqBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                reqBuilder.addTarget(mImageReader.getSurface());
                // 自动对焦
                reqBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // 自动打开闪光灯
                reqBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                // 获取手机方向
                int rotation = getWindowManager().getDefaultDisplay().getRotation();

                // 根据设备方向计算设置照片的方向
                reqBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                //拍照
                mCameraCaptureSession.capture(reqBuilder.build(), null, childHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化：ImageReader
     */
    private void initImageReader() {
        mImageReader = ImageReader.newInstance(justSize.getWidth(), justSize.getHeight(), ImageFormat.JPEG, 1);
        //可以在这里处理拍照得到的临时照片
        mImageReader.setOnImageAvailableListener(reader -> {
            // 拿到拍照照片数据
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();

            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);//由缓冲区存入字节数组

            File file = FileHelper.get().createFile("DCIM/CustomCamera/" + StrUtil.getCurrentTime_yyyyMMddHHmmss() + ".jpg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startPreview();
                reader.close();
            }
        }, mainHandler);
    }

    /**
     * 缩放
     */
    public void setZoom() {
        if ((mRate - 1) * 10 / 4 + 1 > 4.6f) {
            mRate = 1;
        }
        String rate = new DecimalFormat("#.0").format((mRate - 1) * 10 / 4 + 1);
        mIvZoom.setText(rate + "x");

        try {
            CaptureRequest.Builder reqBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            reqBuilder.addTarget(mHolder.getSurface());
            reqBuilder.set(CaptureRequest.SCALER_CROP_REGION, new Rect(0, 0, (int) (justSize.getWidth() / mRate), (int) (justSize.getHeight() / mRate)));
            mCameraCaptureSession.setRepeatingRequest(reqBuilder.build(), null, childHandler);
            mRate += 0.15;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开指定摄像头
     */
    private void changeCamera(int id) {
        closeCamera();
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mCameraManager.openCamera(id + "", mStateCallback, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * 关闭当前相机
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (mCameraCaptureSession != null) {
                mCameraCaptureSession.close();
                mCameraCaptureSession = null;
            }
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (mImageReader != null) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * 计算该手机合适的照片尺寸
     * 将屏幕高的2倍进行参考,取差值最小的index,再根据屏幕宽的两倍，取差值最小的index
     */
    private void fitPhotoSize() {
        // 获取指定摄像头的特性
        CameraCharacteristics characteristics = null;
        try {
            characteristics = mCameraManager.getCameraCharacteristics(mCameraID);
            // 获取摄像头支持的配置属性
            StreamConfigurationMap map = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            // 获取摄像头支持的最大尺寸
            List<Size> sizes = Arrays.asList(map.getOutputSizes(ImageFormat.JPEG));
            int minIndex = 0;//差距最小的索引
            int minDx = Integer.MAX_VALUE;
            int minDy = Integer.MAX_VALUE;
            int[] dxs = new int[sizes.size()];
            int justW = mWinSize.getHeight() * 2;//相机默认是横向的，so
            int justH = mWinSize.getWidth() * 2;
            for (int i = 0; i < sizes.size(); i++) {
                dxs[i] = sizes.get(i).getWidth() - justW;
            }
            for (int i = 0; i < dxs.length; i++) {
                int abs = Math.abs(dxs[i]);
                if (abs < minDx) {
                    minIndex = i;//获取高的最适索引
                    minDx = abs;
                }
            }
            for (int i = 0; i < sizes.size(); i++) {
                Size size = sizes.get(i);
                if (size.getWidth() == sizes.get(minIndex).getWidth()) {
                    int dy = Math.abs(justH - size.getHeight());
                    if (dy < minDy) {
                        minIndex = i;//获取宽的最适索引
                        minDy = dy;
                    }
                }
            }
            justSize = sizes.get(minIndex);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 适应屏幕
     */
    private void adjustScreenW(View surfaceView) {
        int height = surfaceView.getHeight();
        int width = surfaceView.getWidth();
        if (height > width) {
            float justH = width * 4.f / 3;
            mSvVideo.setScaleX(height / justH);
        } else {
            float justW = height * 4.f / 3;
            mSvVideo.setScaleY(width / justW);
        }
    }

    /**
     * 获得屏幕高度
     *
     * @param ctx 上下文
     */
    public Size loadWinSize(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return new Size(outMetrics.widthPixels, outMetrics.heightPixels);
    }
}
