package com.andrewtse.testdemo.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Switch;
import com.andrewtse.testdemo.R;

public class SwitchButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_button);

        final Switch switchBtn = findViewById(R.id.switch_btn);
        System.out.println("开关" + switchBtn.isChecked() + " " + switchBtn.isEnabled());
        switchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("开关" + switchBtn.isChecked() + " " + switchBtn.isEnabled());
            }
        });
    }
}
