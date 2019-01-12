package com.andrewtse.testdemo.bottom_nav_view_fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.fragment_data.DataGenerator;

public class BottomNavViewActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private Fragment[] mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav_view);

        mFragments = DataGenerator.getFragments("BottomNavigationView Tab");

        initView();
    }

    private void initView() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                onTabItemSelected(menuItem.getItemId());
                return true;
            }
        });
        // 由于第一次进来没有回调onNavigationItemSelected，因此需要手动调用一下切换状态的方法
        onTabItemSelected(R.id.tab_menu_home);
    }

    private void onTabItemSelected(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.tab_menu_home:
                fragment = mFragments[0];
                break;
            case R.id.tab_menu_discovery:
                fragment = mFragments[1];
                break;

            case R.id.tab_menu_attention:
                fragment = mFragments[2];
                break;
            case R.id.tab_menu_profile:
                fragment = mFragments[3];
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_container, fragment).commit();
        }
    }
}
