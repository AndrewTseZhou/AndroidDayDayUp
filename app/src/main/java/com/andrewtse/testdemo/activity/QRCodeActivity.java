package com.andrewtse.testdemo.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.qrcode.CustomCaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class QRCodeActivity extends AppCompatActivity {

    private TextView mTvQRContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        mTvQRContent = findViewById(R.id.tv_qr_content);

        findViewById(R.id.btn_scan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(QRCodeActivity.this);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "取消扫描", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mTvQRContent.setText(result.getContents());
                showBarcodeImage(result.getBarcodeImagePath());

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 加载并显示条形码图片
     */
    private void showBarcodeImage(String barcodeImagePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(barcodeImagePath));
            ((ImageView) findViewById(R.id.iv_qr)).setImageBitmap(BitmapFactory.decodeStream(fis));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
