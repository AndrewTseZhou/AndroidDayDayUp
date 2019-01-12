package com.andrewtse.testdemo.temperature_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * @author xk
 * @date 2018/12/31
 */
public class TempView2 extends View {

    private Paint mLinePaint;
    private Paint mTextPaint;
    private Paint mIndicatorPaint;

    private Path mPath;

    private int mWidth;
    private int mHeight;
    private float mRadius = toDp(80);
    private float mLineStrokeWidth = toDp(2);
    private float mTextSize = toDp(10);
    private float maxValue = 100f;
    private float mCurrentValue;

    private static final int[] SELECTION_COLORS = {Color.GREEN, Color.YELLOW, Color.RED};

    private SweepGradient mSweepGradient;

    public TempView2(Context context) {
        this(context, null);
    }

    public TempView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(mLineStrokeWidth);
        mLinePaint.setStyle(Style.STROKE);

        mSweepGradient = new SweepGradient(0, 0, SELECTION_COLORS, null);
        mLinePaint.setShader(mSweepGradient);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Align.CENTER);

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setStrokeWidth(mLineStrokeWidth);
        mIndicatorPaint.setStyle(Style.STROKE);

        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mRadius + mLineStrokeWidth, mRadius + mLineStrokeWidth);

        drawDial(canvas);
        drawIndicator(canvas);

        canvas.save();
        canvas.drawText(mCurrentValue + "℃", 0, 0, mTextPaint);
        canvas.restore();
    }

    private void drawDial(Canvas canvas) {
        for (int i = 0; i < 12; i++) {
            canvas.save();
            canvas.rotate(30 * i);
            mPath.moveTo(0, -mRadius);
            mPath.rLineTo(0, mRadius * 0.1f);
            canvas.drawPath(mPath, mLinePaint);
            canvas.restore();
        }

        canvas.save();
        canvas.drawCircle(0, 0, mRadius, mLinePaint);
        canvas.restore();
    }

    private void drawIndicator(Canvas canvas) {
        canvas.save();
        float percent = mCurrentValue / maxValue * 360;
        canvas.rotate(percent);
        canvas.drawLine(0, 0, 0, -mRadius * 0.8f, mIndicatorPaint);
        canvas.restore();
    }

    /***
     * 设置当前的温度
     * @param currentCount
     */
    public void setCurrentCount(float currentCount) {
        if (currentCount > maxValue) {
            mCurrentValue = maxValue - 5;
        } else if (currentCount < 0f) {
            mCurrentValue = 0f + 5;
        } else {
            mCurrentValue = currentCount;
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.EXACTLY) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }

        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = (int) (2 * mRadius + 2 * mLineStrokeWidth);
        } else {
            mHeight = heightSpecSize;
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    private float toDp(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
