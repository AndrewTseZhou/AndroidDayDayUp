package com.andrewtse.testdemo.temperature_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.andrewtse.testdemo.R;

/**
 * @author xk
 * @date 2018/10/8
 */
public class TempView extends View {

    private Context mContext;

    //温度的最大范围
    private float maxValue = 100f;
    //当前温度
    private float mCurrentValue;
    //分段颜色
    private static final int[] SELECTION_COLORS = {Color.GREEN, Color.YELLOW, Color.RED};
    //圆角矩形paint
    private Paint mRoundRectPaint;
    //宽度
    private int mWidth;
    //高度
    private int mHeight;

    private float selection;
    //文本paint
    private Paint mTextPaint;
    //三角形指针paint
    private Paint mTrianglePaint;
    //三角形指针path
    private Path mTrianglePath;
    //指针宽高
    private int mDefaultIndicatorWidth = dipToPx(10);
    private int mDefaultIndicatorHeight = dipToPx(8);
    //圆角矩形高度
    private int mDefaultTempHeight = dipToPx(20);
    //文字大小
    private int mDefaultTextSize = 30;
    //文字间隙
    private int mTextSpace = dipToPx(5);

    private RectF mRectProgressBg;

    private LinearGradient mShader;

    public TempView(Context context) {
        this(context, null);
    }

    public TempView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TempView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;

        mRoundRectPaint = new Paint();
        mRoundRectPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mDefaultTextSize);
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setColor(mContext.getResources().getColor(R.color.colorPressedColor));

        mTrianglePath = new Path();
        mTrianglePaint = new Paint();
        mTrianglePaint.setAntiAlias(true);
        mTrianglePaint.setStyle(Style.FILL);
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

        //view的高度=渐变圆角矩形高度+指针高度+文本高度+文本与指针的间隙
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = mDefaultTempHeight + mDefaultIndicatorHeight + mDefaultTextSize + mTextSpace;
        } else {
            mHeight = heightSpecSize;
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //确定圆角矩形的范围,在TmepView的最底部,top位置为总高度-圆角矩形的高度
        mRectProgressBg = new RectF(0, mHeight - mDefaultTempHeight, mWidth, mHeight);
        mShader = new LinearGradient(0, mHeight - mDefaultTempHeight, mWidth, mHeight, SELECTION_COLORS, null, Shader.TileMode.MIRROR);
        mRoundRectPaint.setShader(mShader);
        //绘制圆角矩形 mDefaultTempHeight / 2确定圆角的圆心位置
        canvas.drawRoundRect(mRectProgressBg, mDefaultTempHeight / 2, mDefaultTempHeight / 2, mRoundRectPaint);

        //当前位置占比
        selection = mCurrentValue / maxValue;
        //绘制指针 指针的位置在当前温度的位置 也就是三角形的顶点落在当前温度的位置
        //定义三角形的左边点的坐标 x= tempView的宽度*当前位置占比-三角形的宽度/2  y=tempView的高度-圆角矩形的高度
        mTrianglePath.moveTo(mWidth * selection - mDefaultIndicatorWidth / 2, mHeight - mDefaultTempHeight);
        //定义三角形的右边点的坐标 = tempView的宽度*当前位置占比+三角形的宽度/2  y=tempView的高度-圆角矩形的高度
        mTrianglePath.lineTo(mWidth * selection + mDefaultIndicatorWidth / 2, mHeight - mDefaultTempHeight);
        //定义三角形的左边点的坐标 x= tempView的宽度*当前位置占比  y=tempView的高度-圆角矩形的高度-三角形的高度
        mTrianglePath.lineTo(mWidth * selection, mHeight - mDefaultTempHeight - mDefaultIndicatorHeight);
        mTrianglePath.close();
        mTrianglePaint.setShader(mShader);
        canvas.drawPath(mTrianglePath, mTrianglePaint);

        //绘制文本
        String text = (int) mCurrentValue + "°c";
        //确定文本的位置 x=tempViwe的宽度*当前位置占比 y=tempView的高度-圆角矩形的高度-三角形的高度-文本的间隙
        canvas.drawText(text, mWidth * selection, mHeight - mDefaultTempHeight - mDefaultIndicatorHeight - mTextSpace, mTextPaint);
    }

    /***
     * 设置最大的温度值
     * @param maxValue
     */
    public void setMaxCount(float maxValue) {
        this.maxValue = maxValue;
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
        mTrianglePath.reset();
    }

    /**
     * 设置温度指针的大小
     */
    public void setIndicatorSize(int width, int height) {

        this.mDefaultIndicatorWidth = width;
        this.mDefaultIndicatorHeight = height;
    }

    public void setTempHeight(int height) {
        this.mDefaultTempHeight = height;
    }

    public void setTextSize(int textSize) {
        this.mDefaultTextSize = textSize;
    }

    public float getMaxCount() {
        return maxValue;
    }

    public float getCurrentCount() {
        return mCurrentValue;
    }

    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }
}
