package com.andrewtse.testdemo.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.bezier_view.BezierView2;

public class BezierActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezier);

        final BezierView2 bezierView2 = findViewById(R.id.bezier_view2);
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_first:
                        bezierView2.setControlPoint(1);
                        break;
                    case R.id.rb_second:
                        bezierView2.setControlPoint(2);
                        break;
                }
            }
        });
    }
}
