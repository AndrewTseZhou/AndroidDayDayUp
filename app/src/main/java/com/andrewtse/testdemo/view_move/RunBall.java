package com.andrewtse.testdemo.view_move;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author xk
 * @date 2018/12/22
 */
public class RunBall extends View {

    private ValueAnimator mAnimator;
    private Ball mBall;
    private Paint mPaint;
    private Point mCoo;//坐标系

    private float defaultR = 20;
    private int defaultColor = Color.BLUE;
    private float defaultVX = 10;
    private float defaultVY = 0;
    private float defaultAX = 0;
    private float defaultAY = 0;

    private int mStatus = 0;

    public RunBall(Context context) {
        this(context, null);
    }

    public RunBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCoo = new Point(500, 500);

        mBall = new Ball();
        mBall.color = defaultColor;
        mBall.r = defaultR;
        mBall.vX = defaultVX;
        mBall.vY = defaultVY;
        mBall.aX = defaultAX;
        mBall.aY = defaultAY;
        mBall.x = 0;
        mBall.y = 0;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setRepeatCount(-1);
        mAnimator.setDuration(1000);
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateBall();
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mCoo.x, mCoo.y);
        drawBall(canvas, mBall);
        canvas.restore();
    }

    private void drawBall(Canvas canvas, Ball ball) {
        mPaint.setColor(ball.color);
        canvas.drawCircle(ball.x, ball.y, ball.r, mPaint);
    }

    private void updateBall() {
        switch (mStatus) {
            case 0:
                mBall.x += mBall.vX;
                break;
            case 1:
                mBall.x += mBall.vX;
                if (mBall.x > 400 || mBall.x < -400) {
                    mBall.vX = -mBall.vX;
                }
                break;
            case 2:

                break;
        }
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAnimator.start();
                break;
            case MotionEvent.ACTION_UP:
                mAnimator.pause();
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, 200);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, 200);
        }
    }
}
