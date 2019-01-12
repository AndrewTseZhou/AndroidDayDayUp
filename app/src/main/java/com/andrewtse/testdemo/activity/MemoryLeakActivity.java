package com.andrewtse.testdemo.activity;

import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.andrewtse.testdemo.R;

public class MemoryLeakActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_lead);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000000);
    }
}
