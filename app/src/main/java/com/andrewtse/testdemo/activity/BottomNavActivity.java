package com.andrewtse.testdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.bottom_nav_view_fragment.BottomNavViewActivity;
import com.andrewtse.testdemo.tablayout_nav_fragment.TabLayoutNavActivity;

public class BottomNavActivity extends AppCompatActivity implements OnClickListener {

    private Button mBtnTabLayout;
    private Button mBtnBottomNav;
    private Button mBtnRadioGroup;
    private Button mBtnFragmentHost;
    private Button mBtnCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        mBtnTabLayout = findViewById(R.id.btn_tab_layout);
        mBtnBottomNav = findViewById(R.id.btn_bottom_navi);
        mBtnRadioGroup = findViewById(R.id.btn_radio_group);
        mBtnFragmentHost = findViewById(R.id.btn_fragment_host);
        mBtnCustomView = findViewById(R.id.btn_custom_view);

        mBtnTabLayout.setOnClickListener(this);
        mBtnBottomNav.setOnClickListener(this);
        mBtnRadioGroup.setOnClickListener(this);
        mBtnFragmentHost.setOnClickListener(this);
        mBtnCustomView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_tab_layout:
                intent = new Intent(this, TabLayoutNavActivity.class);
                break;
            case R.id.btn_bottom_navi:
                intent = new Intent(this, BottomNavViewActivity.class);
                break;
            case R.id.btn_radio_group:
                break;
            case R.id.btn_fragment_host:
                break;
            case R.id.btn_custom_view:
                break;
        }
        startActivity(intent);
    }
}
