package com.andrewtse.testdemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.andrewtse.testdemo.R;

public class SophixActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sophix);

        TextView textView = findViewById(R.id.tv_sophix);
        textView.setText("李朋伟哈哈哈哈");
    }
}
