package com.andrewtse.testdemo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrewtse.testdemo.R
import com.andrewtse.testdemo.time_axle.DividerItemDecoration
import com.andrewtse.testdemo.time_axle.MyAdapter
import kotlinx.android.synthetic.main.activity_time_axle.*


class TimeAxleActivity : AppCompatActivity() {

    private lateinit var listItem: ArrayList<HashMap<String, Any>>
    private lateinit var myAdapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_axle)

        // 初始化显示的数据
        initData()

        // 绑定数据到RecyclerView
        initView()
    }

    // 初始化显示的数据
    private fun initData() {
        listItem = ArrayList()/*在数组中存放数据*/

        val map1 = HashMap<String, Any>()
        val map2 = HashMap<String, Any>()
        val map3 = HashMap<String, Any>()
        val map4 = HashMap<String, Any>()
        val map5 = HashMap<String, Any>()
        val map6 = HashMap<String, Any>()

        map1["ItemTitle"] = "美国谷歌公司已发出"
        map1["ItemText"] = "发件人:谷歌 CEO Sundar Pichai"
        listItem.add(map1)

        map2["ItemTitle"] = "国际顺丰已收入"
        map2["ItemText"] = "等待中转"
        listItem.add(map2)

        map3["ItemTitle"] = "国际顺丰转件中"
        map3["ItemText"] = "下一站中国"
        listItem.add(map3)

        map4["ItemTitle"] = "中国顺丰已收入"
        map4["ItemText"] = "下一站广州华南理工大学"
        listItem.add(map4)

        map5["ItemTitle"] = "中国顺丰派件中"
        map5["ItemText"] = "等待派件"
        listItem.add(map5)

        map6["ItemTitle"] = "北京物资学院已签收"
        map6["ItemText"] = "收件人:Andrew Tse"
        listItem.add(map6)
    }

    // 绑定数据到RecyclerView
    private fun initView() {
        //使用线性布局
        val layoutManager = LinearLayoutManager(this)
        rl_time_axle.layoutManager = layoutManager
        rl_time_axle.setHasFixedSize(true)

        //用自定义分割线类设置分割线
        rl_time_axle.addItemDecoration(DividerItemDecoration(this))

        //为ListView绑定适配器
        myAdapter = MyAdapter(this, listItem)
        rl_time_axle.adapter = myAdapter
    }
}
