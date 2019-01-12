package com.andrewtse.testdemo.activity;

import android.os.CountDownTimer;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.andrewtse.testdemo.R;

public class TimerActivity extends AppCompatActivity implements OnClickListener {

    private CountDownTimer countDownTimer;
    private TextView mTvTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        mTvTimer = findViewById(R.id.tv_timer);
        ConstraintLayout container = findViewById(R.id.container);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        countDownTimer = new CountDownTimer(1000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvTimer.setText("倒计时：" + millisUntilFinished / 100);

                TextView tv = new TextView(TimerActivity.this);
            }

            @Override
            public void onFinish() {
                Toast.makeText(TimerActivity.this, "倒计时结束", Toast.LENGTH_SHORT).show();
//                countDownTimer.cancel();
                countDownTimer.start();
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                countDownTimer.start();
                break;
            case R.id.btn_cancel:
                countDownTimer.cancel();
                mTvTimer.setText("倒计时：");
                break;
        }
    }
}
