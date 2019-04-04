package com.andrewtse.testdemo.opencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrewtse.testdemo.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IDCardOCRActivity extends AppCompatActivity {

    @BindView(R.id.iv_src)
    ImageView mIvSrc;
    @BindView(R.id.btn_ocr)
    Button mBtnOcr;
    @BindView(R.id.tv_result)
    TextView mTvResult;
    @BindView(R.id.iv_src_handle)
    ImageView mIvSrcHandle;

    private Bitmap mSrcBitmap;
    private TessBaseAPI mTessBaseAPI;
    private String language = "eng";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard_ocr);
        ButterKnife.bind(this);
        mSrcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.id_card);
        mIvSrc.setImageBitmap(mSrcBitmap);
        initTess();
    }

    private void initTess() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                mTessBaseAPI = new TessBaseAPI();
                try {
                    InputStream is = getAssets().open(language + ".traineddata");
                    File file = new File("/sdcard/tess/tessdata/" + language + ".traineddata");
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int len = -1;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                    }
                    is.close();
                    return mTessBaseAPI.init("/sdcard/tess", language);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void handleImg(Bitmap bitmap) {
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.resize(src, src, new Size(640, 400));

        //灰度
        Mat dst = new Mat();
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);

        //二值化
        Imgproc.threshold(dst, dst, 100, 255, Imgproc.THRESH_BINARY);

        //膨胀
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 6));
        Imgproc.erode(dst, dst, element);

        showImg(dst);

        //轮廓检测
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hireachy = new Mat();
        Imgproc.findContours(dst, contours, hireachy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        List<Rect> rects = new ArrayList<>();
        Mat dstImg = new Mat();
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (rect.width > rect.height * 10) {
                rects.add(rect);
            }
        }
        Toast.makeText(this, "找到" + rects.size() + "处特征", Toast.LENGTH_SHORT).show();

        if (rects.size() == 1) {
            Rect rect = rects.get(0);
            src.submat(rect).copyTo(dstImg);
        } else {
            double lowPoint = 0;
            Rect finalRect = new Rect();
            for (int i = 0; i < rects.size(); i++) {
                Rect rect = rects.get(i);
                if (rect.tl().y > lowPoint) {
                    lowPoint = rect.tl().y;
                    finalRect = rect;
                }
            }
            src.submat(finalRect).copyTo(dstImg);
        }

        if (dstImg.cols() > 0 && dstImg.rows() > 0) {
            mSrcBitmap = Bitmap.createBitmap(dstImg.cols(), dstImg.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dstImg, mSrcBitmap);
            mIvSrc.setImageBitmap(mSrcBitmap);
            recognition(mSrcBitmap);
        }

        src.release();
        element.release();
        dst.release();
        dstImg.release();
    }

    private void recognition(Bitmap bitmap) {
        mTessBaseAPI.setImage(bitmap);
        mTvResult.setText(mTessBaseAPI.getUTF8Text());
        mTessBaseAPI.clear();
    }

    @OnClick(R.id.btn_ocr)
    public void onViewClicked() {
        handleImg(mSrcBitmap);
    }

    private void showImg(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        mIvSrcHandle.setImageBitmap(bitmap);
    }
}
