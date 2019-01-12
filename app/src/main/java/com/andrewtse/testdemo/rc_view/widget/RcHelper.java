package com.andrewtse.testdemo.rc_view.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import com.andrewtse.testdemo.R;
import java.util.ArrayList;

/**
 * @author xk
 * @date 2018/9/22
 */
public class RcHelper {

    //半径信息
    public float[] radiusInfo = new float[8];
    //裁剪区域路径
    public Path mClipPath;
    //画笔
    public Paint mPaint;
    //圆形
    public boolean mRoundAsCircle = false;
    //默认描边颜色
    public int mDefaultStrokeColor;

    public int mStrokeColor;
    //描边颜色状态
    public ColorStateList mStrokeColorStateList;
    //描边宽度
    public int mStrokeWidth;
    //是否裁剪背景
    public boolean mClipBackground;
    //内容区域
    public Region mAreaRegion;
    //边缘修复
    public int mEdgeFix = 10;
    //画布图层大小
    public RectF mLayer;

    public void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RCAttrs);
        mRoundAsCircle = ta.getBoolean(R.styleable.RCAttrs_round_as_circle, false);
        mStrokeColorStateList = ta.getColorStateList(R.styleable.RCAttrs_stroke_color);
        if (mStrokeColorStateList != null) {
            mStrokeColor = mStrokeColorStateList.getDefaultColor();
            mDefaultStrokeColor = mStrokeColorStateList.getDefaultColor();
        } else {
            mStrokeColor = Color.WHITE;
            mDefaultStrokeColor = Color.WHITE;
        }

        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.RCAttrs_stroke_width, 0);
        mClipBackground = ta.getBoolean(R.styleable.RCAttrs_clip_background, false);
        int roundCorner = ta.getDimensionPixelSize(R.styleable.RCAttrs_round_corner, 0);
        int roundCornerTopLeft = ta.getDimensionPixelSize(R.styleable.RCAttrs_round_corner_top_left, 0);
        int roundCornerTopRight = ta.getDimensionPixelSize(R.styleable.RCAttrs_round_corner_top_right, 0);
        int roundCornerBottomLeft = ta.getDimensionPixelSize(R.styleable.RCAttrs_round_corner_bottom_left, 0);
        int roundCornerBottomRight = ta.getDimensionPixelSize(R.styleable.RCAttrs_round_corner_bottom_right, 0);
        ta.recycle();

        radiusInfo[0] = roundCornerTopLeft;
        radiusInfo[1] = roundCornerTopLeft;

        radiusInfo[2] = roundCornerTopRight;
        radiusInfo[3] = roundCornerTopRight;

        radiusInfo[4] = roundCornerBottomRight;
        radiusInfo[5] = roundCornerBottomRight;

        radiusInfo[6] = roundCornerBottomLeft;
        radiusInfo[7] = roundCornerBottomLeft;

        mLayer = new RectF();
        mClipPath = new Path();
        mAreaRegion = new Region();
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
    }

    public void onSizeChanged(View view, int width, int height) {
        mLayer.set(0, 0, width, height);
        refreshRegion(view);
    }

    public void refreshRegion(View view) {
        int width = (int) mLayer.width();
        int height = (int) mLayer.height();

        RectF area = new RectF();
        area.left = view.getPaddingLeft();
        area.top = view.getPaddingTop();
        area.right = width - view.getPaddingRight();
        area.bottom = height - view.getPaddingBottom();
        mClipPath.reset();

        if (mRoundAsCircle) {
            float d = area.width() >= area.height() ? area.height() : area.width();
            float r = d / 2;
            PointF center = new PointF(width / 2, height / 2);
            mClipPath.addCircle(center.x, center.y, r, Direction.CW);
        } else {
            mClipPath.addRoundRect(area, radiusInfo, Direction.CW);
        }

        mClipPath.moveTo(-mEdgeFix, -mEdgeFix);
        mClipPath.moveTo(width + mEdgeFix, height + mEdgeFix);

        Region clip = new Region((int) area.left, (int) area.top, (int) area.right, (int) area.bottom);
        mAreaRegion.setPath(mClipPath, clip);
    }

    public void onClipDraw(Canvas canvas) {
        if (mStrokeWidth > 0) {
            //半透明描边，将与描边区域重叠的内容裁剪掉
            mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(mStrokeWidth * 2);
            mPaint.setStyle(Style.STROKE);
            canvas.drawPath(mClipPath, mPaint);

            //绘制描边
            mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
            mPaint.setColor(mStrokeColor);
            mPaint.setStyle(Style.STROKE);
            canvas.drawPath(mClipPath, mPaint);
        }

        mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Style.FILL);
        canvas.drawPath(mClipPath, mPaint);
    }

    public boolean mChecked;
    public OnCheckedChangeListener mOnCheckedChangeListener;

    public void drawableStateChanged(View view) {
        if (view instanceof RcAttrs) {
            ArrayList<Integer> stateArrayList = new ArrayList<>();
            if (view instanceof Checkable) {
                stateArrayList.add(android.R.attr.state_checkable);
                if (((Checkable) view).isChecked()) {
                    stateArrayList.add(android.R.attr.state_checked);
                }
            }
            if (view.isEnabled()) {
                stateArrayList.add(android.R.attr.state_enabled);
            }
            if (view.isFocused()) {
                stateArrayList.add(android.R.attr.state_focused);
            }
            if (view.isPressed()) {
                stateArrayList.add(android.R.attr.state_pressed);
            }
            if (view.isHovered()) {
                stateArrayList.add(android.R.attr.state_hovered);
            }
            if (view.isSelected()) {
                stateArrayList.add(android.R.attr.state_selected);
            }
            if (view.isActivated()) {
                stateArrayList.add(android.R.attr.state_activated);
            }
            if (view.hasWindowFocus()) {
                stateArrayList.add(android.R.attr.state_window_focused);
            }

            if (mStrokeColorStateList != null && mStrokeColorStateList.isStateful()) {
                int[] stateList = new int[stateArrayList.size()];
                for (int i = 0; i < stateArrayList.size(); i++) {
                    stateList[i] = stateArrayList.get(i);
                }
                int stateColor = mStrokeColorStateList.getColorForState(stateList, mDefaultStrokeColor);
                ((RcAttrs) view).setStrokeColor(stateColor);
            }
        }
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(View view, boolean isChecked);
    }
}
