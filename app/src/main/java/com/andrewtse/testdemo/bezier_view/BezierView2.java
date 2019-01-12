package com.andrewtse.testdemo.bezier_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author xk
 * @date 2018/10/9
 */
public class BezierView2 extends View {

    private Paint mPaint;

    private int mCenterX;
    private int mCenterY;

    private PointF mStart;
    private PointF mEnd;
    private PointF mControl1;
    private PointF mControl2;

    private int controlPoint = 1;

    public BezierView2(Context context) {
        this(context, null);
    }

    public BezierView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BezierView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Style.STROKE);

        mStart = new PointF(0, 0);
        mEnd = new PointF(0, 0);
        mControl1 = new PointF(0, 0);
        mControl2 = new PointF(0, 0);
    }

    public void setControlPoint(int controlPoint) {
        this.controlPoint = controlPoint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCenterX = w / 2;
        mCenterY = h / 2;

        // 初始化数据点和控制点的位置
        mStart.x = mCenterX - 200;
        mStart.y = mCenterY;
        mEnd.x = mCenterX + 200;
        mEnd.y = mCenterY;
        mControl1.x = mCenterX;
        mControl1.y = mCenterY - 100;
        mControl2.x = mCenterX;
        mControl2.y = mCenterY - 100;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (controlPoint == 1) {
            mControl1.x = event.getX();
            mControl1.y = event.getY();
        } else {
            mControl2.x = event.getX();
            mControl2.y = event.getY();
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制数据点和控制点
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(20);
        canvas.drawPoint(mStart.x, mStart.y, mPaint);
        canvas.drawPoint(mEnd.x, mEnd.y, mPaint);
        canvas.drawPoint(mControl1.x, mControl1.y, mPaint);
        canvas.drawPoint(mControl2.x, mControl2.y, mPaint);

        //绘制辅助线
        mPaint.setStrokeWidth(4);
        canvas.drawLine(mStart.x, mStart.y, mControl1.x, mControl1.y, mPaint);
        canvas.drawLine(mControl1.x, mControl1.y, mControl2.x, mControl2.y, mPaint);
        canvas.drawLine(mControl2.x, mControl2.y, mEnd.x, mEnd.y, mPaint);

        //绘制贝塞尔曲线
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(8);

        Path path = new Path();
        path.moveTo(mStart.x, mStart.y);
        path.cubicTo(mControl1.x, mControl1.y, mControl2.x, mControl2.y, mEnd.x, mEnd.y);
        canvas.drawPath(path, mPaint);
    }
}
