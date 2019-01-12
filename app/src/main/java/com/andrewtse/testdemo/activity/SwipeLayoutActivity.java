package com.andrewtse.testdemo.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.adapter.MySwipeLayoutAdapter;
import com.daimajia.swipe.util.Attributes;
import java.util.ArrayList;
import java.util.List;

public class SwipeLayoutActivity extends AppCompatActivity {

    private RecyclerView mrecycleView;
    private List<String> list;
    private MySwipeLayoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_layout);

        mrecycleView = findViewById(R.id.mRecycleView);
        list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("条目" + i);
        }
        adapter = new MySwipeLayoutAdapter(this, list);
        adapter.setMode(Attributes.Mode.Single);
        mrecycleView.setLayoutManager(new LinearLayoutManager(this));
        mrecycleView.setAdapter(adapter);
    }
}
