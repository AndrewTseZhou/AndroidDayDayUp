package com.andrewtse.testdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.andrewtse.testdemo.R;

public class CameraDemoActivity extends AppCompatActivity {

    @BindView(R.id.btn_camera)
    public Button mBtnCamera;
    @BindView(R.id.btn_camera2)
    public Button mBtnCamera2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_demo);
        ButterKnife.bind(this);

        mBtnCamera.setOnClickListener(v -> {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        });

        mBtnCamera2.setOnClickListener(v -> {
            Intent intent = new Intent(this, Camera2Activity.class);
            startActivity(intent);
        });
    }
}
