package com.andrewtse.testdemo.rc_view.widget;

/**
 * @author xk
 * @date 2018/9/22
 */
public interface RcAttrs {

    void setClipBackground(boolean clipBackground);

    void setRoundAsCircle(boolean roundAsCircle);

    void setRadius(float radius);

    void setTopLeftRadius(float topLeftRadius);

    void setTopRightRadius(float topRightRadius);

    void setBottomLeftRadius(float bottomLeftRadius);

    void setBottomRightRadius(float bottomRightRadius);

    void setStrokeWidth(int strokeWidth);

    void setStrokeColor(int strokeColor);

    boolean isClipBackground();

    boolean isRoundAsCircle();

    float getRadius();

    float getTopLeftRadius();

    float getTopRightRadius();

    float getBottomLeftRadius();

    float getBottomRightRadius();

    int getStrokeWidth();

    int getStrokeColor();
}
