package com.andrewtse.testdemo.activity;

import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.view_drag_helper.VDHLinearLayout;

public class ViewDragHelperActivity extends AppCompatActivity {

    private VDHLinearLayout mVDHLiearLayout;
    private TextView mDragView;
    private TextView mAutobackView;
    private TextView mEdgeView;

    Point mAutobackPoint = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drag_helper);

        mDragView = findViewById(R.id.child1);
        mAutobackView = findViewById(R.id.child2);
        mEdgeView = findViewById(R.id.child3);
        mVDHLiearLayout = findViewById(R.id.root_vdh);
        init();
    }

    protected void init() {
        mVDHLiearLayout.createVDH(new VDHCallback());
        mAutobackView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAutobackPoint.set(mAutobackView.getLeft(), mAutobackView.getTop());
            }
        }, 0);
    }

    class VDHCallback extends ViewDragHelper.Callback {

        /**
         * 此例中 mEdgeView将无法被拖动
         * 此函数必须实现
         *
         * @return if false,那么child view将无法拖动（Drag）
         */
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            if (child == mEdgeView) {
                return false;
            }
            return true;
        }

        /**
         * 实现对child view x 轴方向的控制
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            int leftBound = mVDHLiearLayout.getPaddingLeft();
            int rightBound = mVDHLiearLayout.getWidth() - mVDHLiearLayout.getPaddingRight() - child.getWidth();
            //更加简洁的写法
            left = Math.min(Math.max(left, leftBound), rightBound);
            return left;
        }

        /**
         * 对child view y 轴方向的控制
         */
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            int topBound = mVDHLiearLayout.getPaddingTop();
            int bottomBound = mVDHLiearLayout.getHeight() - child.getHeight() - mVDHLiearLayout.getPaddingBottom();
            if (top < topBound) {
                top = topBound;
            } else if (top > bottomBound) {
                top = bottomBound;
            }
            return top;
        }

        /**
         * 此函数用于捕捉用户的ACTION_UP
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (releasedChild == mAutobackView) {
                mVDHLiearLayout.getViewDragHelper().settleCapturedViewAt(mAutobackPoint.x, mAutobackPoint.y);
                mVDHLiearLayout.invalidate();
            }
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            // super.onEdgeDragStarted(edgeFlags, pointerId);
            mVDHLiearLayout.getViewDragHelper().captureChildView(mEdgeView, pointerId);
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            Toast.makeText(ViewDragHelperActivity.this, "抓住我啦", Toast.LENGTH_SHORT).show();
        }
    }
}
