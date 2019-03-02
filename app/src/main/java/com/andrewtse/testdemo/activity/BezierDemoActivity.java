package com.andrewtse.testdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.bezier_view.BezierActivity;
import com.andrewtse.testdemo.bezier_view.water_wave.WaterWaveActivity;

public class BezierDemoActivity extends AppCompatActivity {

    @BindView(R.id.btn_bezier)
    Button mBtnBezier;
    @BindView(R.id.btn_water_wave)
    Button mBtnWaterWaver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezier_demo);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_bezier, R.id.btn_water_wave})
    public void onButtonClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id) {
            case R.id.btn_bezier:
                intent = new Intent(this, BezierActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_water_wave:
                intent = new Intent(this, WaterWaveActivity.class);
                startActivity(intent);
                break;
        }
    }
}
