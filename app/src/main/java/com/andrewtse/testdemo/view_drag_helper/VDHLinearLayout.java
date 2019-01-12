package com.andrewtse.testdemo.view_drag_helper;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author xk
 * @date 2018/12/18
 */
public class VDHLinearLayout extends LinearLayout {

    private ViewDragHelper mViewDragHelper = null;

    public VDHLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        createVDH(null);
    }

    public void createVDH(ViewDragHelper.Callback callback) {
        if (callback == null) {
            mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
                @Override
                public boolean tryCaptureView(@NonNull View child, int pointerId) {
                    return true;
                }
            });
        } else {
            mViewDragHelper = ViewDragHelper.create(this, 1.0f, callback);
        }
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mViewDragHelper == null) {
            return super.dispatchTouchEvent(event);
        }
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mViewDragHelper == null) {
            return super.onInterceptTouchEvent(ev);
        }
        int keyCode = ev.getAction();
        if (keyCode == MotionEvent.ACTION_CANCEL) {
            mViewDragHelper.cancel();
            return false;
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mViewDragHelper.continueSettling(true)){
            invalidate();
        }
    }

    public ViewDragHelper getViewDragHelper() {
        return mViewDragHelper;
    }
}
