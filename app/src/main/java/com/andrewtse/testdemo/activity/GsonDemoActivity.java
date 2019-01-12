package com.andrewtse.testdemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.gsontest.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gson_demo);

        User user = new User();
        user.setName("哈哈哈");
        user.setAge(10);
        user.setId("123456789");
        user.setSex("male");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result = gson.toJson(user);
        ((TextView) findViewById(R.id.tv_gson)).setText(result);
    }
}
