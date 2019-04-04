package com.andrewtse.testdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.opencv.BankCardOCRActivity;
import com.andrewtse.testdemo.opencv.FaceDetectActivity;
import com.andrewtse.testdemo.opencv.IDCardOCRActivity;
import com.andrewtse.testdemo.opencv.ImgProcessActivity;

import org.opencv.android.OpenCVLoader;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpenCVActivity extends AppCompatActivity {

    private static final String TAG = "OpenCVActivity";
    @BindView(R.id.btn_hello)
    Button mBtnHello;
    @BindView(R.id.btn_faceDetect)
    Button mBtnFaceDetect;
    @BindView(R.id.btn_bankcard_ocr)
    Button mBtnBankCardOCR;
    @BindView(R.id.btn_id_card_ocr)
    Button mBtnIdCardOcr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cv);
        // 绑定View
        ButterKnife.bind(this);

        iniLoadOpenCV();
    }

    private void iniLoadOpenCV() {
        boolean success = OpenCVLoader.initDebug();
        if (success) {
            Log.i(TAG, "OpenCV Library loaded...");
        } else {
            Toast.makeText(this.getApplicationContext(), "could not load opencv lib...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.btn_hello, R.id.btn_faceDetect, R.id.btn_bankcard_ocr, R.id.btn_id_card_ocr})
    public void onViewClick(View view) {
        int vId = view.getId();
        switch (vId) {
            case R.id.btn_hello:
                Intent intentHello = new Intent(this, ImgProcessActivity.class);
                startActivity(intentHello);
                break;
            case R.id.btn_faceDetect:
                Intent intentFd = new Intent(this, FaceDetectActivity.class);
                startActivity(intentFd);
                break;
            case R.id.btn_bankcard_ocr:
                Intent intentBankCard = new Intent(this, BankCardOCRActivity.class);
                startActivity(intentBankCard);
                break;
            case R.id.btn_id_card_ocr:
                Intent idCard = new Intent(this, IDCardOCRActivity.class);
                startActivity(idCard);
                break;
        }
    }
}
