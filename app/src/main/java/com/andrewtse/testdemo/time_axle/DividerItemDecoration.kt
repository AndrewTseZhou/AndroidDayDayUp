package com.andrewtse.testdemo.time_axle

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.andrewtse.testdemo.R

/**
 * @author Andrew Tse
 * @date 2019-06-03
 */
class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    /**
     * 写右边字的画笔(具体信息)
     */
    private val mPaint: Paint

    /**
     * 写左边日期字的画笔( 时间 + 日期)
     */
    private val mPaint1: Paint
    private val mPaint2: Paint

    /**
     * 左上偏移长度
     */
    private val mItemViewLeftInterval: Int
    private val mItemViewTopInterval: Int

    /**
     * 轴点半径
     */
    private val mCircleRadius: Int

    /**
     * 图标
     */
    private val mIcon: Bitmap

    /**
     * 在构造函数里进行绘制的初始化，如画笔属性设置等
     */
    init {
        // 轴点画笔(红色)
        mPaint = Paint()
        mPaint.color = Color.RED

        // 左边时间文本画笔(蓝色)
        // 此处设置了两只分别设置 时分 & 年月
        mPaint1 = Paint()
        mPaint1.color = Color.BLUE
        mPaint1.textSize = 30f

        mPaint2 = Paint()
        mPaint2.color = Color.BLUE


        // 赋值ItemView的左偏移长度为200
        mItemViewLeftInterval = 200

        // 赋值ItemView的上偏移长度为50
        mItemViewTopInterval = 50

        // 赋值轴点圆的半径为10
        mCircleRadius = 10

        // 获取图标资源
        mIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.logo)
    }

    /**
     * 设置ItemView 左 & 上偏移长度
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        // 设置ItemView的左 & 上偏移长度分别为200 px & 50px,即此为onDraw()可绘制的区域
        outRect.set(mItemViewLeftInterval, mItemViewTopInterval, 0, 0)
    }

    /**
     * 在间隔区域里绘制时光轴线 & 时间文本
     *
     * @param c
     * @param parent
     * @param state
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        //获取RecyclerView的Child view的个数
        val childCount = parent.childCount
        //遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
        for (i in 0 until childCount) {
            // 获取每个Item对象
            val child = parent.getChildAt(i)
            //绘制轴点
            //轴点 = 圆 = 圆心(x,y)
            val centerx = (child.left - mItemViewLeftInterval / 3).toFloat()
            val centery = (child.top - mItemViewTopInterval + (mItemViewTopInterval + child.height) / 2).toFloat()
            //绘制轴点圆
            //c.drawCircle(centerx, centery, mCircleRadius, mPaint);

            //通过Canvas绘制角标
            c.drawBitmap(mIcon, centerx - mCircleRadius, centery - mCircleRadius, mPaint)

            //绘制上半轴线
            //上端点坐标(x,y)
            val upLineUpY = (child.top - mItemViewTopInterval).toFloat()
            //下端点坐标(x,y)
            val upLineBottomY = centery - mCircleRadius
            //绘制上半部轴线
            c.drawLine(centerx, upLineUpY, centerx, upLineBottomY, mPaint)

            //绘制下半轴线
            //上端点坐标(x,y)
            val bottomUpY = centery + mCircleRadius
            //下端点坐标(x,y)
            val bottomLineBottomY = child.bottom.toFloat()
            //绘制下半部轴线
            c.drawLine(centerx, bottomUpY, centerx, bottomLineBottomY, mPaint)

            //绘制左边时间文本
            //获取每个Item的位置
            val index = parent.getChildAdapterPosition(child)
            //设置文本起始坐标
            val textX = (child.left - mItemViewLeftInterval * 5 / 6).toFloat()

            //根据Item位置设置时间文本
            when (index) {
                0 -> {
                    //设置日期绘制位置
                    c.drawText("13:40", textX, upLineBottomY, mPaint1)
                    c.drawText("2017.4.03", textX + 5, upLineBottomY + 20, mPaint2)
                }
                1 -> {
                    //设置日期绘制位置
                    c.drawText("17:33", textX, upLineBottomY, mPaint1)
                    c.drawText("2017.4.03", textX + 5, upLineBottomY + 20, mPaint2)
                }
                2 -> {
                    //设置日期绘制位置
                    c.drawText("18:11", textX, upLineBottomY, mPaint1)
                    c.drawText("2017.4.03", textX + 5, upLineBottomY + 20, mPaint2)
                }
                3 -> {
                    //设置日期绘制位置
                    c.drawText("9:40", textX, upLineBottomY, mPaint1)
                    c.drawText("2017.4.04", textX + 5, upLineBottomY + 20, mPaint2)
                }
                4 -> {
                    //设置日期绘制位置
                    c.drawText("13:20", textX, upLineBottomY, mPaint1)
                    c.drawText("2017.4.04", textX + 5, upLineBottomY + 20, mPaint2)
                }
                5 -> {
                    //设置日期绘制位置
                    c.drawText("20:40", textX, upLineBottomY, mPaint1)
                    c.drawText("2017.4.04", textX + 5, upLineBottomY + 20, mPaint2)
                }
                else -> c.drawText("已签收", textX, upLineBottomY, mPaint1)
            }
        }
    }
}
