package com.andrewtse.testdemo.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.ability_view.AbilityView;
import com.andrewtse.testdemo.ability_view.DataMapper;
import java.util.HashMap;

public class AbilityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ability);

        AbilityView abilityView = findViewById(R.id.abv);
        HashMap<String, Integer> mData = new HashMap<>();
        mData.put("Java", 100);
        mData.put("Kotlin", 80);
        mData.put("JavaScript", 100);
        mData.put("Python", 60);
        mData.put("Dart", 70);
        mData.put("C++", 60);
        mData.put("C", 60);
        mData.put("CSS", 100);

        abilityView.setDataMapper(new DataMapper(new String[]{"神", "高", "普", "新", "入"}));
        abilityView.setData(mData);
    }
}
