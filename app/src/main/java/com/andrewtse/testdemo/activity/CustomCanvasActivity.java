package com.andrewtse.testdemo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.custom_canvas_view.CustomCanvasView;
import java.io.File;
import java.io.FileOutputStream;

public class CustomCanvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_canvas);

        final ConstraintLayout layout = findViewById(R.id.cl_custom_canvas);
        final CustomCanvasView mCustomCanvasView = findViewById(R.id.canvas_view);
        Button mBtnUndo = findViewById(R.id.btn_undo);
        Button mBtnClear = findViewById(R.id.btn_clear);
        Button mBtnRedo = findViewById(R.id.btn_redo);
        Button mBtnScreenShot = findViewById(R.id.btn_screen_shot);

        Intent intent = getIntent();
        if (intent != null) {
            byte[] bis = intent.getByteArrayExtra("screenShot");
            if (bis != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
                mCustomCanvasView.setSrcBitmap(bitmap);
            }
        }

        mBtnUndo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomCanvasView.undo();
            }
        });

        mBtnRedo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomCanvasView.redo();
            }
        });

        mBtnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomCanvasView.clear();
            }
        });

        mBtnScreenShot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setDrawingCacheEnabled(true);
                layout.buildDrawingCache();
                // 重新测量一遍View的宽高
                layout.measure(View.MeasureSpec.makeMeasureSpec(layout.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(layout.getHeight(), View.MeasureSpec.EXACTLY));
                // 确定View的位置
                layout.layout((int) layout.getX(), (int) layout.getY(), (int) (layout.getX() + layout.getMeasuredWidth()), (int) (layout.getY() + layout.getMeasuredHeight()));
                // 生成View宽高一样的Bitmap
                Bitmap bitmap = Bitmap.createBitmap(layout.getDrawingCache(), 0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
                layout.setDrawingCacheEnabled(false);
                layout.destroyDrawingCache();

                //主要是先创建一张白色图片，然后把原来的绘制至上去，目的是为了解决截屏后背景是黑色的问题
                Bitmap outB = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(outB);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmap, 0, 0, null);

                File file = new File(Environment.getExternalStorageDirectory(), "/test.png");
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    outB.compress(CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
