package com.andrewtse.testdemo.rc_view.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import com.andrewtse.testdemo.rc_view.widget.RcAttrs;
import com.andrewtse.testdemo.rc_view.widget.RcHelper;
import com.andrewtse.testdemo.rc_view.widget.RcHelper.OnCheckedChangeListener;

/**
 * @author xk
 * @date 2018/9/24
 */
@SuppressLint("AppCompatCustomView")
public class RcImageView extends ImageView implements Checkable, RcAttrs {

    private RcHelper mRcHelper;

    public RcImageView(Context context) {
        this(context, null);
    }

    public RcImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RcImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRcHelper = new RcHelper();
        mRcHelper.initAttrs(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mRcHelper.mChecked != checked) {
            mRcHelper.mChecked = checked;
            refreshDrawableState();
            if (mRcHelper.mOnCheckedChangeListener != null) {
                mRcHelper.mOnCheckedChangeListener.onCheckedChanged(this, mRcHelper.mChecked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return mRcHelper.mChecked;
    }

    @Override
    public void toggle() {
        setChecked(mRcHelper.mChecked);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRcHelper.onSizeChanged(this, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        mRcHelper.refreshRegion(this);
        if (mRcHelper.mClipBackground) {
            canvas.save();
            canvas.clipPath(mRcHelper.mClipPath);
            super.draw(canvas);
            canvas.restore();
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayer(mRcHelper.mLayer, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        mRcHelper.onClipDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mRcHelper.drawableStateChanged(this);
    }

    @Override
    public void setClipBackground(boolean clipBackground) {
        mRcHelper.mClipBackground = clipBackground;
        invalidate();
    }

    @Override
    public void setRoundAsCircle(boolean roundAsCircle) {
        mRcHelper.mRoundAsCircle = roundAsCircle;
        invalidate();
    }

    @Override
    public void setRadius(float radius) {
        for (int i = 0; i < mRcHelper.radiusInfo.length; i++) {
            mRcHelper.radiusInfo[i] = radius;
        }
        invalidate();
    }

    @Override
    public void setTopLeftRadius(float topLeftRadius) {
        mRcHelper.radiusInfo[0] = topLeftRadius;
        mRcHelper.radiusInfo[1] = topLeftRadius;
        invalidate();
    }

    @Override
    public void setTopRightRadius(float topRightRadius) {
        mRcHelper.radiusInfo[2] = topRightRadius;
        mRcHelper.radiusInfo[3] = topRightRadius;
        invalidate();
    }

    @Override
    public void setBottomLeftRadius(float bottomLeftRadius) {
        mRcHelper.radiusInfo[4] = bottomLeftRadius;
        mRcHelper.radiusInfo[5] = bottomLeftRadius;
        invalidate();
    }

    @Override
    public void setBottomRightRadius(float bottomRightRadius) {
        mRcHelper.radiusInfo[6] = bottomRightRadius;
        mRcHelper.radiusInfo[7] = bottomRightRadius;
        invalidate();
    }

    @Override
    public void setStrokeWidth(int strokeWidth) {
        mRcHelper.mStrokeWidth = strokeWidth;
        invalidate();
    }

    @Override
    public void setStrokeColor(int strokeColor) {
        mRcHelper.mStrokeColor = strokeColor;
        invalidate();
    }

    @Override
    public boolean isClipBackground() {
        return mRcHelper.mClipBackground;
    }

    @Override
    public boolean isRoundAsCircle() {
        return mRcHelper.mRoundAsCircle;
    }

    @Override
    public float getRadius() {
        return 0;
    }

    @Override
    public float getTopLeftRadius() {
        return mRcHelper.radiusInfo[0];
    }

    @Override
    public float getTopRightRadius() {
        return mRcHelper.radiusInfo[2];
    }

    @Override
    public float getBottomLeftRadius() {
        return mRcHelper.radiusInfo[4];
    }

    @Override
    public float getBottomRightRadius() {
        return mRcHelper.radiusInfo[6];
    }

    @Override
    public int getStrokeWidth() {
        return mRcHelper.mStrokeWidth;
    }

    @Override
    public int getStrokeColor() {
        return mRcHelper.mStrokeColor;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mRcHelper.mOnCheckedChangeListener = listener;
    }
}
