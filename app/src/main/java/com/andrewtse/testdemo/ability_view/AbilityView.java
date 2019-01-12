package com.andrewtse.testdemo.ability_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import java.util.HashMap;

/**
 * @author xk
 * @date 2018/12/30
 */
public class AbilityView extends View {

    private static final String TAG = "AbilityView";

    private float mRadius = dp(100);//外圆半径
    private float mLineWidth = dp(1);//线宽
    private float mInnerRadius;

    private Paint mLinePaint;
    private Paint mFillPaint;
    private Paint mTextPaint;
    private Paint mAbilityPaint;

    private Path mPath;
    private Path mAbilityPath;

    private HashMap<String, Integer> mData;//核心数据
    private String[] mAbilityInfo;
    private Integer[] mAbilityMark;
    private DataMapper mDataMapper;//数据与字符串映射规则

    public AbilityView(Context context) {
        this(context, null);
    }

    public AbilityView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbilityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setStyle(Style.STROKE);

        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setStrokeWidth(0.05f * mRadius);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mRadius * 0.1f);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mAbilityPaint = new Paint();
        mAbilityPaint.setAntiAlias(true);
        mAbilityPaint.setColor(0x8897C5FE);

        mPath = new Path();
        mAbilityPath = new Path();
        mData = new HashMap<>();
        mDataMapper = new WordMapper();//初始化DataMapper--默认WordMapper
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mRadius, mRadius);
        drawOutCircle(canvas);
        drawInnerCircle(canvas);
        drawInfoText(canvas);
        drawAbility(canvas);
    }

    /**
     * 绘制外圈
     *
     * @param canvas 画布
     */
    private void drawOutCircle(Canvas canvas) {
        canvas.save();

        canvas.drawCircle(0, 0, mRadius, mLinePaint);
        float r2 = mRadius - 0.08f * mRadius;//下圆半径
        canvas.drawCircle(0, 0, r2, mLinePaint);

        for (int i = 0; i < 22; i++) {//循环画出小黑条
            canvas.save();
            canvas.rotate(360 / 22f * i);
            canvas.drawLine(0, -mRadius, 0, -r2, mFillPaint);
            canvas.restore();
        }
        canvas.restore();
    }

    /**
     * 绘制内圈圆
     *
     * @param canvas 画布
     */
    private void drawInnerCircle(Canvas canvas) {
        canvas.save();

        mInnerRadius = 0.6f * mRadius;
        canvas.drawCircle(0, 0, mInnerRadius, mLinePaint);

        for (int i = 0; i < mData.size(); i++) {
            canvas.save();
            canvas.rotate(360f / mData.size() * i);
            mPath.moveTo(0, -mInnerRadius);
            mPath.rLineTo(0, mInnerRadius);//线的路径
            for (int j = 1; j < 6; j++) {
                mPath.moveTo(-mRadius * 0.02f, mInnerRadius / 6 * j);
                mPath.rLineTo(mRadius * 0.02f * 2, 0);
            }//加5条小线
            canvas.drawPath(mPath, mLinePaint);//绘制线
            canvas.restore();
        }
        canvas.restore();
    }

    /**
     * 绘制文字
     *
     * @param canvas 画布
     */
    private void drawInfoText(Canvas canvas) {
        float r2 = mRadius - 0.08f * mRadius;//下圆半径
        for (int i = 0; i < mData.size(); i++) {
            canvas.save();
            canvas.rotate(360.f / mData.size() * i + 180);
            mTextPaint.setTextSize(mRadius * 0.1f);
            canvas.drawText(mAbilityInfo[i], 0, r2 - 0.06f * mRadius, mTextPaint);
            mTextPaint.setTextSize(mRadius * 0.15f);
            canvas.drawText(mDataMapper.abilityMark2Str(mAbilityMark[i]), 0, r2 - 0.18f * mRadius, mTextPaint);
            canvas.restore();
        }
        mTextPaint.setTextSize(mRadius * 0.07f);
        for (int k = 0; k < mDataMapper.getMapper().length; k++) {
            canvas.drawText(mDataMapper.getMapper()[k], mRadius * 0.06f, mInnerRadius / mData.size() * (k + 1) + mRadius * 0.02f - mInnerRadius, mTextPaint);
        }
    }

    /**
     * 绘制能力面
     */
    private void drawAbility(Canvas canvas) {
        float step = mInnerRadius / (mDataMapper.getMapper().length + 1);//每小段的长度
        mAbilityPath.moveTo(0, -mAbilityMark[0] / 20.f * step);//起点
        for (int i = 1; i < mData.size(); i++) {
            float mark = mAbilityMark[i] / 20.f;
            mAbilityPath.lineTo((float) (mark * step * Math.cos(Math.PI / 180 * (360.f / mData.size() * i - 90))),
                                (float) (mark * step * Math.sin(Math.PI / 180 * (360.f / mData.size() * i - 90))));
        }
        mAbilityPath.close();
        canvas.drawPath(mAbilityPath, mAbilityPaint);
    }

    private float dp(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRadius = MeasureSpec.getSize(widthMeasureSpec) / 2;
        mInnerRadius = 0.6f * mRadius;
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public DataMapper getDataMapper() {
        return mDataMapper;
    }

    public void setDataMapper(DataMapper dataMapper) {
        mDataMapper = dataMapper;
    }

    public HashMap<String, Integer> getData() {
        return mData;
    }

    public void setData(HashMap<String, Integer> data) {
        mData = data;
        mAbilityInfo = mData.keySet().toArray(new String[mData.size()]);
        mAbilityMark = mData.values().toArray(new Integer[mData.size()]);
        invalidate();
    }
}
