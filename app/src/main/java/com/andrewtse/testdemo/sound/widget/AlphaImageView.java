package com.andrewtse.testdemo.sound.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author xk
 * @date 2019/1/11
 */
public class AlphaImageView extends AppCompatImageView {

    public AlphaImageView(Context context) {
        super(context);
    }

    public AlphaImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 1f, .1f, 1f).setDuration(300);
            alpha.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mOnAlphaListener != null) {
                        mOnAlphaListener.click(AlphaImageView.this);
                    }
                }
            });
            alpha.start();
        }

        return true;
    }

    public interface OnAlphaListener {

        void click(View view);
    }

    private OnAlphaListener mOnAlphaListener;

    public void setOnAlphaListener(OnAlphaListener onAlphaListener) {
        mOnAlphaListener = onAlphaListener;
    }
}
