package com.andrewtse.testdemo.activity;

import android.os.Bundle;

import com.andrewtse.testdemo.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import io.flutter.facade.Flutter;

public class FlutterDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flutter_demo);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, Flutter.createFragment("route"));
        ft.commit();
    }
}
