package com.andrewtse.testdemo.bezier_view.water_wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * @author xk
 * @date 2019/2/12
 */
public class WaterWaveView extends View {

    private Paint mCirclePaint;
    private Paint mWavePaint;
    private Paint mTextPaint;

    private int screenWidth;
    private int screenHeight;
    private int amplitude = 100;

    private Path mPath;

    private float mProgress = 0;
    private float mTextProgress = 0;

    private Point mStartPoint = new Point();

    public WaterWaveView(Context context) {
        this(context, null);
    }

    public WaterWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Style.STROKE);
        mCirclePaint.setStrokeWidth(5f);
        mCirclePaint.setColor(Color.parseColor("#999999"));

        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Style.FILL);
        mWavePaint.setStrokeWidth(1f);
        mWavePaint.setColor(Color.GREEN);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Style.STROKE);
        mTextPaint.setTextSize(50f);
        mTextPaint.setColor(Color.parseColor("#999999"));
    }

    public void setProgress(float progress) {
        mTextProgress = progress;
        if (progress == 100) {
            mProgress = progress + amplitude;
        } else {
            mProgress = progress;
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(measureSize(400, widthMeasureSpec), measureSize(400, heightMeasureSpec));
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;
        screenHeight = h;
        mStartPoint.x = -screenWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        clipCircle(canvas);
        drawCircle(canvas);
        drawWave(canvas);
        drawText(canvas);
    }

    private void clipCircle(Canvas canvas) {
        Path circlePath = new Path();
        circlePath.addCircle(screenWidth / 2, screenHeight / 2, screenHeight / 2, Direction.CW);
        canvas.clipPath(circlePath);
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(screenHeight / 2, screenHeight / 2, screenHeight / 2, mCirclePaint);
    }

    private void drawWave(Canvas canvas) {
        int height = (int) (mProgress / 100 * screenHeight);
        mStartPoint.y = -height;
        canvas.translate(0, screenHeight);

        mPath = new Path();
        mPath.moveTo(mStartPoint.x, mStartPoint.y);

        int wave = screenWidth / 4;
        for (int i = 0; i < 4; i++) {
            int startX = mStartPoint.x + i * wave * 2;
            int endX = startX + 2 * wave;
            if (i % 2 == 0) {
                mPath.quadTo((startX + endX) / 2, mStartPoint.y + amplitude, endX, mStartPoint.y);
            } else {
                mPath.quadTo((startX + endX) / 2, mStartPoint.y - amplitude, endX, mStartPoint.y);
            }
        }

        mPath.lineTo(screenWidth, screenHeight / 2);
        mPath.lineTo(-screenWidth, screenHeight / 2);
        mPath.lineTo(-screenWidth, 0);
        mPath.close();
        canvas.drawPath(mPath, mWavePaint);
        mStartPoint.x += 10;
        if (mStartPoint.x > 0) {
            mStartPoint.x = -screenWidth;
        }
        mPath.reset();
    }

    private void drawText(Canvas canvas) {
        Rect targetRect = new Rect(0, -screenHeight, screenWidth, 0);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf((int) mTextProgress + "%"), targetRect.centerX(), baseline, mTextPaint);
    }

    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            default:
                break;
        }
        return result;
    }
}
