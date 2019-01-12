package com.andrewtse.testdemo.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.temperature_view.TempView;
import com.andrewtse.testdemo.temperature_view.TempView2;

public class TempActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private TempView mTempView;
    private TempView2 mTempView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_NORMAL);//温度传感器
        mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);

        mTempView = findViewById(R.id.temp_view);
        mTempView2 = findViewById(R.id.temp_view2);
    }

    //传感器监听器
    private SensorEventListener listener = new SensorEventListener() {
        @Override//传感器改变时调用
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;//获取结果
            switch (event.sensor.getType()) {//获取触发的传感器类型
                case Sensor.TYPE_AMBIENT_TEMPERATURE://温度传感器
                    mTempView.setCurrentCount(values[0]);
                    Toast.makeText(getApplicationContext(), "当前温度为：" + values[0], Toast.LENGTH_SHORT).show();
                    break;
                case Sensor.TYPE_LIGHT:
                    mTempView.setCurrentCount(values[0]);
                    mTempView2.setCurrentCount(values[0]);
                    break;
                default:
                    break;
            }
        }

        @Override//传感器进度发生改变时调用
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(listener);//取消监听
        }
    }
}
