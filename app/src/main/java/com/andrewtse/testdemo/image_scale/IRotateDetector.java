package com.andrewtse.testdemo.image_scale;

import android.view.MotionEvent;

/**
 * @author xk
 * @date 2018/12/25
 */
public interface IRotateDetector {

    /**
     * handle rotation in onTouchEvent
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    boolean onTouchEvent(MotionEvent event);

    /**
     * is the Gesture Rotate
     *
     * @return true:rotating;false,otherwise
     */
    boolean isRotating();
}
