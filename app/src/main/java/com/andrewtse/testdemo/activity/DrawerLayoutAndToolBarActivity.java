package com.andrewtse.testdemo.activity;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.andrewtse.testdemo.fragment.ContentFragment;
import com.andrewtse.testdemo.R;
import java.util.ArrayList;
import java.util.List;

public class DrawerLayoutAndToolBarActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<ContentFragment> mFragments = new ArrayList<>();
    private ListView mLvList;

    private String[] mMenuTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout_and_tool_bar);

        initView();
        initToolbar();
        initMenuTitles();
        initFragments();
        initDrawerLayout();
    }

    private void initView() {
        mToolbar = findViewById(R.id.tool_bar);
        mDrawerLayout = findViewById(R.id.drawlayout);
        mLvList = findViewById(R.id.lv_list);
    }

    private void initToolbar() {
//        mToolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_bg));//设置Toolbar的背景颜色

        mToolbar.setNavigationIcon(R.mipmap.ic_launcher_round);//设置导航的图标
        mToolbar.setLogo(R.mipmap.ic_launcher_round);//设置logo

        mToolbar.setTitle("title");//设置标题
        mToolbar.setSubtitle("subtitle");//设置子标题

        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));//设置标题的字体颜色
        mToolbar.setSubtitleTextColor(getResources().getColor(android.R.color.white));//设置子标题的字体颜色

//        setSupportActionBar(mToolbar); //当使用代码填充xml布局时，不需要这行代码
        //设置右上角的填充菜单
        mToolbar.inflateMenu(R.menu.menu_tool_bar);
        //设置导航图标的点击事件 (无效，setToolbarNavigationClickListener有效)
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(DrawerLayoutAndToolBarActivity.this, "菜单", Toast.LENGTH_SHORT).show();
//                mDrawerLayout.openDrawer(GravityCompat.END);
//            }
//        });

        //设置右侧菜单项的点击事件
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                String tip = "";
                switch (id) {
                    case R.id.action_search:
                        tip = "搜索";
                        break;
                    case R.id.action_add:
                        tip = "添加";
                        break;
                    case R.id.action_setting:
                        tip = "设置";
                        break;
                    case R.id.action_help:
                        tip = "帮助";
                        break;
                }
                Toast.makeText(DrawerLayoutAndToolBarActivity.this, tip, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    /**
     * 设置左侧菜单条目的标题
     */
    private void initMenuTitles() {
        mMenuTitles = getResources().getStringArray(R.array.menuTitles);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mMenuTitles);
        mLvList.setAdapter(arrayAdapter);
        mLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchFragment(position);//切换fragment
                mDrawerLayout.closeDrawers();//收起DrawerLayout
            }
        });
    }

    private void initFragments() {
        ContentFragment fragment1 = new ContentFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString(ContentFragment.TEXT, getString(R.string.menu_local_music));
        fragment1.setArguments(bundle1);

        ContentFragment fragment2 = new ContentFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString(ContentFragment.TEXT, getString(R.string.menu_net_music));
        fragment2.setArguments(bundle2);

        mFragments.add(fragment1);
        mFragments.add(fragment2);
    }

    private void initDrawerLayout() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerToggle.syncState();//将ActionDrawerToggle与DrawerLayout的状态同步

        //实现抽屉从右边显示
        mDrawerToggle.setDrawerIndicatorEnabled(false);//去掉默认的三条横线的导航图标
        mDrawerToggle.setToolbarNavigationClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DrawerLayoutAndToolBarActivity.this, "菜单", Toast.LENGTH_SHORT).show();
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
        });
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        switchFragment(0);
    }

    /**
     * 切换fragment
     *
     * @param index 下标
     */
    private void switchFragment(int index) {
        ContentFragment contentFragment = mFragments.get(index);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, contentFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_tool_bar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        String tip = "";
//        switch (id) {
//            case android.R.id.home://对应navigationIcon的点击
//                tip = "菜单";
//                break;
//            case R.id.action_search:
//                tip = "搜索";
//                break;
//            case R.id.action_add:
//                tip = "添加";
//                break;
//            case R.id.action_setting:
//                tip = "设置";
//                break;
//            case R.id.action_help:
//                tip = "帮助";
//                break;
//        }
//        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
//        return super.onOptionsItemSelected(item);
//    }
}
