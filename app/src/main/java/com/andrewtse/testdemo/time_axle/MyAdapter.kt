package com.andrewtse.testdemo.time_axle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andrewtse.testdemo.R


/**
 * @author Andrew Tse
 * @date 2019-06-03
 */
open class MyAdapter(context: Context, list: ArrayList<HashMap<String, Any>>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var listItem: ArrayList<HashMap<String, Any>> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(inflater.inflate(R.layout.list_cell, null))
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val vh: MyViewHolder = holder
        vh.title.text = listItem[position]["ItemTitle"] as String
        vh.text.text = listItem[position]["ItemText"] as String
    }

    inner class MyViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val title: TextView = root.findViewById(R.id.item_title)
        val text: TextView = root.findViewById(R.id.item_text)
    }
}