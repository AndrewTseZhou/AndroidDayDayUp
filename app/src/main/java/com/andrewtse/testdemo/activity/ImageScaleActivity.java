package com.andrewtse.testdemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.image_scale.ScaleImageView;

public class ImageScaleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_scale);

        ScaleImageView siv = findViewById(R.id.siv);
        siv.setImageResource(R.drawable.test);
    }
}
