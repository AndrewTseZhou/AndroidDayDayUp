package com.andrewtse.testdemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.view_move.RunBall;

public class ViewMoveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_move);

        final RunBall ball = findViewById(R.id.run_ball);
        RadioGroup rgStatus = findViewById(R.id.rg_status);
        rgStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_horizontal_move:
                        ball.setStatus(0);
                        break;
                    case R.id.rb_reverse_move:
                        ball.setStatus(1);
                        break;
                }
            }
        });
    }
}
