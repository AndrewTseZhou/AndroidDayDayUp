package com.andrewtse.testdemo.custom_canvas_view;

import android.content.Context;
import android.graphics.Bitmap;
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
import java.util.Stack;

/**
 * @author xk
 * @date 2018/11/1
 */
public class CustomCanvasView extends View {

    private Paint mPaint;
    private Path mPath;

    private PointF mStartPoint;
    private PointF mEndPoint;
    private PointF mControlPoint;

    private Stack<Path> mUndoStack;
    private Stack<Path> mRedoStack;

    private int size;
    private Bitmap mSrcBitmap;

    public CustomCanvasView(Context context) {
        this(context, null);
    }

    public CustomCanvasView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPath = new Path();
        mStartPoint = new PointF();
        mEndPoint = new PointF();
        mControlPoint = new PointF();

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.RED);

        mUndoStack = new Stack<>();
        mRedoStack = new Stack<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartPoint.x = event.getX();
                mStartPoint.y = event.getY();
                mPath = new Path();
                mPath.moveTo(mStartPoint.x, mStartPoint.y);
                invalidate();
                mControlPoint.x = mStartPoint.x;
                mControlPoint.y = mStartPoint.y;
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = event.getX();
                float endY = event.getY();
                mPath.quadTo(mControlPoint.x, mControlPoint.y, endX, endY);
//                mPath.cubicTo(mControlPoint.x, mControlPoint.y, mControlPoint2.x, mControlPoint2.y, endX, endY);
                invalidate();
                mControlPoint.x = endX;
                mControlPoint.y = endY;
                break;
            case MotionEvent.ACTION_UP:
                mUndoStack.push(mPath);
                mRedoStack.clear();
                mPath = null;
                size = mUndoStack.size();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mSrcBitmap, 0, 0, mPaint);
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);

        for (int i = 0; i < size; i++) {
            canvas.drawPath(mUndoStack.elementAt(i), mPaint);
        }

        if (mPath != null) {
            // 显示当前画的路径
            canvas.drawPath(mPath, mPaint);
        }
        canvas.restoreToCount(layerId);
    }

    public void clear() {
        if (!mUndoStack.empty()) {
            mUndoStack.clear();
            size = 0;
        }

        if (!mRedoStack.empty()) {
            mRedoStack.clear();
        }
        invalidate();
    }

    public void undo() {
        if (!mUndoStack.empty()) {
            Path lastOp = mUndoStack.pop();
            size = mUndoStack.size();
            mRedoStack.push(lastOp);
        }
        invalidate();
    }

    public void redo() {
        if (!mRedoStack.empty()) {
            Path lastOp = mRedoStack.pop();
            mUndoStack.push(lastOp);
            size = mUndoStack.size();
        }
        invalidate();
    }

    public void setSrcBitmap(Bitmap bitmap) {
        mSrcBitmap = bitmap;
        invalidate();
    }
}
