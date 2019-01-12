package com.andrewtse.testdemo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.andrewtse.testdemo.R;
import java.io.ByteArrayOutputStream;

public class ScreenShotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);

        final ConstraintLayout layout = findViewById(R.id.cl_screen_shot);
        final ImageView iv = findViewById(R.id.iv_screen_shot);

        findViewById(R.id.btn_screen_shot).setOnClickListener(new OnClickListener() {
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
                iv.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();
                Intent intent = new Intent(ScreenShotActivity.this, CustomCanvasActivity.class);
                intent.putExtra("screenShot", bytes);
                startActivity(intent);
            }
        });
    }
}
